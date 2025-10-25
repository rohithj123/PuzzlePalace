package com.model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PuzzlePalaceFacade {

    private Player currentPlayer;
    private Progress progress;
    private Leaderboard leaderboard;
    private Settings settings;
    private Room currentRoom;
    private Puzzle activePuzzle;
    private final PlayerManager playerManager;
    private final String userDataPath;
        private Instant puzzleStartTime;
    private long lastCompletionSeconds;
    private final List<Room> availableRooms;
    private int currentRoomIndex;

    public PuzzlePalaceFacade() {
        this("json/users.json");
    }

    public PuzzlePalaceFacade(String userDataPath) {
        this.playerManager = new PlayerManager();
        this.userDataPath = userDataPath;
        this.settings = new Settings();
        this.availableRooms = new ArrayList<>();
        this.currentRoomIndex = -1;
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
        startEscapeRoom();
        return this.currentPlayer;
    }

    private Room summarisePlayerRoom(Player player) {
        if (player == null) {
            availableRooms.clear();
            activePuzzle = null;
            currentRoom = null;
            currentRoomIndex = -1;
            return null;
        }
        if (availableRooms.isEmpty() || currentRoom == null) {
            buildRoomsFor(player);
        }
        return currentRoom;
    }

    private void buildRoomsFor(Player player) {
        availableRooms.clear();
        currentRoom = null;
        activePuzzle = null;
        currentRoomIndex = -1;
        puzzleStartTime = null;

        if (player == null) {
            lastCompletionSeconds = 0L;
            return;
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

        Room mathRoom = new Room();
        mathRoom.setRoomId("math-gate");
        mathRoom.setName("Math Gate");
        mathRoom.setDescription("A glowing equation blocks the exit.");
        mathRoom.addPuzzle(puzzle);

        SimplePuzzle wordPuzzle = new SimplePuzzle(
                2002,
                "Shelves whisper riddles: unscramble the letters T L G H I to reveal the password.",
                "light",
                "Think about what helps you see in the dark.",
                "The answer is something that shines brightly."
        );

        Room wordRoom = new Room();
        wordRoom.setRoomId("word-puzzle");
        wordRoom.setName("Word Puzzle Room");
        wordRoom.setDescription("Stacks of books hide a secret word.");
        wordRoom.addPuzzle(wordPuzzle);

        availableRooms.add(mathRoom);
        availableRooms.add(wordRoom);

        currentRoomIndex = 0;
        currentRoom = mathRoom;
        activePuzzle = puzzle;
        

        Score score = player.getScoreDetails();
        lastCompletionSeconds = score != null ? Math.max(0, score.getTimeTaken()) : 0;
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
        availableRooms.clear();
        currentRoomIndex = -1;
        puzzleStartTime = null;
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

    public void enterRoom(int roomIndex) {
        if (roomIndex < 0 || roomIndex >= availableRooms.size()) {
            return;
        }
        currentRoomIndex = roomIndex;
        currentRoom = availableRooms.get(roomIndex);
        activePuzzle = currentRoom.getPuzzles().isEmpty() ? null : currentRoom.getPuzzles().get(0);
        puzzleStartTime = null;
    }

    public Room getCurrentRoom() {
        if (currentPlayer == null) {
            return null;
        }
        if (currentRoom == null || availableRooms.isEmpty()) {
            currentRoom = summarisePlayerRoom(currentPlayer);
        }
        return currentRoom;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Puzzle getActivePuzzle() {
        if (activePuzzle == null) {
            Room room = getCurrentRoom();
            if (room != null) {
                activePuzzle = room.getPuzzles().isEmpty() ? null : room.getPuzzles().get(0);
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
        if (currentPlayer == null) {
            return Collections.emptyList();
        }
        if (availableRooms.isEmpty()) {
            summarisePlayerRoom(currentPlayer);
        }
        return Collections.unmodifiableList(new ArrayList<>(availableRooms));
    }

    public Puzzle getPuzzle(int puzzleId) {
        Room room = getCurrentRoom();
        if (room == null) {
            return null;
        }
        return room.getPuzzleById(puzzleId);
    }

    public boolean moveToNextRoom() {
        if (!hasNextRoom()) {
            return false;
        }
        enterRoom(currentRoomIndex + 1);
        return activePuzzle != null;
    }

    public boolean hasNextRoom() {
        return currentRoomIndex >= 0 && currentRoomIndex + 1 < availableRooms.size();
    }

    public String getCurrentRoomName() {
        Room room = getCurrentRoom();
        if (room == null || room.getName() == null || room.getName().isBlank()) {
            return "Mystery Room";
        }
        return room.getName();
    }

    public void resetProgressToFirstRoom() {
        if (currentPlayer == null) {
            return;
        }
        if (availableRooms.isEmpty()) {
            buildRoomsFor(currentPlayer);
        }
        for (Room room : availableRooms) {
            for (Puzzle puzzle : room.getPuzzles()) {
                puzzle.resetPuzzle();
            }
        }
        enterRoom(0);
        puzzleStartTime = null;
    }
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
            Score score = currentPlayer != null ? currentPlayer.getScoreDetails() : null;
            if (score != null) {
                int seconds = (int) Math.min(Integer.MAX_VALUE, Math.max(0L, lastCompletionSeconds));
                score.setTimeTaken(seconds);
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
        if (currentPlayer == null) {
            availableRooms.clear();
            currentRoom = null;
            activePuzzle = null;
            currentRoomIndex = -1;
            puzzleStartTime = null;
            lastCompletionSeconds = 0L;
            return;
        }

        buildRoomsFor(currentPlayer);

    }
}
