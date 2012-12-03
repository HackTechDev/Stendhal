#!/bin/sh

echo Arret Serveur Stendhal

kill -9 `ps ax | grep stendhal-server | grep -v grep | awk '{print $1}'`
