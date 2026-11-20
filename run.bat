@echo off
rem double click this file to run the project
cd /d "%~dp0"

javac -cp "sqlite-jdbc-3.47.1.0.jar" *.java
if errorlevel 1 (
    echo Compile failed. Check that JDK is installed.
    pause
    exit /b 1
)

java -cp ".;sqlite-jdbc-3.47.1.0.jar" Login
if errorlevel 1 pause
