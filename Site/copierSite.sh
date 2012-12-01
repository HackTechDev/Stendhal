#!/bin/sh

cp -r stendhal_website ../../PROD/Site
cd ../../PROD/Lamp/lampp/htdocs/
rm stendhal
ln -s ../../../Site/stendhal_website/ stendhal
