type t;;
type move_possible = 
    | Possible of (int * char list)
    | Not_possible of string
;;
val of_string : string -> t;;
val to_string : t -> string;;
val words_of_plateau : t -> string list;;
val empty : unit -> t;;
val score : t -> int;;
val is_valid_move : t -> t -> char list -> Dict.t -> move_possible;;
val copy : t -> t;;