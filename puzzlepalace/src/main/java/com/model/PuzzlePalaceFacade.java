package com.model;

import java.util.List;

public class PuzzlePalaceFacade {

    private Player currentPlayer;
    private Progress progress;
    private Leaderboard leaderboard;
    private Settings settings;
    private final PlayerManager playerManager;
    private final String userDataPath;

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
        playerManager.loadPlayersFromFile(userDataPath);
    }

    public Player login(String userName, String password) {
        Player authenticated = playerManager.authenticate(userName, password);
        if (authenticated == null) {
            return null;
        }
        this.currentPlayer = authenticated;
        return this.currentPlayer;
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
        return playerManager.getAllPlayers();
    }
}
