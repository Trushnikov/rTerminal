#!/bin/bash
rm -Rf rTerminal
mv ../../target/terminal-0.0.1-jar-with-dependencies.jar ../../target/app.jar 
jpackage --name rTerminal --main-jar app.jar --type app-image --input ../../target/ --icon ../../icon.ico
