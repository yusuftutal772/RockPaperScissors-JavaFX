package com.example.rockpaperscciorsgame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class HelloApplication extends Application {

    private Stage window;
    private Scene loginScene, gameScene;
    private String loggedInUser = ""; // Keeps track of the current user for scoring

    // UI Elements for the Game Scene
    private Label resultLabel, choicesLabel, scoreLabel, welcomeLabel;
    private TextField targetInput;
    private Button btnStart, btnRock, btnPaper, btnScissors, btnLogout;
    private Random random = new Random();

    // Tournament variables
    private int targetScore = 0, currentRound = 0, userWins = 0, computerWins = 0;

    // Core Game Logic: -1 for draw, 1 for user win, 0 for computer win
    public static int game(char you, char computer) {
        if (you == computer) return -1;
        else if ((you == 'R' && computer == 'S') || (you == 'P' && computer == 'R') || (you == 'S' && computer == 'P')) return 1;
        else return 0;
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;

        // Initialize both scenes
        createLoginScene();
        createGameScene();

        window.setTitle("Rock Paper Scissors - Authentication");
        // Application starts with the login screen
        window.setScene(loginScene);
        window.show();
    }

    // ==========================================
    // 1. AUTHENTICATION & REGISTRATION SCENE
    // ==========================================
    private void createLoginScene() {
        Label titleLabel = new Label("Welcome! Please Login or Register");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(200);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(200);

        Button btnLogin = new Button("Login");
        Button btnRegister = new Button("Register");
        Label messageLabel = new Label("");

        // REGISTER ACTION: Writes user data to users.csv
        btnRegister.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username or password cannot be empty!");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Using FileWriter with 'true' to append data instead of overwriting
            try (PrintWriter pw = new PrintWriter(new FileWriter("users.csv", true))) {
                pw.println(username + "," + password);
                messageLabel.setText("Registered successfully! You can now login.");
                messageLabel.setStyle("-fx-text-fill: green;");
            } catch (IOException ex) {
                messageLabel.setText("Error saving user data!");
            }
        });

        // LOGIN ACTION: Reads users.csv to authenticate
        btnLogin.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            boolean authenticated = false;

            try (Scanner scanner = new Scanner(new File("users.csv"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    // Split the CSV line into username and password
                    String[] parts = line.split(",");
                    if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                        authenticated = true;
                        break;
                    }
                }
            } catch (FileNotFoundException ex) {
                messageLabel.setText("No users found. Please register first.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (authenticated) {
                loggedInUser = username;
                welcomeLabel.setText("Player: " + loggedInUser);
                userField.clear();
                passField.clear();
                messageLabel.setText("");
                // Switch to game scene upon successful login
                window.setScene(gameScene);
            } else {
                messageLabel.setText("Invalid username or password!");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        });

        HBox buttonBox = new HBox(10, btnLogin, btnRegister);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, titleLabel, userField, passField, buttonBox, messageLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        loginScene = new Scene(layout, 450, 350);
    }

    // ==========================================
    // 2. GAME SCENE & TOURNAMENT LOGIC
    // ==========================================
    private void createGameScene() {
        welcomeLabel = new Label("Player: ");
        welcomeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: purple;");

        btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            window.setScene(loginScene);
        });

        HBox topBox = new HBox(20, welcomeLabel, btnLogout);
        topBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Rock Paper Scissors Tournament");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label inputLabel = new Label("Target Score to Win: ");
        targetInput = new TextField("3");
        targetInput.setPrefWidth(50);
        btnStart = new Button("Start Game");

        HBox setupBox = new HBox(10, inputLabel, targetInput, btnStart);
        setupBox.setAlignment(Pos.CENTER);

        scoreLabel = new Label("Set target score and click 'Start Game'!");
        scoreLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: blue;");

        choicesLabel = new Label("");
        resultLabel = new Label("");

        btnRock = new Button("Stone (R)");
        btnPaper = new Button("Paper (P)");
        btnScissors = new Button("Scissors (S)");

        // Disable game buttons until the tournament starts
        btnRock.setDisable(true);
        btnPaper.setDisable(true);
        btnScissors.setDisable(true);

        btnStart.setOnAction(e -> startTournament());
        btnRock.setOnAction(e -> playRound('R'));
        btnPaper.setOnAction(e -> playRound('P'));
        btnScissors.setOnAction(e -> playRound('S'));

        HBox buttonBox = new HBox(15, btnRock, btnPaper, btnScissors);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, topBox, titleLabel, setupBox, scoreLabel, buttonBox, choicesLabel, resultLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        gameScene = new Scene(layout, 450, 400);
    }

    // Prepares the variables for a new tournament
    private void startTournament() {
        try {
            targetScore = Integer.parseInt(targetInput.getText());
            if (targetScore <= 0) {
                scoreLabel.setText("Please enter a number greater than 0!");
                return;
            }

            // Reset scores for a fresh start
            currentRound = 0; userWins = 0; computerWins = 0;

            // Enable gameplay buttons and lock the setup input
            btnRock.setDisable(false); btnPaper.setDisable(false); btnScissors.setDisable(false);
            targetInput.setDisable(true); btnStart.setDisable(true);

            scoreLabel.setText("First to " + targetScore + " wins!  |  Score: You 0 - 0 Computer");
            choicesLabel.setText("Game started!");
            resultLabel.setText("Make your choice!");
            resultLabel.setStyle("-fx-text-fill: black; -fx-font-size: 13px;");

        } catch (NumberFormatException ex) {
            scoreLabel.setText("Please enter a valid number!");
        }
    }

    // Handles the core gameplay loop per round
    private void playRound(char you) {
        currentRound++;
        char computer = 'R';

        // Randomly assign computer's choice
        int n = random.nextInt(3);
        switch (n) {
            case 0: computer = 'R'; break;
            case 1: computer = 'P'; break;
            case 2: computer = 'S'; break;
        }

        int result = game(you, computer);

        if (result == 1) userWins++;
        else if (result == 0) computerWins++;

        choicesLabel.setText("Round " + currentRound + " -> You: " + you + " | Computer: " + computer);
        scoreLabel.setText("Target: " + targetScore + "  |  Score: You " + userWins + " - " + computerWins + " Computer");

        if (result == -1) {
            resultLabel.setText("This round is a draw!"); resultLabel.setStyle("-fx-text-fill: orange;");
        } else if (result == 1) {
            resultLabel.setText("Bravo! You won this round!"); resultLabel.setStyle("-fx-text-fill: green;");
        } else {
            resultLabel.setText("Unfortunately! You lost this round!"); resultLabel.setStyle("-fx-text-fill: red;");
        }

        // Check if the tournament has concluded
        if (userWins == targetScore || computerWins == targetScore) {
            // Lock gameplay buttons and unlock setup for a new game
            btnRock.setDisable(true); btnPaper.setDisable(true); btnScissors.setDisable(true);
            targetInput.setDisable(false); btnStart.setDisable(false); btnStart.setText("New Game");

            scoreLabel.setText("Tournament Finished! Final Score: You " + userWins + " - " + computerWins + " Computer");

            String matchResult;
            if (userWins == targetScore) {
                matchResult = "WON";
                resultLabel.setText("CHAMPION! You reached " + targetScore + " points first!");
                resultLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else {
                matchResult = "LOST";
                resultLabel.setText("GAME OVER! Computer reached " + targetScore + " points first!");
                resultLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-font-weight: bold;");
            }

            // TOURNAMENT END: Writes final score data to scores.csv
            try (PrintWriter pw = new PrintWriter(new FileWriter("scores.csv", true))) {
                pw.println("User: " + loggedInUser + ", Result: " + matchResult + ", Score: " + userWins + "-" + computerWins);
            } catch (IOException ex) {
                System.out.println("Could not save the score.");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}