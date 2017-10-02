(* module Dict *)

type t;;

val from_file : string -> t;;

val has : t -> string -> bool;;