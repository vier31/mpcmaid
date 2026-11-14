#!/bin/sh
# Builds build/mpcmaid.jar from MpcMaid/src with the installed JDK (11+).
set -e
cd "$(dirname "$0")"
rm -rf build && mkdir -p build/classes
javac --release 11 -encoding ISO-8859-1 -nowarn -d build/classes $(find MpcMaid/src -name '*.java')
(cd MpcMaid/src && find . -type f ! -name '*.java' -exec cp {} ../../build/classes/{} \;)
jar --create --file build/mpcmaid.jar --main-class MPCMaid -C build/classes .
echo "built build/mpcmaid.jar"
