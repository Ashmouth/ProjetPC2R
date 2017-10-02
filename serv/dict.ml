(* module Dict *)

type t = (bytes, bool) Hashtbl.t;;

let from_file f =
    let tab = Hashtbl.create 10000 in
    let chan = open_in f in 
    let rec loop () =
        match input_line chan with
        | exception _ -> ()
        | word -> (Hashtbl.add tab word true; loop ())
    in
    loop ();
    close_in chan;
    tab
;;

let has3 dic w = 
    let rec loop min max =
        let i = (max+min)/2 in
        if min = max then (dic.(i) = w)
        else 
            if w = dic.(i) then true
            else if w < dic.(i) then loop min ((max+min)/2)
            else loop ((max+min)/2) max
    in loop 0 ((Array.length dic)-1)
;;

let rec has2 dic w =
    let rec loop min max =
    if max = min then
        dic.(min) = w
    else 
        let mid = (min + max) / 2 in
        if dic.(mid) > w then
        loop min (mid - 1)
        else if dic.(mid) < w then
        loop (mid + 1) max
        else
        true
    in loop 0 ((Array.length dic)-1)
;;

let has dic w =
    Hashtbl.mem dic (String.lowercase w)
;;

(*let a = from_file "dico.txt" ;;
while true do
    let w = read_line () in
    print_string (string_of_bool (has a w));
    (*print_string (string_of_bool (has3 a w));*)
    print_newline()
done;;*)