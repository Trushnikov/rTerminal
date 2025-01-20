#!/bin/bash
rm rTerminal-1.0.app
mv ../../target/terminal-x-jar-with-dependencies.jar ../../target/app.jar 
jpackage --name rTerminal --main-jar app.jar --type app-image --input ../../target/ --icon ../../icon.icns

