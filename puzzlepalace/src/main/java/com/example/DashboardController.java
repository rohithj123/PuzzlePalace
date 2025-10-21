package com.example;

import com.model.Player;
import com.model.PuzzlePalaceFacade;
import com.model.Room;
import com.model.Score;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label puzzlesSolvedLabel;

    @FXML
    private Label hintsUsedLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        refreshPlayerDetails();
    }

    @FXML
    private void handleLogout() {
        PuzzlePalaceFacade facade = App.getFacade();
        facade.logout();
        try {
            App.setRoot("login");
        } catch (IOException e) {
            statusLabel.setText("Unable to return to the login view.");
        }
    }

    @FXML
    private void handleSaveProgress() {
        PuzzlePalaceFacade facade = App.getFacade();
        facade.saveCurrentPlayerProgress();
        statusLabel.setText("Progress saved successfully.");
    }

    private void refreshPlayerDetails() {
        PuzzlePalaceFacade facade = App.getFacade();
        Room current = facade.getCurrentRoom();
        if (current == null) {
            welcomeLabel.setText("No player logged in.");
            scoreLabel.setText("Score: --");
            puzzlesSolvedLabel.setText("Puzzles solved: --");
            hintsUsedLabel.setText("Hints used: --");
            statusLabel.setText("Please log in again.");
            return;
        }

        welcomeLabel.setText("Welcome back, " + current.getName() + "!");
        Score score = current.getScoreDetails();
        int totalScore = score != null ? score.calculateScore() : 0;
        scoreLabel.setText("Score: " + totalScore);
        puzzlesSolvedLabel.setText("Puzzles solved: " + (score != null ? score.getPuzzlesSolved() : 0));
        hintsUsedLabel.setText("Hints used: " + (score != null ? score.getHintsUsed() : 0));
        statusLabel.setText("Your adventure awaits!");
    }
}