# Rock Paper Scissors Tournament (JavaFX)

## Project Overview
This project is developed as the Final Project for the Programming II course. It is a desktop application built with JavaFX that implements a tournament-style Rock-Paper-Scissors game.

## Features Developed Based on Requirements
* **Authentication (10 pts):** Users must register and log in to play. Passwords and usernames are saved locally using File I/O.
* **File Processing (10 pts):** * User credentials are saved to `users.csv`.
  * Final tournament match results and scores are written to `scores.csv`.
* **Basic Functions (50 pts):** A fully functioning, interactive GUI built with JavaFX (`VBox`, `HBox`, `Button`, `Label`, `Scene`). Includes tournament logic where users set a target score.

## Installation & Execution Procedure
1. Clone this repository to your local machine:
   `git clone https://github.com/yusuftutal772/RockPaperScissors-JavaFX.git`
2. Open the project folder in **IntelliJ IDEA** (or any IDE that supports JavaFX and Maven).
3. Ensure that your IDE is configured with JDK 23 (as specified in the `pom.xml`).
4. Maven will automatically resolve the required JavaFX dependencies.
5. Navigate to `src/main/java/com/example/rockpaperscciorsgame/HelloApplication.java`.
6. Run the `main` method to launch the application.

## How to Play
1. **Register:** Create a new username and password.
2. **Login:** Log in with your registered credentials.
3. **Set Target:** Enter the target score to win the tournament.
4. **Play:** Click the Stone, Paper, or Scissors buttons to compete against the computer.
5. **Score Log:** Once the tournament finishes, check your project folder for the `scores.csv` file to see your saved match history.
