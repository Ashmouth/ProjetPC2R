type t = {
    r:Mutex.t;
    w:Mutex.t;
    mutable b:int
};;

open Mutex;;

let create () =
    {r = Mutex.create ();
     w = Mutex.create ();
     b = 0}
;;

let write_lock m =
    lock m.w
;;

let write_unlock m =
    unlock m.w
;;

let read_lock m =
    lock m.r;
    m.b <- (m.b+1);
    if m.b = 1 then lock m.w;
    unlock m.r
;;

let read_unlock m = 
    lock m.r;
    m.b <- (m.b-1);
    if m.b = 0 then unlock m.w;
    unlock m.r
;;

let l = create();;

let lec () = 
    read_lock l;
    print_string "lec";
    print_newline();
    Unix.sleep 2;
    read_unlock l
;;

let wri () = 
    write_lock l;
    print_string "wri";
    print_newline();
    Unix.sleep 3;
    write_unlock l
;;

(*let _ =
    let a = Array.init 20 (fun i -> Thread.create (if i >= 17 then wri else lec) ()) in
    Array.iter Thread.join
;;*)
