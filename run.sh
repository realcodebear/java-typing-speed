#!/bin/sh
# Compile and run the Typing Speed Test on Mac / Linux:  sh run.sh
cd "$(dirname "$0")"
javac -cp lib/sqlite-jdbc-3.47.1.0.jar *.java && java -cp .:lib/sqlite-jdbc-3.47.1.0.jar Login
