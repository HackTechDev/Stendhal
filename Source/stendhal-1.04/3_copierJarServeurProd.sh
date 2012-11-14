#!/bin/sh

STENDHAL_VERSION="1.04"
echo Copie vers Serveur

cp ../../Serveur/server.ini  ../../../PROD/Serveur/
cp -f build/stendhal-server-$STENDHAL_VERSION.zip ../../../PROD/Serveur/
cd ../../../PROD/Serveur/
unzip -o stendhal-server-$STENDHAL_VERSION.zip
