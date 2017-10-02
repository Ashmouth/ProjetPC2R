type phase = DebutSess | Recherche | Soumission | Result ;;

(* represente un commande qu'un thread de connexion avec un client peut
   envoyer au thread "principal" pour qu'il gère 
 *)

 
type serv_cmd = 
    | MsgBroadcast of string*string (* (msg, sender) -> envoie d'un msg à tous les joueurs *)
    | StateChange (* on passe d'une phase à une autre *)
    | Connected of string (* on prévient qu'un user s'est connecte *)
    | Exited of string (* on prévient qu'un user s'est déco *)
    | SessionBeg (* debut d'une nouvelle session *) 
    | FinSession (* annoncer les scores *)
    | DebutTour (* envoi du plateau et du tirage *)
    | QqnTrouve of string (* annonce que usr a trouvé qqch, fin de la phase de recherche *)
    | FinRech (* countdown de recherche terminé, début de la phase de soumission *)
    | FinSoum
;;

let output str chan =
    output_string chan str;
    flush chan (* evite les problemes de buffering d'ocaml *)
;;

(* crée un socket pret à accepter une connexion *)
let create_listen_socket port nlstn =
    let open Unix in
    let sock = socket PF_INET SOCK_STREAM 0 in
    setsockopt sock SO_REUSEADDR true;
    bind sock (ADDR_INET (inet_addr_of_string "0.0.0.0", port));
    listen sock nlstn;
    sock
;;

type connexion = {
    cout : out_channel;
    cin : in_channel;

    mutable pseudo : string;
    mutable score : int;
    mutable plateau : Plateau.t; (*meilleur plateau du joueur *)
    mutable l_restantes : char list (* liste restante avec ce meilleur plateau *)
}

type s = {
    mutable plateau : Plateau.t;
    mutable reservoir : Reservoir.t;
    mutable tirage : char list;

    plat_mut : Rwl.t;
    
    dict : Dict.t;

    mutable phase : phase;
    mutable phase_beg : float;

    mutable lcon : connexion list;
    lcon_mut : Rwl.t
};;

let s = 
    let reser = Reservoir.create_random Settings.taille_reservoir in
    {
    plateau = Plateau.empty ();
    reservoir = reser;
    tirage = Reservoir.tirage reser;

    plat_mut = Rwl.create ();
    
    dict = Dict.from_file "dico.txt";

    phase = DebutSess;
    phase_beg = Unix.time();

    lcon = [];
    lcon_mut = Rwl.create()
};;

type client_cmd = 
    | Connexion of string (* user se connecte *)
    | Sort of string (* user se déconnecte *)
    | Trouve of Plateau.t (* un joueur a proposé un plateau *)
    | Message of string
    | PMessage of string*string
    | Invalid of string
;;

(* sépare une chaine de caractère
    note : si le dernier caractère est un séparateur, une chaine vide sera dans la liste (mais pas le premier caractère)
    ex : /hello/world/ --> ["hello", "world", ""]
 *)
let split_cmd str sep =
    let rec aux i accToks accStr =
        match (i, str.[i]) with
        | (0, c) when c = sep -> accStr::accToks
        | (0, c) -> ((String.make 1 c)^accStr)::accToks
        | (i, c) when c = sep -> aux (i-1) (accStr::accToks) ""
        | (i, c) -> aux (i-1) accToks ((String.make 1 c)^accStr)
    in aux ((String.length str)-1) [] ""
;;

(* lit une commande depuis un in_channel [a priori ici un socket]*)
let rec read_cmd_from_chan c =
    let line = input_line c in
    let spt = split_cmd line '/' in
    match spt with
    | ["CONNEXION"; pseudo; ""] -> Connexion pseudo
    | ["SORT"; pseudo; ""] -> Sort pseudo
    | ["TROUVE"; plateau; ""] when (String.length plateau) = 225 -> Trouve (Plateau.of_string plateau)
    | ["ENVOI"; msg; ""] -> Message msg
    | ["PENVOI"; dst; msg; ""] -> PMessage (msg, dst)
    | _ -> Invalid line
;;

exception Deconnexion;;

let add_connexion (cin,cout) =
    let co = {cout = cout; cin = cin; pseudo = ""; score = 0; plateau = Plateau.empty(); l_restantes =[]} in
    Rwl.write_lock s.lcon_mut;
    s.lcon <- co :: s.lcon;
    Rwl.write_unlock s.lcon_mut;
    co
;;

(* envoie msg à tout le monde *)
let broadcast_all msg = 
    Rwl.read_lock s.lcon_mut;
    List.iter (fun {cout=cout; _} -> output msg cout) s.lcon;
    Rwl.read_unlock s.lcon_mut
;;

(* envoie msg à tout le monde sauf co *)
let broadcast_e msg co =
    Rwl.read_lock s.lcon_mut;
    List.iter (fun x -> if x <> co then output msg x.cout) s.lcon;
    Rwl.read_unlock s.lcon_mut
;;

(* deconnecte completmeent un utilisateur *)
let logout co =
    Rwl.write_lock s.lcon_mut;
    s.lcon <- List.filter (fun x -> x <> co) s.lcon;
    Rwl.write_unlock s.lcon_mut;
;;

(* associe un pseudo à une connexion,
 retourne true si OK,
 false si le pseudo est déjà pris ou vide *)
let set_pseudo co p =
    if p = "" then false 
    else begin
        Rwl.write_lock s.lcon_mut;
        let taken = List.exists (fun x -> x.pseudo = p) s.lcon in
        let ret =
            if taken 
            then false 
            else (List.iter (fun x -> if x = co then x.pseudo <- p) s.lcon; true) in
        Rwl.write_unlock s.lcon_mut;
        ret
    end
;;

let string_of_scores () = 
    let one {score=score; pseudo=pseudo; _} =
        ("*"^pseudo^"*"^(string_of_int score))
    in
    let str = List.fold_left (fun acc co -> if co.pseudo <> "" then acc^(one co) else acc) "" s.lcon in
    (string_of_int (List.length s.lcon))^str
;;

let string_of_phase = function
| DebutSess -> "DEB"
| Recherche -> "REC"
| Soumission-> "SOU"
| Result    -> "RES"
;;

let temps_restant () = 
    let elapsed = int_of_float (Unix.time() -. s.phase_beg) in 
    (match s.phase with
    | DebutSess -> Settings.temps_debut
    | Recherche -> Settings.temps_recherche
    | Soumission-> Settings.temps_soumission
    | Result    -> Settings.temps_resultats) 
    - elapsed
;;

(* retourne un message de bienveue *)
let get_bienvenue () =
    Rwl.read_lock s.lcon_mut;
    Rwl.read_lock s.plat_mut;

    let str = ("BIENVENUE/" ^
    (Plateau.to_string s.plateau) ^ "/" ^
    (List.fold_left (fun acc c -> acc^(String.make 1 c)) "" s.tirage) ^ "/" ^
    (string_of_scores ()) ^"/" ^
    (string_of_phase s.phase) ^ "/" ^
    (string_of_int (temps_restant ())) ^ "/\n") in

    Rwl.read_unlock s.plat_mut;
    Rwl.read_unlock s.lcon_mut;
    str
;;

let set_co_placement (co:connexion) p l_rest =
    Rwl.write_lock s.lcon_mut;
    List.iter (fun x -> if x = co then x.plateau <- p; x.l_restantes <- l_rest) s.lcon;
    Rwl.write_unlock s.lcon_mut;
;;

let reset_session () = 
    begin
    Rwl.write_lock s.lcon_mut;
    Rwl.write_lock s.plat_mut;
    
    s.phase <- DebutSess;
    s.phase_beg <- Unix.time();
    ignore(Unix.alarm Settings.temps_debut);

    s.plateau <- Plateau.empty();
    s.reservoir <- Reservoir.create_random Settings.taille_reservoir;
    s.tirage <- Reservoir.tirage s.reservoir;

    List.iter (fun co -> (co.score <- 0; co.plateau <- Plateau.empty())) s.lcon;

    Rwl.write_unlock s.lcon_mut;
    Rwl.write_unlock s.plat_mut;

    broadcast_all "SESSION/\n";
    end
;;


let nouveau_tour () =begin
    Rwl.write_lock s.lcon_mut;
    Rwl.write_lock s.plat_mut;
    
    s.phase <- Recherche;
    ignore(Unix.alarm Settings.temps_recherche);
    s.phase_beg <- Unix.time();

    s.tirage <- Reservoir.tirage s.reservoir;

    List.iter (fun co -> (co.score <- 0; co.plateau <- Plateau.empty())) s.lcon;

    let tourstr = 
        "TOUR/"
        ^(Plateau.to_string s.plateau)^"/"
        ^(List.fold_left (fun acc c -> acc^(String.make 1 c)) "" s.tirage) ^ "/\n"
    in 
    Rwl.write_unlock s.lcon_mut;
    Rwl.write_unlock s.plat_mut;
    broadcast_all tourstr
end;;

let next_phase _ = 
    Rwl.read_lock s.lcon_mut;
    Rwl.read_lock s.plat_mut;

    (match s.phase with
    | DebutSess -> begin (* on passe à la recherche *)
        s.phase <- Recherche;
        ignore(Unix.alarm Settings.temps_recherche);
        output "on passe à la recherche\n" stdout;

        Rwl.read_unlock s.plat_mut;
        Rwl.read_unlock s.lcon_mut;
    end
    | Recherche -> begin (* on passe à la soumission *)
        
        s.phase <- Soumission;
        ignore(Unix.alarm Settings.temps_soumission);
        output "on passe à la soumission\n" stdout;

        Rwl.read_unlock s.plat_mut;
        Rwl.read_unlock s.lcon_mut;
        (* notify *)
        broadcast_all "RFIN/\n";
    end 
    | Soumission -> begin (* on passe aux résutats *)

        (* on update le plateau du serveur en prenant le meiller plateau proposé +
            on met à jour les scores de chacun *)
        let servscore = ref (Plateau.score s.plateau) in
        let plat = ref s.plateau in
        let tir = ref s.tirage in
        let vainqueur = ref "" in
        List.iter (fun (co:connexion) ->
            let sc = Plateau.score co.plateau in
            if sc > !servscore then (servscore := sc; plat := co.plateau; tir := co.l_restantes; vainqueur := co.pseudo);
            co.score <- (co.score + sc);
            output ((string_of_int co.score)^"\n") stdout
        ) s.lcon;
        s.plateau <- Plateau.copy !plat;

        (* on met à jour le reservoir en remettant les lettres *)
        Reservoir.remise !tir s.reservoir;

        s.phase <- Result;
        ignore(Unix.alarm Settings.temps_resultats);
        output "on passe aux résultats\n" stdout;

        Rwl.read_unlock s.plat_mut;
        Rwl.read_unlock s.lcon_mut;

        (* notify *)
        broadcast_all "SFIN/\n";
        broadcast_all ("BILAN/(??)/"^(!vainqueur)^"/"^(string_of_scores ())^"/\n");
    end
    | Result -> begin (* retour en recherche *)
        Rwl.read_unlock s.plat_mut;
        Rwl.read_unlock s.lcon_mut;

        output "on passe à la recherche\n" stdout;
        nouveau_tour ()
    end);
    s.phase_beg <- Unix.time()
;;

(* fonction du thread associé à chaque connexion *)
let connection_fun co = 
    let rec loop () = 
        let cmd = read_cmd_from_chan co.cin in
        (match cmd with
        | Connexion p -> begin

            output ( "Demande de cnnexion du joueur '"^p^"'\n" ) stdout;

            if set_pseudo co p then begin (* si pseudo OK *)
                output ("connexion ok pour '"^p^"'\n") stdout;
                broadcast_e ("CONNECTE/"^p^"/\n") co;
                output (get_bienvenue ()) co.cout
            end 
            else begin
                output ("connexion nop pour '"^p^"'\n") stdout;
                output "REFUS/\n" co.cout
            end

        end    
        | Sort p -> begin
            output ( "Demande de deconnexion du joueur '"^p^"'\n" ) stdout;

            (if co.pseudo = p then begin (* deconnexion ok *)
                raise Deconnexion
            end
            else (* deconnexion pas ok *)
                output ("impossible de deconnecter '"^p^"' depuis '"^co.pseudo ^"'\n") stdout
            );

        end
        | Trouve pl -> begin
            output ("Recu soumission de "^co.pseudo^"\n") stdout;
            if s.phase = DebutSess || s.phase = Result 
                then output "SINVALIDE/impossible de soumettre pendant cette phase de jeu.../\n" co.cout
            else (
                match Plateau.is_valid_move s.plateau pl s.tirage s.dict with
                | Plateau.Not_possible raison -> begin
                    output ((if s.phase = Recherche then "R" else "S")^"INVALIDE/"^raison^"/\n") co.cout;
                    output ("Soumission de "^co.pseudo^" invalide\n") stdout;
                    end
                | Plateau.Possible (score, l_rest) ->
                    if s.phase = Recherche then (
                        next_phase ();
                        broadcast_all ("RATROUVE/"^co.pseudo^"/\n")
                    );
                    let curscore = Plateau.score co.plateau in
                    if score > curscore then begin
                        set_co_placement co pl l_rest;
                        output ((if s.phase = Recherche then "R" else "S")^"VALIDE/\n") co.cout;
                        output ("Soumission de "^co.pseudo^" validée!\n") stdout;
                    end
                    else begin
                        output ((if s.phase = Recherche then "R" else "S")^"INVALIDE/ce placement rapporte moins de points que votre précédent placement/\n") co.cout;
                        output ("Soumission de "^co.pseudo^" inutile!\n") stdout;
                    end
                    
            )
        end
        | Message m -> begin
            broadcast_all ("RECEPTION/"^co.pseudo^" : "^m^"/\n")
        end
        | PMessage (m, d) -> begin
            output (co.pseudo ^ " -> " ^ d ^ " : " ^m^"\n") stdout;
            List.iter (fun c -> if c.pseudo = d then output ("PRECEPTION/"^m^"/"^co.pseudo^"/\n") c.cout) s.lcon
        end
        | Invalid c -> 
            output ("Invalid : "^c^"\n") stdout
        ); 
        loop ()
    in try loop () 
    with _ -> begin
        output ("deconnexion de '"^co.pseudo^"'\n") stdout;
        if co.pseudo <> "" then broadcast_e ("DECONNEXION/"^co.pseudo^"/\n") co;
        logout co
    end

;;

(* lance le thread handle de connexion + transforme le sock en chan + ajoute le out_sock à la liste des out_chans *)
let create_co_thread sock = 
    let cin = Unix.in_channel_of_descr sock in
    let cout = Unix.out_channel_of_descr sock in
    let co = add_connexion (cin, cout) in
    ignore(Thread.create connection_fun co)
;;

(**** MAIN ****)
let _ = 
    Random.self_init ();
    Sys.set_signal Sys.sigalrm (Sys.Signal_handle next_phase);
    reset_session ();
    let sock = create_listen_socket Settings.server_port Settings.server_listen_cnt in
    output "Le serveur ecoute sur le port 2017..." stdout;
    while true do
        try 
            let (sck, _) = Unix.accept sock in
            create_co_thread sck
        with Unix.Unix_error (Unix.EINTR, "accept", _) -> ()
    done;;