type t;;

val create_random : int -> t
val tirage : t -> char list
val is_empty : t -> bool
val remise : char list -> t -> unit