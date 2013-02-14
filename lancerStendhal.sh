#!/bin/sh

echo Arret serveur Lampp Local
./1_arreterServeurLocal.sh

echo Initialisation serveur lampp interne
./2_initialiserServeurLampp.sh 

echo Modification permission serveur Lampp
./3_modifierPermission.sh

echo Lancement serveur Lampp interne
./4_lancerServeurLampp.sh

echo Lancement serveur Stendhal
./5_lancerServeurStendhal.sh

echo Lancement client Stendhal
./6_lancerClientStendhal.sh

echo Lancement site Stendhal
./7_lancerSite.sh

while read -p "Arrêter le serveur Lampp
Choix (o) ou (q)uitter :" choix ; do
        case $choix in
                q|Q)
			echo Arreter les serveurs Lampp interne et Stendhal manuellement
                        exit
			;;
                o|O)
			echo Arrete serveur Stendhal
			./8_arreterServeurStendhal.sh
			echo Arret serveur Lampp interne
			./9_arreterServeurLampp.sh
			exit
			;;
                *)
                        echo "Choix non valide"
        esac
done
