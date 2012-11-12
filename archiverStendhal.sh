#!/bin/sh

STENDHALVERSION="104"

DATE=`date +"%y%m%d"`
tar cvfz STENDHAL_$STENDHALVERSION-$DATE.tar.gz DEVEL

