#!/bin/sh

STENDHAL_VERSION="1.04"

echo Copie vers Client

cp build/stendhal-$STENDHAL_VERSION.zip ../../../PROD/Client
cd ../../../PROD/Client
unzip stendhal-$STENDHAL_VERSION.zip
rm stendhal-$STENDHAL_VERSION.zip
