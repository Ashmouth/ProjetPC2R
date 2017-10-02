type t = char array array;;
type tirage = char list;;

let of_string str = 
    let a = Array.make_matrix Settings.taille_plateau Settings.taille_plateau '1' in
    let rec loop ind = function 
        | (i,j) when (i,j)= ((Settings.taille_plateau-1),Settings.taille_plateau) -> ()
        | (i, j) when j=Settings.taille_plateau -> loop ind (i+1, 0)
        | (i, j) -> 
            begin
            a.(i).(j) <- str.[ind]; loop (ind+1) (i, j+1)
        end
    in loop 0 (0,0);
    a
;;

let to_string plt = 
    let bytes = Bytes.init (Settings.taille_plateau*Settings.taille_plateau) (fun i -> plt.(i/Settings.taille_plateau).(i mod Settings.taille_plateau)) in
    Bytes.to_string bytes
;;

let rec ligne_words pl i lacc sacc =
    if i = Array.length pl then 
        if (String.length sacc) = 0 then lacc else sacc::lacc
    else
    match pl.(i) with 
    | '0' -> ligne_words pl (i+1) (if (String.length sacc) = 0 then lacc else sacc::lacc) ""
    | c -> 
        let cs = String.make 1 c in
        ligne_words pl (i+1) lacc (sacc ^ cs)
;;

let rec plateau_v_words pl col i lacc sacc =
    if i = Array.length pl then 
        if (String.length sacc) = 0 then lacc else sacc::lacc
    else
    match pl.(i).(col) with 
    | '0' -> plateau_v_words pl col (i+1) (if (String.length sacc) = 0 then lacc else sacc::lacc) ""
    | c -> 
        let cs = String.make 1 c in
        plateau_v_words pl col (i+1) lacc (sacc ^ cs)
;;

let words_of_plateau pl =
    let lw = ref [] in
    for i = 0 to (Settings.taille_plateau-1) do
        lw := !lw @ (ligne_words pl.(i) 0 [] "")
    done;
    for i = 0 to (Settings.taille_plateau-1) do
        lw := !lw @ (plateau_v_words pl i 0 [] "")
    done;
    List.filter (fun s -> (String.length s) > 1) !lw
;;

let tirer = function 
    | l when List.length l < 7 -> l 
    | a::b::c::d::e::f::g::l -> [a;b;c;d;e;f;g]
    | _ -> failwith "Ne peut pas arriver"
;;

let string_of_tirage l = 
    List.fold_left (fun acc c -> acc^(String.make 1 c)) "" l
;;

let empty () = 
    Array.init Settings.taille_plateau (fun _ -> Array.make Settings.taille_plateau '0')
;;

let score pl =
    let ws = words_of_plateau pl in
    List.fold_left (fun s w -> s + (String.length w)) 0 ws
;;

type move_possible = 
    | Possible of (int * char list) 
    | Not_possible of string
;;

exception Invalid_move of string;;

let is_valid_move pls pld pioche dic =
    let rec remove e l =
        match l with 
        | [] -> raise (Invalid_move "il manque des lettres!")
        | e'::l' when e = e' -> l'
        | e'::l' -> e'::(remove e l')
    in 
    let pi = ref pioche in
    try
        for i = 0 to (Settings.taille_plateau-1) do
            for j = 0 to (Settings.taille_plateau-1) do
                if pls.(i).(j) <> pld.(i).(j) 
                then begin
                    if pls.(i).(j) <> '0' then raise (Invalid_move "vous essayez de modifier une lettre déja placée!");
                    pi := remove pld.(i).(j) !pi
                end
            done
        done;
        let ws = words_of_plateau pld in
        let lnodic = List.filter (fun w -> not (Dict.has dic w)) ws in
        match lnodic with
        | [] -> Possible ((List.fold_left (fun s w -> s + (String.length w)) 0 ws), !pi)
        | [e] -> Not_possible (e ^" n'appartient pas au dictionnaire...")
        | l -> Not_possible ((List.fold_left (fun acc w -> acc ^ ", ") "" l) ^ "n'appartiennent pas au dictionnaire...")
    with Invalid_move str -> Not_possible str
;;

let copy p =
    Array.init Settings.taille_plateau (fun i -> Array.copy p.(i))
;;
    

(* pour tester : (* oui c'est très moche et alors? (* #commentaireImbriqué (*bon, ok, j'arrête, je devrais plutot me concentrer sur le boulot. (* après tout un scrabble ca se code pas tout seul... (* peut être un jour dans le futur? *) *) *) *) *) *)
  (*let old=  
[|
[| 'b'; '0'; '0'; '0'; '0'; 'p'; 'a'; 'r'; 'e'; '0'; '0'; '0'; 'n'; '0'; '0'|];
[| 'a'; '0'; '0'; 'l'; 'a'; 'i'; 't'; '0'; '0'; '0'; '0'; '0'; 'i'; '0'; '0'|];
[| 'n'; '0'; '0'; '0'; '0'; 's'; 't'; 'a'; 'b'; 'l'; 'e'; '0'; 'a'; '0'; '0'|];
[| 'a'; '0'; '0'; '0'; '0'; '0'; 'a'; '0'; '0'; '0'; '0'; '0'; 'i'; '0'; '0'|];
[| 'n'; '0'; '0'; '0'; '0'; '0'; 'q'; '0'; '0'; 'b'; 'o'; 'n'; 's'; '0'; '0'|];
[| 'e'; '0'; '0'; '0'; '0'; '0'; 'u'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; 'e'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'a'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'm'|];
[| '0'; 'c'; 'h'; 'i'; 'e'; 'n'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'o'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'u'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'r'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 's'|]
|];;

let ne=
[|
[| 'b'; '0'; '0'; '0'; '0'; 'p'; 'a'; 'r'; 'e'; '0'; '0'; '0'; 'n'; '0'; '0'|];
[| 'a'; '0'; '0'; 'l'; 'a'; 'i'; 't'; '0'; '0'; '0'; '0'; '0'; 'i'; '0'; '0'|];
[| 'n'; '0'; '0'; '0'; '0'; 's'; 't'; 'a'; 'b'; 'l'; 'e'; '0'; 'a'; '0'; '0'|];
[| 'a'; '0'; '0'; '0'; '0'; '0'; 'a'; '0'; '0'; '0'; '0'; '0'; 'i'; '0'; '0'|];
[| 'n'; '0'; '0'; '0'; '0'; '0'; 'q'; '0'; '0'; 'b'; 'o'; 'n'; 's'; '0'; '0'|];
[| 'e'; '0'; '0'; '0'; '0'; '0'; 'u'; 'n'; 'i'; '0'; '0'; '0'; '0'; '0'; '0'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; 'e'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; 's'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'a'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'm'|];
[| '0'; 'c'; 'h'; 'i'; 'e'; 'n'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'o'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'u'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 'r'|];
[| '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; '0'; 's'|]
|];;

let d = Dict.from_file "dico.txt";;

if (is_valid_move old ne ['u';'s';'i';'n';'e'] d) then print_string "TRUE" else print_string "FALSE";;
print_newline();;*)

(**)
