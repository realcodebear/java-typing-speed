# Typing Speed Test

My Java project. It has a login and register page and a typing speed test.
It shows time, speed (WPM) and accuracy, and saves the scores in a SQLite database using JDBC.
The leaderboard shows the top 10 scores.

Made with Java Swing and JDBC.

## How to run

**Windows**

Double click `run.bat`

or run these commands in this folder:

```
javac -cp "sqlite-jdbc-3.47.1.0.jar" *.java
java -cp ".;sqlite-jdbc-3.47.1.0.jar" Login
```

You can also just open `TypingSpeedTest.exe` (Java must be installed).

**Mac / Linux**

```
javac -cp sqlite-jdbc-3.47.1.0.jar *.java
java -cp .:sqlite-jdbc-3.47.1.0.jar Login
```

## Files

- `Login.java` - login and register window (main method is here)
- `TypingTest.java` - typing test, timer and leaderboard
- `DB.java` - database connection and tables
- `sqlite-jdbc-3.47.1.0.jar` - SQLite driver (needed for JDBC)
- `run.bat` - runs the project on Windows
- `TypingSpeedTest.exe` - Windows app

## How it works

- WPM = (letters typed / 5) / minutes
- Accuracy = correct letters * 100 / letters typed
- Text turns red when you make a mistake
- Test ends when you type as many letters as the sentence
