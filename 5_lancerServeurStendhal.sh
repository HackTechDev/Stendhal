#!/bin/sh

echo Serveur Stendhal

cd ./Serveur/
xterm -title ServeurStendhal -e ./lancerServeur.sh &
cd ..
