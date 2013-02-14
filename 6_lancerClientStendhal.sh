#!/bin/sh

echo Client Stendhal

cd ./Client
xterm -title ClientStendhal -e ./lancerClient.sh &
cd ..
