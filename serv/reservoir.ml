type t = {b:bytes; mutable n:int};;

(* ici on peut s'amuser un peu, utiliser des lois de probabilités différentes, enlever certaines lettres, etc... :D 
    suffit de modifier cette fonction

    ici, on prend la distribution standard du scrabble 
    http://www.regles-de-jeux.com/regle-du-scrabble/

    généré par : (javascript)

    var al = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    var cn = [9,2,2,3,15,2,2,2,8,1,1,5,3,6,6,2,1,6,6,6,6,2,1,1,1,1];
    var r = "";
    var b = 0;
    for(var i in al) {
        for(var j = 0; j < cn[i]; j++) {
            r += " | "+(b++);
        }
        r += " -> '"+al[i]+"'";
    }
*)
let random_char () = 
    let r = Random.int 100 in
    match r with
     | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 -> 'A' | 9 | 10 -> 'B' | 11 | 12 -> 'C' | 13 | 14 | 15 -> 'D' | 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 | 24 | 25 | 26 | 27 | 28 | 29 | 30 -> 'E' | 31 | 32 -> 'F' | 33 | 34 -> 'G' | 35 | 36 -> 'H' | 37 | 38 | 39 | 40 | 41 | 42 | 43 | 44 -> 'I' | 45 -> 'J' | 46 -> 'K' | 47 | 48 | 49 | 50 | 51 -> 'L' | 52 | 53 | 54 -> 'M' | 55 | 56 | 57 | 58 | 59 | 60 -> 'N' | 61 | 62 | 63 | 64 | 65 | 66 -> 'O' | 67 | 68 -> 'P' | 69 -> 'Q' | 70 | 71 | 72 | 73 | 74 | 75 -> 'R' | 76 | 77 | 78 | 79 | 80 | 81 -> 'S' | 82 | 83 | 84 | 85 | 86 | 87 -> 'T' | 88 | 89 | 90 | 91 | 92 | 93 -> 'U' | 94 | 95 -> 'V' | 96 -> 'W' | 97 -> 'X' | 98 -> 'Y' | 99 -> 'Z' | _ -> failwith "gen random plat"
;;

let create_random t =
    let r = Bytes.init t (fun _ -> random_char ()) in
    {b=r; n = t}
;;

let rec remise l res =
    match l with 
    | [] -> ()
    | e::l' -> begin
        Bytes.set res.b res.n e;
        res.n <- res.n +1;
        remise l' res
    end
;;

let une_lettre res =
    if res.n = 0 then failwith "EMPTY UNE LETTRE"
    else (
        let r = Random.int res.n in
        let l = res.b.[r] in
        res.n <- res.n -1;
        Bytes.set res.b r res.b.[res.n];
        l
    )
;;     

let tirage res =
    let rec loop = function
    | 0 -> []
    | i -> (une_lettre res)::(loop (i-1))
    in loop (min res.n 7)
;;

let is_empty res = (res.n = 0)
;;