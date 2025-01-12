#!/bin/bash
rm rTerminal.dmg
mv ../../target/terminal-0.0.1-jar-with-dependencies.jar ../../target/app.jar
jpackage --name rTerminal --main-jar app.jar --type dmg --input ../../target/ --icon ../../icon.icns

