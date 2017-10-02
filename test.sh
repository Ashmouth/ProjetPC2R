exec 3<>/dev/tcp/localhost/2017
echo "/CONNEXION/$1/" >&3
cat <&3

sleep 3

echo "/ENVOI/salut tlm!/" >&3
cat <&3