#!/bin/sh

STENDHAL_VERSION="1.07"

echo Copie vers Client

cp -f build/stendhal-$STENDHAL_VERSION.zip ../../../PROD/Client
cd ../../../PROD/Client
unzip -o stendhal-$STENDHAL_VERSION.zip
rm stendhal-$STENDHAL_VERSION.zip
