@echo off
rem Double-click this file to compile and run the Typing Speed Test on Windows.

rem go to the folder this file is in
cd /d "%~dp0"

if not exist "lib\sqlite-jdbc-3.47.1.0.jar" (
    echo The file lib\sqlite-jdbc-3.47.1.0.jar is missing.
    echo Download the whole project again, including the lib folder.
    pause
    exit /b 1
)

echo Compiling...
javac -cp "lib\sqlite-jdbc-3.47.1.0.jar" *.java
if errorlevel 1 (
    echo.
    echo Compiling failed. Make sure a JDK is installed and javac works.
    pause
    exit /b 1
)

echo Starting...
java -cp ".;lib\sqlite-jdbc-3.47.1.0.jar" Login
if errorlevel 1 pause
