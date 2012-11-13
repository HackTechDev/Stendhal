#!/bin/sh

STENDHAL_VERSION="1.04"
echo Copie vers Serveur

cp build/stendhal-server-$STENDHAL_VERSION.zip ../../../PROD/Serveur/
cd ../../../PROD/Serveur/
unzip stendhal-server-$STENDHAL_VERSION.zip
