package com.example;

import java.io.IOException;

import com.model.Player;
import com.model.PuzzlePalaceFacade;
import com.model.Score;
import com.model.Settings;


import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

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
    private Label timeTakenLabel;


    @FXML
    private Label statusLabel;

    @FXML
    private Label puzzleStatusLabel;
    @FXML
    private ChoiceBox<Settings.Difficulty> difficultyChoiceBox;

    @FXML
    private Label difficultyDescriptionLabel;


    @FXML
    private void initialize() {
        configureDifficultySelector();

        refreshPlayerDetails();
    }

    @FXML
    private void handleLogout() {
        PuzzlePalaceFacade facade = App.getFacade();
        Settings.Difficulty selected = difficultyChoiceBox != null
        ? difficultyChoiceBox.getSelectionModel().getSelectedItem()
        : Settings.Difficulty.EASY;
facade.setSelectedDifficulty(selected);
        facade.logout();
        try {
            App.getFacade().startEscapeRoom();

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

    @FXML
    private void handleStartPuzzle() {
        try {
            PuzzlePalaceFacade facade = App.getFacade();
            Settings.Difficulty selected = difficultyChoiceBox != null
            ? difficultyChoiceBox.getSelectionModel().getSelectedItem()
            : Settings.Difficulty.EASY;
    facade.setSelectedDifficulty(selected);
            facade.resetProgressToFirstRoom();
            App.setRoot("game");
        } catch (IOException e) {
            statusLabel.setText("Unable to open the puzzle view.");
        }
    }


    private void refreshPlayerDetails() {
        PuzzlePalaceFacade facade = App.getFacade();
        Player current = facade.getCurrentPlayer();
        if (current == null) {
            welcomeLabel.setText("No player logged in.");
            scoreLabel.setText("Score: --");
            puzzlesSolvedLabel.setText("Puzzles solved: --");
            hintsUsedLabel.setText("Hints used: --");
            if (timeTakenLabel != null) {
                timeTakenLabel.setText("Last escape time: --");
            }

            statusLabel.setText("Please log in again.");
            if (puzzleStatusLabel != null) {
                puzzleStatusLabel.setText("Log in to access puzzles.");
            }
            return;
        }

        welcomeLabel.setText("Welcome back, " + current.getUsername() + "!");
        Score score = current.getScoreDetails();
        int totalScore = score != null ? score.calculateScore() : 0;
        scoreLabel.setText("Score: " + totalScore);
        puzzlesSolvedLabel.setText("Puzzles solved: " + (score != null ? score.getPuzzlesSolved() : 0));
        hintsUsedLabel.setText("Hints used: " + (score != null ? score.getHintsUsed() : 0));
        if (timeTakenLabel != null) {
            timeTakenLabel.setText("Last escape time: " + formatTime(score != null ? score.getTimeTaken() : 0));
        }
        statusLabel.setText("Your adventure awaits!");
        if (puzzleStatusLabel != null) {
            puzzleStatusLabel.setText(facade.describeCurrentPuzzleStatus());
        }
        if (difficultyChoiceBox != null) {
            Settings.Difficulty difficulty = facade.getSelectedDifficulty();
            difficultyChoiceBox.getSelectionModel().select(difficulty);
            updateDifficultyDescription(difficulty);
        }
    }
    private String formatTime(int seconds) {
        if (seconds <= 0) {
            return "--";
        }
        int mins = seconds / 60;
        int secs = seconds % 60;
        if (mins <= 0) {
            return secs + "s";
        }
        return mins + "m " + String.format("%02ds", secs);
    }
    
    private void configureDifficultySelector() {
        if (difficultyChoiceBox == null) {
            return;
        }
        difficultyChoiceBox.getItems().setAll(Settings.Difficulty.values());
        Settings.Difficulty current = App.getFacade().getSelectedDifficulty();
        difficultyChoiceBox.getSelectionModel().select(current);
        updateDifficultyDescription(current);
        difficultyChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Settings.Difficulty resolved = newVal == null ? Settings.Difficulty.EASY : newVal;
            updateDifficultyDescription(resolved);
        });
    }
    private void updateDifficultyDescription(Settings.Difficulty difficulty) {
        if (difficultyDescriptionLabel == null) {
            return;
        }
        Settings.Difficulty resolved = difficulty == null ? Settings.Difficulty.EASY : difficulty;
        String text;
        switch (resolved) {
            case MEDIUM:
                text = "A balanced challenge with multi-step clues and trickier riddles.";
                break;
            case HARD:
                text = "Designed for veterans: layered puzzles that demand careful reasoning.";
                break;
            case EASY:
            default:
                text = "Great for warming up—straightforward puzzles with generous hints.";
                break;
        }
        difficultyDescriptionLabel.setText(text);
    }
    
}
