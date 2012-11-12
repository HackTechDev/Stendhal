#!/bin/sh

STENDHAL_VERSION="1.04"

java -cp ./lib/stendhal-server-$STENDHAL_VERSION.jar games.stendhal.server.core.engine.GenerateINI
