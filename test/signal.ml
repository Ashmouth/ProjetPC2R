ignore (Unix.alarm 1);;

Sys.set_signal Sys.sigalrm (Sys.Signal_handle (fun x ->
    print_int x;
    print_newline()
));;

ignore (Unix.alarm 3);;
Unix.sleep 66;;