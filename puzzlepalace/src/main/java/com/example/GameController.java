package com.example;

import java.io.IOException;
import java.util.Optional;

import com.model.HintRequestResult;
import com.model.Player;
import com.model.Puzzle;
import com.model.PuzzlePalaceFacade;
import com.model.Room;
import com.model.Score;
import com.model.Settings;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


/**
 * Controls the game screen.
 * Loads puzzles, handles answers, hints, timer, and final certificate.
 */
public class GameController {

    private static final int STANDARD_HINT_LIMIT = 3;

    @FXML
    private Label roomTitleLabel;

    @FXML
    private Label puzzlePromptLabel;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Label hintLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label progressSummaryLabel;

    @FXML
    private TextField answerField;

    @FXML
    private Button submitButton;

    @FXML
    private Button hintButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button extraHintButton;

    @FXML
    private Button freezeTimerButton;

    @FXML
    private VBox certificateBox;

    @FXML
    private Label certificateTitleLabel;

    @FXML
    private Label certificateMessageLabel;

    @FXML
    private Label certificateDifficultyLabel;

    @FXML
    private Label certificateHintsLabel;

    @FXML
    private Label certificateScoreLabel;

    @FXML
    private Label certificateFooterLabel;



    private Puzzle activePuzzle;

    private Timeline timerTimeline;
    private boolean firstRoomTransitionAcknowledged;
    private boolean secondRoomTransitionAcknowledged;
    private boolean finalEscapeMessageShown;





        /** Called after FXML loads. Sets up the screen. */

    @FXML
    private void initialize() {
        loadPuzzle();
        updateProgressSummary();
    }

            /** Loads the current puzzle and updates UI state. */

    private void loadPuzzle() {
        PuzzlePalaceFacade facade = App.getFacade();
        hideCertificate();

        activePuzzle = facade.getActivePuzzle();
        if (activePuzzle == null) {
            finalEscapeMessageShown = false;
        } else if (!"SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            finalEscapeMessageShown = false;
        } else if (facade != null && !facade.hasNextRoom()) {
            finalEscapeMessageShown = true;
        }
        if (facade.isCurrentRoomFirst() && (activePuzzle == null || !"SOLVED".equalsIgnoreCase(activePuzzle.getStatus()))) {
            firstRoomTransitionAcknowledged = false;
        }
        if (facade.isCurrentRoomSecond() && (activePuzzle == null || !"SOLVED".equalsIgnoreCase(activePuzzle.getStatus()))) {
            secondRoomTransitionAcknowledged = false;
        }
        if (roomTitleLabel != null) {
            Settings.Difficulty difficulty = facade.getSelectedDifficulty();
            String label = String.format("%s Challenge – %s",
                    difficulty.getDisplayName(),
                    facade.getCurrentRoomName());
            roomTitleLabel.setText(label);    
        }
        if (activePuzzle == null) {
            puzzlePromptLabel.setText("No puzzle available.");
            submitButton.setDisable(true);
            hintButton.setDisable(true);
            nextButton.setVisible(false); 
            nextButton.setManaged(false);
            nextButton.setDisable(true);
            nextButton.setText("Next Room");

        

            stopTimer();
            updateTimerLabelWithSeconds(0);
            updateProgressSummary();
            if (!facade.hasNextRoom()) {
                showCertificate();
            }
            updateExtraHintButton();
            updateFreezeTimerButton();
            return;
        }

        puzzlePromptLabel.setText(activePuzzle.getDescription());
        feedbackLabel.setText("");
        hintLabel.setText("");
        answerField.setDisable(false);
        answerField.clear();
        submitButton.setDisable(false);
        hintButton.setDisable(!hasStandardHintsRemaining(activePuzzle));
        nextButton.setVisible(false); // hide by default
        nextButton.setManaged(false);
        nextButton.setDisable(true);

        updateExtraHintButton();
        updateFreezeTimerButton();

        if ("SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            displaySolvedState();
        } else {
            facade.ensureActivePuzzleTimerStarted();
            startTimer();
        }
        updateProgressSummary();
    }


        /** Called when the user submits an answer. */

    @FXML
    private void handleSubmitAnswer() {
        if (activePuzzle == null) {
            return;
        }
        String answer = answerField.getText();
        boolean solved = App.getFacade().submitPuzzleAnswer(activePuzzle.getPuzzleId(), answer);
        if (solved) {
            StringBuilder message = new StringBuilder("Correct! The door unlocks with a satisfying click.");
            if (activePuzzle.getHintsUsed() == 0) {
                message.append("\nYou earned an extra hint token for solving without hints!");
            }
            feedbackLabel.setText(message.toString());
            if (shouldShowEscapeCompletionMessage()) {
                showEscapeCompletionAlert();
            }
            displaySolvedState();
        } else {
            feedbackLabel.setText("That's not quite right. Try another combination.");
        }
        updateExtraHintButton();
        updateFreezeTimerButton();
    }

        /** Gives a standard hint if available. */
    @FXML
    private void handleRequestHint() {
        if (activePuzzle == null) {
            return;
        }
        if (!hasStandardHintsRemaining(activePuzzle)) {
            if (feedbackLabel != null) {
                feedbackLabel.setText("You've used all standard hints. Try an extra hint token!");
            }
            hintButton.setDisable(true);
            return;
        }
        String hint = App.getFacade().requestHint(activePuzzle.getPuzzleId());
        hintLabel.setText(hint);
        if (!hasStandardHintsRemaining(activePuzzle)) {
            hintButton.setDisable(true);
        }
        updateExtraHintButton();
        updateFreezeTimerButton();
    }

    @FXML
    private void handleUseFreeClueToken() {
        if (activePuzzle == null) {
            return;
        }
        PuzzlePalaceFacade facade = App.getFacade();
        if (facade == null) {
            if (feedbackLabel != null) {
                feedbackLabel.setText("Unable to use an extra hint right now.");
            }
            return;
        }
        HintRequestResult result = facade.useFreeHintToken(activePuzzle.getPuzzleId());
        if (result == null) {
            if (feedbackLabel != null) {
                feedbackLabel.setText("Unable to use an extra hint right now.");
            }
            updateExtraHintButton();
            updateFreezeTimerButton();
            return;
        }
        if (result.isSuccess()) {
            if (feedbackLabel != null) {
                feedbackLabel.setText("Extra hint unlocked!");
            }
            if (hintLabel != null) {
                String hintText = result.getMessage();
                hintLabel.setText(hintText == null || hintText.isBlank() ? "No hint available." : hintText);
            }
        } else {
            String message = result.getMessage();
            if (message == null || message.isBlank()) {
                message = "No extra hints available.";
            }
            if (feedbackLabel != null) {
                feedbackLabel.setText(message);
            }
        }
        if (hintButton != null && !hasStandardHintsRemaining(activePuzzle)) {
            hintButton.setDisable(true);
        }
        updateExtraHintButton();
        updateFreezeTimerButton();
    }

        /** Returns to the dashboard screen. */
    @FXML
    private void handleReturnToDashboard() {
        try {
            stopTimer();
            App.setRoot("dashboard");
        } catch (IOException e) {
            feedbackLabel.setText("Unable to return to the dashboard.");
        }
    }

        /** Shows UI for when a puzzle is solved. */
    private void displaySolvedState() {
        answerField.setDisable(true);
        submitButton.setDisable(true);
        hintButton.setDisable(true);
        boolean hasNextRoom = App.getFacade().hasNextRoom();
        if (hasNextRoom && App.getFacade().isNextRoomFinal()) {
            nextButton.setText("Final Room");
        }
        nextButton.setVisible(hasNextRoom);
        nextButton.setManaged(hasNextRoom);
        nextButton.setDisable(!hasNextRoom);

        stopTimer();
        long lastSeconds = App.getFacade().getLastCompletionSeconds();
        updateTimerLabelWithSeconds(lastSeconds);
        updateProgressSummary();
        if (!hasNextRoom) {
            showCertificate();
        } else {
            hideCertificate();
        }
        updateExtraHintButton();
        updateFreezeTimerButton();

    }

    private boolean shouldShowEscapeCompletionMessage() {
        PuzzlePalaceFacade facade = App.getFacade();
        if (facade == null || finalEscapeMessageShown) {
            return false;
        }
        if (activePuzzle == null || !"SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            return false;
        }
        return !facade.hasNextRoom();
    }

    private void showEscapeCompletionAlert() {
        finalEscapeMessageShown = true;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Escape Achieved");
        alert.setHeaderText("You Escaped!");
        alert.setContentText("The door opens with a loud click. Bright light fills the room as you step outside into the cool night. You’re free. Behind you, the PuzzlePalace becomes quiet and still again. You escaped.");
        alert.showAndWait();
    }


    @FXML
    private void handleNextRoom() {
        PuzzlePalaceFacade facade = App.getFacade();
        stopTimer();
        if (facade != null && facade.isCurrentRoomFirst() && !firstRoomTransitionAcknowledged) {
            showFirstRoomTransitionDialog(facade);

            return;
        }
        if (facade != null && facade.isCurrentRoomSecond() && !secondRoomTransitionAcknowledged) {
            showSecondRoomTransitionDialog(facade);
            return;
        }
        proceedToNextRoom(facade);


    }

        /** Attempts to freeze the active timer (if allowed). */
    @FXML
    private void handleFreezeTimer() {
        PuzzlePalaceFacade facade = App.getFacade();
        if (facade == null) {
            return;
        }
        boolean activated = facade.activateFreezeTimer();
        if (activated) {
            if (feedbackLabel != null) {
                feedbackLabel.setText("Timer frozen for 10 seconds! Stay focused on the final puzzle.");
            }
        } else {
            if (feedbackLabel != null) {
                feedbackLabel.setText("Unable to freeze the timer right now.");
            }
        }
        updateFreezeTimerButton();
    }

    private void startTimer() {
        stopTimer();
        updateTimerLabelWithSeconds(App.getFacade().getActivePuzzleElapsedSeconds());
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            updateTimerLabelWithSeconds(App.getFacade().getActivePuzzleElapsedSeconds());
            updateFreezeTimerButton();
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

        /** Starts the timer with option to reset shown time. */
    private void startTimer(boolean resetDisplay) {
        stopTimer();
        App.getFacade().ensureActivePuzzleTimerStarted();
        if (resetDisplay) {
            updateTimerLabelWithSeconds(0);
        } else {
            updateTimerLabelWithSeconds(App.getFacade().getActivePuzzleElapsedSeconds());
        }
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            updateTimerLabelWithSeconds(App.getFacade().getActivePuzzleElapsedSeconds());
            updateFreezeTimerButton();
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
            timerTimeline = null;
        }
    }

        /** Sets timer label text using seconds. */
    private void updateTimerLabelWithSeconds(long totalSeconds) {
        if (timerLabel == null) {
            return;
        }
        long clamped = Math.max(0, totalSeconds);
        long minutes = clamped / 60;
        long seconds = clamped % 60;
        timerLabel.setText(String.format("Timer: %02d:%02d", minutes, seconds));
    }

    private void updateProgressSummary() {
        if (progressSummaryLabel == null) {
            return;
        }
        long lastSeconds = App.getFacade().getLastCompletionSeconds();
        if (lastSeconds > 0) {
            long minutes = lastSeconds / 60;
            long seconds = lastSeconds % 60;
            progressSummaryLabel.setText(String.format("Previous escape time: %02d:%02d", minutes, seconds));
        } else {
            progressSummaryLabel.setText("No escape time recorded yet.");
        }
    }

    private void hideCertificate() {
        if (certificateBox != null) {
            certificateBox.setVisible(false);
            certificateBox.setManaged(false);
        }
        if (certificateMessageLabel != null) {
            certificateMessageLabel.setText("");
        }
        if (certificateDifficultyLabel != null) {
            certificateDifficultyLabel.setText("");
        }
        if (certificateHintsLabel != null) {
            certificateHintsLabel.setText("");
        }
        if (certificateScoreLabel != null) {
            certificateScoreLabel.setText("");
        }
        if (certificateFooterLabel != null) {
            certificateFooterLabel.setText("");
        }
    }

    private void showCertificate() {
        if (certificateBox == null) {
            return;
        }
        PuzzlePalaceFacade facade = App.getFacade();
        Settings.Difficulty difficulty = facade.getSelectedDifficulty();
        String difficultyName = difficulty != null ? difficulty.getDisplayName() : Settings.Difficulty.EASY.getDisplayName();
        int totalHintsUsed = calculateTotalHintsUsed(facade);
        Player player = facade.getCurrentPlayer();
        Score score = player != null ? player.getScoreDetails() : null;
        int baseScore = score != null ? Math.max(0, score.calculateScore()) : 0;
        int difficultyMultiplier = resolveDifficultyMultiplier(difficulty);
        int hintPenaltyPer = 10;
        int finalScore = Math.max(0, baseScore * difficultyMultiplier - totalHintsUsed * hintPenaltyPer);
        if (score != null) {
            score.setHintsUsed(totalHintsUsed);
        }

        if (certificateTitleLabel != null) {
            certificateTitleLabel.setText("Puzzle Palace");
        }
        if (certificateMessageLabel != null) {
            certificateMessageLabel.setText("You cracked the final vault and escaped the Puzzle Palace!");
        }
        if (certificateDifficultyLabel != null) {
            certificateDifficultyLabel.setText(String.format("Difficulty Played: %s", difficultyName));
        }
        if (certificateHintsLabel != null) {
            certificateHintsLabel.setText(String.format("Hints Used: %d", totalHintsUsed));
        }
        if (certificateScoreLabel != null) {
            certificateScoreLabel.setText(String.format("Final Score: %,d", finalScore));
        }
        if (certificateFooterLabel != null) {
            certificateFooterLabel.setText(String.format("Score = (base %,d × %dx difficulty) − %d from hints.",
                    baseScore,
                    difficultyMultiplier,
                    totalHintsUsed * hintPenaltyPer));
        }

        certificateBox.setVisible(true);
        certificateBox.setManaged(true);
    }

    private int calculateTotalHintsUsed(PuzzlePalaceFacade facade) {
        int total = 0;
        if (facade == null) {
            return total;
        }
        for (Room room : facade.listAvailableRooms()) {
            if (room == null) {
                continue;
            }
            for (Puzzle puzzle : room.getPuzzles()) {
                if (puzzle != null) {
                    total += Math.max(0, puzzle.getPenaltyHintsUsed());
                }
            }
        }
        return total;
    }

        /** Updates whether to show the extra hint button. */
    private void updateExtraHintButton() {
        if (extraHintButton == null) {
            return;
        }
        PuzzlePalaceFacade facade = App.getFacade();
        boolean show = facade != null
                && activePuzzle != null
                && !"SOLVED".equalsIgnoreCase(activePuzzle.getStatus())
                && facade.hasFreeHintToken()
                && hasRemainingHintsAvailable(activePuzzle);
        extraHintButton.setVisible(show);
        extraHintButton.setManaged(show);
        if (!show) {
            extraHintButton.setText("Extra hint");
            return;
        }
        int tokens = Math.max(0, facade.getFreeHintTokenCount());
        if (tokens > 1) {
            extraHintButton.setText(String.format("Extra hint (%d)", tokens));
        } else {
            extraHintButton.setText("Extra hint");
        }
    }

        /** Updates the freeze timer button text and state. */
    private void updateFreezeTimerButton() {
        if (freezeTimerButton == null) {
            return;
        }
        PuzzlePalaceFacade facade = App.getFacade();
        boolean show = facade != null && activePuzzle != null && facade.canUseFreezeTimerItem();
        freezeTimerButton.setVisible(show);
        freezeTimerButton.setManaged(show);
        if (!show) {
            freezeTimerButton.setText("Freeze Timer");
            freezeTimerButton.setDisable(false);
            return;
        }
        boolean active = facade.isFreezeTimerActive();
        int charges = Math.max(0, facade.getFreezeTimerChargeCount());
        if (active) {
            freezeTimerButton.setText("Freeze Active");
        } else if (charges > 1) {
            freezeTimerButton.setText(String.format("Freeze Timer (%d)", charges));
        } else {
            freezeTimerButton.setText("Freeze Timer");
        }
        freezeTimerButton.setDisable(active);
    }

    private void showFirstRoomTransitionDialog(PuzzlePalaceFacade facade) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Corridor Unlocked");
        alert.setHeaderText(null);
        alert.setContentText("The equation clicks into place. A hidden panel slides open, letting a draft of cool air whisper past. A corridor of books and runes awaits you step forward, heartbeat steady, into the next room.");
        ButtonType nextButtonType = new ButtonType("Next", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(nextButtonType);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == nextButtonType) {
            firstRoomTransitionAcknowledged = true;
            proceedToNextRoom(facade);
        } else {
            updateFreezeTimerButton();
        }
    }
    private void showSecondRoomTransitionDialog(PuzzlePalaceFacade facade) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Final Door Unsealed");
        alert.setHeaderText(null);
        alert.setContentText("The word fades, and the door unlocks with a soft chime. A calm silence fills the air you take a deep breath and step into the final room.");
        ButtonType nextButtonType = new ButtonType("Next", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(nextButtonType);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == nextButtonType) {
            secondRoomTransitionAcknowledged = true;
            proceedToNextRoom(facade);
        } else {
            updateFreezeTimerButton();
        }
    }

    private void proceedToNextRoom(PuzzlePalaceFacade facade) {
        if (facade == null) {
            return;
        }
        boolean advanced = facade.moveToNextRoom();
        if (!advanced) {
            feedbackLabel.setText("No more rooms to explore. Return to the dashboard to celebrate!");
            showCertificate();
            updateFreezeTimerButton();
            return;
        }
        loadPuzzle();
        updateFreezeTimerButton();
    }


        /** Checks if any hints remain for the puzzle. */
    private boolean hasRemainingHintsAvailable(Puzzle puzzle) {
        if (puzzle == null) {
            return false;
        }
        return hasAnyHintsAvailable(puzzle);
    }

        /** Checks if standard (free) hints are still allowed. */
    private boolean hasStandardHintsRemaining(Puzzle puzzle) {
        if (puzzle == null) {
            return false;
        }
        int maxHints = Math.max(0, puzzle.getMaxHints());
        if (maxHints == 0) {
            return false;
        }
        int allowed = Math.min(STANDARD_HINT_LIMIT, maxHints);
        int used = Math.max(0, puzzle.getPenaltyHintsUsed());
        return used < allowed;
    }

        /** Checks if puzzle has any hint slots left. */
    private boolean hasAnyHintsAvailable(Puzzle puzzle) {
        if (puzzle == null) {
            return false;
        }
        int maxHints = Math.max(0, puzzle.getMaxHints());
        if (maxHints == 0) {
            return false;
        }
        int used = Math.max(0, puzzle.getHintsUsed());
        return used < maxHints;
    }

        /** Difficulty multiplier: EASY=1, MEDIUM=2, HARD=3. */
    private int resolveDifficultyMultiplier(Settings.Difficulty difficulty) {
        Settings.Difficulty resolved = difficulty == null ? Settings.Difficulty.EASY : difficulty;
        switch (resolved) {
            case HARD:
                return 3;
            case MEDIUM:
                return 2;
            case EASY:
            default:
                return 1;
        }
    }
}