comp=ocamlc
opt=-thread

if [ $# -gt 0 ] && [ $1 = "clean" ]; then
    echo cleaning...
    rm *.cm[iox] *.o
    exit
fi

$comp $opt server.mli
$comp $opt connexion.mli
$comp $opt dict.mli
$comp $opt plateau.mli

$comp -c $opt connexion.ml
$comp -c $opt dict.ml
$comp -c $opt plateau.ml
$comp -c $opt server.ml

$comp -o serv.exe connexion.o server.o plateau.o dict.o