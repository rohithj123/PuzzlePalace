package com.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;


public class PuzzlePalaceFacade {

    private Player currentPlayer;
    private Progress progress;
    private Leaderboard leaderboard;
    private Settings settings;
    private Room currentRoom;
    private MathChallengePuzzle activePuzzle;
    private final PlayerManager playerManager;
    private final String userDataPath;
        private Instant puzzleStartTime;
    private long lastCompletionSeconds;

    public PuzzlePalaceFacade() {
        this("json/users.json");
    }

    public PuzzlePalaceFacade(String userDataPath) {
        this.playerManager = new PlayerManager();
        this.userDataPath = userDataPath;
        this.settings = new Settings();
        loadUsers();
    }

    private void loadUsers() {
        try {
            List<Player> loaded = playerManager.loadPlayersFromFile(userDataPath);
            if (loaded == null || loaded.isEmpty()) {
                seedDefaultPlayers();
            }
        } catch (NoClassDefFoundError error) {
            System.out.println("PuzzlePalaceFacade: JSON parser unavailable, using fallback players.");
            seedDefaultPlayers();
        }
    }

    private void seedDefaultPlayers() {
        Player fallback = new Player("PlayerOne", "playerone@example.com", "SecretPass1!");
        playerManager.addPlayer(fallback);
    }

    public Player login(String userName, String password) {
        Player authenticated = playerManager.authenticate(userName, password);
        if (authenticated == null) {
            return null;
        }
        this.currentPlayer = authenticated;
        this.progress = currentPlayer.getProgress();
        if (this.progress != null) {
            this.progress.loadProgress();
        }
        this.currentRoom = summarisePlayerRoom(currentPlayer);
        return this.currentPlayer;
    }

    private Room summarisePlayerRoom(Player player) {
        if (player == null) {
            activePuzzle = null;
            return null;
        }

        MathChallengePuzzle puzzle = new MathChallengePuzzle(
            2001,
            "A glowing equation hovers over the vault: (12 + 8) / 4 + 3^2 = ?\n" +
                    "Punch in the final number to power the escape hatch.",
            14,
            "Work from the inside out—parentheses first!",
            "Remember that exponents come before addition.",
            "After dividing by four, you still need to add the value of 3²."
        );

        Room room = new Room();
        room.addPuzzle(puzzle);
        

        this.activePuzzle = puzzle;
        Score score = player.getScoreDetails();
        lastCompletionSeconds = score != null ? Math.max(0, score.getTimeTaken()) : 0;
        puzzleStartTime = null;
        return room;
    }

    public void logout() {
        saveCurrentPlayerProgress();
        if (currentPlayer != null) {
            currentPlayer.logout();
        }
        currentPlayer = null;
        progress = null;
        currentRoom = null;
        activePuzzle = null;
    }

    public Player createAccount(String userName, String password) {
        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        String trimmedUsername = userName.trim();
        if (playerManager.getPlayerByUsername(trimmedUsername) != null) {
            return null;
        }

        Player newPlayer = new Player(trimmedUsername, null, password);
        boolean added = playerManager.addPlayer(newPlayer);
        if (!added) {
            return null;
        }

        DataWriter.saveUsers(playerManager.getAllPlayers(), userDataPath);
        return newPlayer;

    }

    public void deleteAccount(int playerId) {
    }

    public void startNewGame() {
    }

    public void continueGame() {
    }

    public String showInstructions() {
        return null;
    }

    public void updateSettings(Settings settings) {
    }

    public Settings getSettings() {
        return settings;
    }

    public void toggleSound(boolean on) {
    }

    public void setLanguage(String langCode) {
    }

    public void setDifficulty(String level) {
    }

    public void enterRoom(int roomId) {
        this.currentRoom = null;
        this.activePuzzle = null;
        this.puzzleStartTime = null;
    }

    public Room getCurrentRoom() {
        if (currentPlayer == null) {
            return null;
        }
        if (currentRoom == null) {
            currentRoom = summarisePlayerRoom(currentPlayer);
        }
        return currentRoom;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public MathChallengePuzzle getActivePuzzle() {
        if (activePuzzle == null) {
            Room room = getCurrentRoom();
            if (room != null) {
                activePuzzle = room.getPuzzles().isEmpty() ? null : (MathChallengePuzzle) room.getPuzzles().get(0);
            }
        }
        ensureActivePuzzleTimerStarted();
        return activePuzzle;
    }

    public void ensureActivePuzzleTimerStarted() {
        if (activePuzzle != null && puzzleStartTime == null && !"SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            puzzleStartTime = Instant.now();
        }
    }

    public void restartActivePuzzleTimer() {
        if (activePuzzle == null) {
            puzzleStartTime = null;
            return;
        }
        if ("SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            puzzleStartTime = null;
        } else {
            puzzleStartTime = Instant.now();
        }
    }

    public long getActivePuzzleElapsedSeconds() {
        if (puzzleStartTime == null) {
            return 0L;
        }
        return Math.max(0L, Duration.between(puzzleStartTime, Instant.now()).getSeconds());
    }

    public long getLastCompletionSeconds() {
        return Math.max(0L, lastCompletionSeconds);
    }
    


    public String describeCurrentPuzzleStatus() {
        Puzzle puzzle = getActivePuzzle();
        if (puzzle == null) {
            return "No puzzle loaded.";
        }
        String status = puzzle.getStatus();
        if ("SOLVED".equalsIgnoreCase(status)) {
            return "You cracked the training puzzle!";
        }
        if ("ATTEMPTED".equalsIgnoreCase(status)) {
            return "The keypad is still locked. Try another code.";
        }
        return "A puzzle is waiting for you.";
    }

    public List<Room> listAvailableRooms() {
        Room room = getCurrentRoom();
        return room == null ? Collections.emptyList() : Collections.singletonList(room);    }

    public Puzzle getPuzzle(int puzzleId) {
        Room room = getCurrentRoom();
        if (room == null) {
            return null;
        }
        return room.getPuzzleById(puzzleId);    }

    public boolean submitPuzzleAnswer(int puzzleId, String answer) {
        Puzzle puzzle = getPuzzle(puzzleId);
        if (puzzle == null) {
            return false;
        }
        String previousStatus = puzzle.getStatus();
        boolean solved = puzzle.trySolve(answer);
        if (solved && (previousStatus == null || !"SOLVED".equalsIgnoreCase(previousStatus))) {
            if (currentPlayer != null) {
                currentPlayer.recordPuzzleSolved();
                currentPlayer.awardBonusPoints(100);
            }
            if (puzzleStartTime != null) {
                lastCompletionSeconds = Math.max(0L, Duration.between(puzzleStartTime, Instant.now()).getSeconds());
            }
            puzzleStartTime = null;
        }
        return solved;    }

    public List<Clue> getCluesForPuzzle(int puzzleId) {
        return null;
    }

    public boolean useItem(int itemId, int targetId) {
        return false;
    }

    public List<Player> getUserList() {
        return playerManager.getAllPlayers();
    }

    public void saveCurrentPlayerProgress() {
        
        if (currentPlayer == null) {
            return;
        }
        currentPlayer.saveProgress();
        Score score = currentPlayer.getScoreDetails();
        if (score != null) {
            score.setHintsUsed(activePuzzle != null ? activePuzzle.getHintsUsed() : score.getHintsUsed());
        }
        DataWriter.saveUsers(playerManager.getAllPlayers(), userDataPath);
    }

    public String requestHint(int puzzleId) {
        Puzzle puzzle = getPuzzle(puzzleId);
        if (puzzle == null) {
            return "No puzzle loaded.";
        }
        String hint = puzzle.requestHint();
        if (currentPlayer != null) {
            Score score = currentPlayer.getScoreDetails();
            if (score != null) {
                score.setHintsUsed(puzzle.getHintsUsed());
            }
        }
        return hint;    }

    public void startEscapeRoom() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startEscapeRoom'");
    }
}
