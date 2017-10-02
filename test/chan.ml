open Event;;
open Printf;;

let print s =
    print_string s;
    print_newline()
;;

let ft chan = 
    let rec loop () =
        print "waiting for event";
        let st = sync (receive chan) in
        print ((string_of_int (Thread.id (Thread.self())))^"received "^(string_of_int st));
        Unix.sleep 1;
        loop ()
    in loop ()
;;

let cons (chan,id) =
    let rec loop () =
        sync (send chan id);
        loop ()
    in loop ();;


let c = new_channel () in
let t1 = Thread.create ft c in
let t2 = Thread.create cons (c, 1) in
let t3 = Thread.create cons (c, 2) in
let t4 = Thread.create cons (c, 3) in
printf "waiting for threads\n";
Thread.join t1;
Thread.join t4;
Thread.join t3;
Thread.join t2;;