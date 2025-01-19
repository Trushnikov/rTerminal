#!/bin/bash
rm rTerminal-1.0.dmg
mv ../../target/terminal-x-jar-with-dependencies.jar ../../target/app.jar
jpackage --name rTerminal --main-jar app.jar --type dmg --input ../../target/ --icon ../../icon.icns

