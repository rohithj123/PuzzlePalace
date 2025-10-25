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

        List<Room> rooms = createRoomsForDifficulty(getSelectedDifficulty());
        availableRooms.addAll(rooms);

        if (!availableRooms.isEmpty()) {
            currentRoomIndex = 0;
            currentRoom = availableRooms.get(0);
            activePuzzle = currentRoom.getPuzzles().isEmpty() ? null : currentRoom.getPuzzles().get(0);
        }

        Score score = player.getScoreDetails();
        lastCompletionSeconds = score != null ? Math.max(0, score.getTimeTaken()) : 0;
    }

    private List<Room> createRoomsForDifficulty(Settings.Difficulty difficulty) {
        if (difficulty == null) {
            difficulty = Settings.Difficulty.EASY;
        }
        switch (difficulty) {
            case MEDIUM:
                return createMediumRooms();
            case HARD:
                return createHardRooms();
            case EASY:
            default:
                return createEasyRooms();
        }
    }


    private List<Room> createEasyRooms() {
        List<Room> rooms = new ArrayList<>();
        Settings.Difficulty difficulty = Settings.Difficulty.EASY;

        MathChallengePuzzle mathPuzzle = new MathChallengePuzzle(
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
        mathRoom.setDifficulty(difficulty.getDisplayName());
        mathRoom.setEstimatedTimeMinutes(5);
        mathRoom.addPuzzle(mathPuzzle);
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
        wordRoom.setDifficulty(difficulty.getDisplayName());
        wordRoom.setEstimatedTimeMinutes(5);
        wordRoom.addPuzzle(wordPuzzle);

        SimplePuzzle logicPuzzle = new SimplePuzzle(
            2003,
            "The final vault presents three gemstone buttons: Ruby says 'Sapphire is the key,' " +
                    "Sapphire insists 'I am not the key,' and Emerald claims 'Ruby is lying.' " +
                    "Only one statement can be true. Which button will open the vault?",
            "sapphire",
            "Remember, exactly one of the statements is telling the truth.",
            "Try assuming each gemstone is correct and see which assumption keeps only a single statement true."
    );
    
    Room logicRoom = new Room();
    logicRoom.setRoomId("logic-vault");
    logicRoom.setName("Logic Vault");
    logicRoom.setDescription("Gemstone buttons challenge your reasoning.");
    logicRoom.setDifficulty(difficulty.getDisplayName());
    logicRoom.setEstimatedTimeMinutes(5);
    logicRoom.addPuzzle(logicPuzzle);

    rooms.add(mathRoom);
    rooms.add(wordRoom);
    rooms.add(logicRoom);
    return rooms;
}

private List<Room> createMediumRooms() {
    List<Room> rooms = new ArrayList<>();
    Settings.Difficulty difficulty = Settings.Difficulty.MEDIUM;

    MathChallengePuzzle mathPuzzle = new MathChallengePuzzle(
            2101,
            "Runed gears align to display: (18 / 3) + 4 × (5 - 1) = ?\n" +
                    "Set the mechanism to the correct number to advance.",
            22,
            "Pay attention to the operations inside the parentheses first.",
            "After dividing eighteen by three, tackle the multiplication.",
            "Your final step subtracts nothing—add the two partial results together."
    );

    Room mathRoom = new Room();
    mathRoom.setRoomId("math-gears");
    mathRoom.setName("Clockwork Calculations");
    mathRoom.setDescription("Intricate gears demand a precise calculation.");
    mathRoom.setDifficulty(difficulty.getDisplayName());
    mathRoom.setEstimatedTimeMinutes(7);
    mathRoom.addPuzzle(mathPuzzle);

    SimplePuzzle wordPuzzle = new SimplePuzzle(
            2102,
            "Carved runes glow softly: Arrange the letters P E A R L S to reveal the password whispered by the mages.",
            "pearls",
            "Think of treasure formed within a humble shell.",
            "The solution is plural and glimmers brightly.",
            "These treasures are often strung together as jewelry."
    );

    Room wordRoom = new Room();
    wordRoom.setRoomId("word-runes");
    wordRoom.setName("Rune Library");
    wordRoom.setDescription("Ancient runes hide a shimmering word.");
    wordRoom.setDifficulty(difficulty.getDisplayName());
    wordRoom.setEstimatedTimeMinutes(7);
    wordRoom.addPuzzle(wordPuzzle);

    SimplePuzzle logicPuzzle = new SimplePuzzle(
            2103,
            "Three clockwork gears are labeled A, B, and C. A claims 'B's statement is false.' " +
                    "B insists 'C is the key.' C declares 'B is lying.' Exactly one statement is true. " +
                    "Which gear unlocks the door? (Answer with A, B, or C)",
            "C",
            "If B were correct, what would that mean for the others?",
            "Try assuming each gear is the key and count how many statements stay true.",
            "Only one statement can be true—find the assumption that makes that possible."
    );

    Room logicRoom = new Room();
    logicRoom.setRoomId("logic-gears");
    logicRoom.setName("Gearwork Logic");
    logicRoom.setDescription("Synchronised gears debate which one is vital.");
    logicRoom.setDifficulty(difficulty.getDisplayName());
    logicRoom.setEstimatedTimeMinutes(7);
    logicRoom.addPuzzle(logicPuzzle);

    rooms.add(mathRoom);
    rooms.add(wordRoom);
    rooms.add(logicRoom);
    return rooms;
}

private List<Room> createHardRooms() {
    List<Room> rooms = new ArrayList<>();
    Settings.Difficulty difficulty = Settings.Difficulty.HARD;

    MathChallengePuzzle mathPuzzle = new MathChallengePuzzle(
            2201,
            "A crystalline equation pulses: ((4^3) + 6 × 5 - 18) / 2 = ?\n" +
                    "Only the correct final value will stabilise the portal.",
            38,
            "Resolve the exponent before anything else.",
            "Handle the multiplication and subtraction before dividing.",
            "Once the numerator is ready, divide by two to finish."
    );

    Room mathRoom = new Room();
    mathRoom.setRoomId("math-portal");
    mathRoom.setName("Arcane Calculus");
    mathRoom.setDescription("Mystic numbers swirl around a crystal portal.");
    mathRoom.setDifficulty(difficulty.getDisplayName());
    mathRoom.setEstimatedTimeMinutes(9);
    mathRoom.addPuzzle(mathPuzzle);

    SimplePuzzle wordPuzzle = new SimplePuzzle(
            2202,
            "A riddle is etched into the lock:\n" +
                    "Sentinels guard the ancient vault.\n" +
                    "Allies answer every call.\n" +
                    "Fables unlock hidden truths.\n" +
                    "Enter the word they form.",
            "safe",
            "Focus on the first letters of each line.",
            "Those letters combine to form a single, familiar word.",
            "It's exactly what the vault wants to be.");

    Room wordRoom = new Room();
    wordRoom.setRoomId("word-vault");
    wordRoom.setName("Vault of Verses");
    wordRoom.setDescription("Poetic wards conceal the password.");
    wordRoom.setDifficulty(difficulty.getDisplayName());
    wordRoom.setEstimatedTimeMinutes(9);
    wordRoom.addPuzzle(wordPuzzle);

    SimplePuzzle logicPuzzle = new SimplePuzzle(
            2203,
            "Three enchanted switches A, B, and C guard the final chamber. Exactly two of the following statements are true:\n" +
                    "A: 'Switch B will not open the door.'\n" +
                    "B: 'Switch C unlocks the door.'\n" +
                    "C: 'Switch A is lying.'\n" +
                    "Which switch actually opens the door? (Answer with A, B, or C)",
            "C",
            "Assume each switch opens the door in turn and test the statements.",
            "Remember that exactly two statements must be true at the same time.",
            "Only one assumption satisfies the requirement—identify which switch makes it work."
    );

    Room logicRoom = new Room();
    logicRoom.setRoomId("logic-wardens");
    logicRoom.setName("Wardens' Final Test");
    logicRoom.setDescription("Sentient switches argue about the truth.");
    logicRoom.setDifficulty(difficulty.getDisplayName());
    logicRoom.setEstimatedTimeMinutes(10);
    logicRoom.addPuzzle(logicPuzzle);

    rooms.add(mathRoom);
    rooms.add(wordRoom);
    rooms.add(logicRoom);
    return rooms;
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

        setSelectedDifficulty(Settings.Difficulty.fromName(level));
    }

    public Settings.Difficulty getSelectedDifficulty() {
        if (settings == null) {
            settings = new Settings();
        }
        Settings.Difficulty difficulty = settings.getDifficulty();
        return difficulty == null ? Settings.Difficulty.EASY : difficulty;
    }

    public void setSelectedDifficulty(Settings.Difficulty difficulty) {
        if (settings == null) {
            settings = new Settings();
        }
        Settings.Difficulty resolved = difficulty == null ? Settings.Difficulty.EASY : difficulty;
        if (resolved == settings.getDifficulty()) {
            return;
        }
        settings.setDifficulty(resolved);
        if (currentPlayer != null) {
            buildRoomsFor(currentPlayer);
        }

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
            return "Difficulty: " + getSelectedDifficulty().getDisplayName() + ". No puzzle loaded.";
        }
        String status = puzzle.getStatus();
        String prefix = "Difficulty: " + getSelectedDifficulty().getDisplayName() + ". ";

        if ("SOLVED".equalsIgnoreCase(status)) {
            return prefix + "You cracked the current puzzle!";
        }
        if ("ATTEMPTED".equalsIgnoreCase(status)) {
            return prefix + "The keypad is still locked. Try another code.";
        }
        return prefix + "A puzzle is waiting for you.";
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

    public boolean isNextRoomFinal() {
        return hasNextRoom() && currentRoomIndex + 1 == availableRooms.size() - 1;
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
