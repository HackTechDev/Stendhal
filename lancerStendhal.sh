#!/bin/sh

./1_arreterServeurLocal.sh 
./2_initialiserServeurLampp.sh
./3_modifierPermission.sh
./4_lancerServeurLampp.sh
./terminalStendhalServeur.sh &
./terminalStendhalClient.sh &

echo -n "Arrêter Serveur Stendhal (O)ui/(Q)uitter : "
read reponse
case $reponse in
        [oO] )
                echo "Arrêt automatique du serveyr Stendhal"
                ;;

        [qQ] )
                echo "Arrêter manuellement le serveur Stendhal";
                exit 1
                ;;
        *) echo "Invalid input"
            ;;
esac

./8_arreterServeurStendhal.sh 
./9_arreterServeurLampp.sh
