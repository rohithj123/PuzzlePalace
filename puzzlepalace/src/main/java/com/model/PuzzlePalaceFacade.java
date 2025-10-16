package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PuzzlePalaceFacade {

    private Player currentPlayer;
    private Progress progress;
    private Leaderboard leaderboard;
    private Settings settings;
    private final PlayerManager playerManager;
    private List<Player> userList;
    private final String userDataPath;

    public PuzzlePalaceFacade() {
        this("json/users.json");
    }

    public PuzzlePalaceFacade(String userDataPath) {
        this.playerManager = new PlayerManager();
        this.userList = new ArrayList<>();
        this.userDataPath = userDataPath;
        this.settings = new Settings();
        loadUsers();
    }

    private void loadUsers() {
        List<Player> loadedPlayers = DataLoader.loadUsers(userDataPath);
        this.userList = new ArrayList<>();

        if (loadedPlayers == null) {
            return;
        }

        for (Player player : loadedPlayers) {
            if (player == null || player.getUsername() == null) {
                continue;
            }
            this.userList.add(player);
            if (playerManager.getPlayerByUsername(player.getUsername()) == null) {
                playerManager.addPlayer(player);
            }
        }
    }

    public Player login(String userName, String password) {
        if (userName == null || userName.isBlank() || password == null) {
            return null;
        }

        Player matchedPlayer = findUser(userName);
        if (matchedPlayer == null) {
            return null;
        }

        if (matchedPlayer.login(matchedPlayer.getUsername(), password)) {
            this.currentPlayer = matchedPlayer;
            return this.currentPlayer;
        }
        return null;
    }


    public void logout() {
    }

    public Player createAccount(String userName, String password) {
        return null;
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
    }

    public Room getCurrentRoom() {
        return null;
    }

    public List<Room> listAvailableRooms() {
        return null;
    }

    public Puzzle getPuzzle(int puzzleId) {
        return null;
    }

    public boolean submitPuzzleAnswer(int puzzleId, String answer) {
        return false;
    }

    public List<Clue> getCluesForPuzzle(int puzzleId) {
        return null;
    }

    public boolean useItem(int itemId, int targetId) {
        return false;
    }

    public List<Player> getUserList() {
        return Collections.unmodifiableList(userList);
    }

    private Player findUser(String userName) {
        for (Player player : userList) {
            if (player != null && player.getUsername() != null && player.getUsername().equalsIgnoreCase(userName)) {
                return player;
            }
        }
        return null;
    }
}
