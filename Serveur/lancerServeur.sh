#!/bin/sh

STENDHAL_VERSION="1.04"

#LOCALCLASSPATH=.:data/script/:data/conf/:lib/stendhal-server-$STENDHAL_VERSION.jar:lib/marauroa.jar:lib/mysql-connector.jar:lib/log4j.jar:lib/commons-lang.jar:lib/h2.jar
LOCALCLASSPATH=.:data/script/:data/conf/:stendhal-server-$STENDHAL_VERSION.jar:marauroa.jar:lib/mysql-connector.jar:lib/log4j.jar:lib/commons-lang.jar:lib/h2.jar

java -Xmx400m -cp "${LOCALCLASSPATH}" games.stendhal.server.StendhalServer -c server.ini -l

