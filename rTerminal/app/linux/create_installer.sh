#!/bin/bash
rm rterminal_1.0_amd64.deb
mv ../../target/rTerminal-0.0.1-jar-with-dependencies.jar ../../target/app.jar
jpackage --name rTerminal --main-jar app.jar --type deb --input ../../target/ --icon ../../icon.png
