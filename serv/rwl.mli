type t;;

val create : unit -> t
val write_lock : t -> unit
val write_unlock : t -> unit
val read_lock : t -> unit
val read_unlock : t -> unit