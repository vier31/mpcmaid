#!/bin/sh
# Builds build/mpcmaid.jar from MpcMaid/src with the installed JDK (11+).
# The jars in lib/ are bundled into it, so the result runs on its own.
set -e
cd "$(dirname "$0")"
rm -rf build && mkdir -p build/classes
CP=$(printf '%s:' lib/*.jar)
javac --release 11 -encoding ISO-8859-1 -nowarn -cp "$CP" -d build/classes $(find MpcMaid/src -name '*.java')
(cd MpcMaid/src && find . -type f ! -name '*.java' -exec cp {} ../../build/classes/{} \;)
for lib in lib/*.jar; do
	(cd build/classes && unzip -qo "../../$lib" -x 'META-INF/MANIFEST.MF')
done
# FlatLaf ships Java 9+ variants of some classes under META-INF/versions
printf 'Main-Class: MPCMaid\nMulti-Release: true\nEnable-Native-Access: ALL-UNNAMED\n' > build/manifest.txt
jar --create --file build/mpcmaid.jar --manifest build/manifest.txt -C build/classes .
echo "built build/mpcmaid.jar"
