package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Thread-safe manager responsible for storing, authenticating, and retrieving
 * {@link Player} accounts within the application.
 *
 * The {@code PlayerManager} acts as a centralized registry for all active or
 * registered players. It provides synchronized methods to ensure safe access and
 * modification in concurrent environments.
 *
 * All public methods are synchronized to guarantee thread safety when used in
 * multi-threaded game sessions.
 */
public class PlayerManager {

    /** Internal thread-safe list of all managed players. */
    private final List<Player> players;

    /**
     * Constructs an empty {@code PlayerManager} instance.
     * Initializes the internal player list.
     */
    public PlayerManager() {
        this.players = new ArrayList<>();
    }

    /**
     * Adds a new {@link Player} to the manager if the username is unique and valid.
     *
     * @param player the player to add
     * @return {@code true} if the player was successfully added;
     *         {@code false} if the player is null, has an invalid username,
     *         or the username already exists
     */
    public synchronized boolean addPlayer(Player player) {
        if (player == null || player.getUsername() == null || player.getUsername().isBlank()) {
            return false;
        }

        String newName = player.getUsername().trim();
        for (Player p : players) {
            if (p != null && p.getUsername() != null && p.getUsername().equalsIgnoreCase(newName)) {
                return false;
            }
        }

        players.add(player);
        return true;
    }

    /**
     * Retrieves a {@link Player} by its unique {@link UUID} identifier.
     *
     * @param id the player's UUID
     * @return the matching {@code Player}, or {@code null} if not found
     */
    public synchronized Player getPlayerById(UUID id) {
        if (id == null) return null;
        for (Player p : players) {
            if (p != null && id.equals(p.getPlayerID())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable snapshot of all managed players.
     *
     * @return an immutable {@link List} of players
     */
    public synchronized List<Player> getAllPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }

    /**
     * Loads players from a specified file path and replaces the current player list.
     * 
     * Uses {@link DataLoader#loadUsers(String)} to read persisted player data.
     * Existing players are cleared before the new list is applied.
     *
     * @param filePath the file path to load player data from
     * @return an immutable list of loaded players
     */
    public synchronized List<Player> loadPlayersFromFile(String filePath) {
        List<Player> loadedPlayers = DataLoader.loadUsers(filePath);
        players.clear();
        if (loadedPlayers != null) {
            for (Player player : loadedPlayers) {
                addPlayer(player);
            }
        }
        return getAllPlayers();
    }

    /**
     * Retrieves a {@link Player} by its username, ignoring case.
     *
     * @param username the username to search for
     * @return the matching player, or {@code null} if not found or invalid
     */
    public synchronized Player getPlayerByUsername(String username) {
        if (username == null) {
            return null;
        }
        String cleaned = username.trim();
        for (Player p : players) {
            if (p != null && p.getUsername() != null && p.getUsername().equalsIgnoreCase(cleaned)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Authenticates a player using a username and password.
     * 
     * Delegates login verification to {@link Player#login(String, String)}.
     * Returns the authenticated {@link Player} if credentials are valid, or {@code null} otherwise.
     * 
     *
     * @param username the username to authenticate
     * @param password the player's password
     * @return the authenticated player, or {@code null} if authentication fails
     */
    public synchronized Player authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        Player candidate = getPlayerByUsername(username);
        if (candidate == null) {
            return null;
        }
        return candidate.login(username, password) ? candidate : null;
    }

    /**
     * Removes the specified {@link Player} from the manager.
     *
     * @param player the player to remove
     * @return {@code true} if the player was successfully removed; {@code false} otherwise
     */
    public synchronized boolean removePlayer(Player player) {
        if (player == null) return false;
        return players.remove(player);
    }

    /**
     * Updates an existing player's record by replacing the old instance with the new one.
     * 
     * The player is identified by matching the {@link UUID} returned from
     * {@link Player#getPlayerID()}.
     * 
     *
     * @param updated the updated player instance
     * @return {@code true} if the player record was successfully updated; {@code false} otherwise
     */
    public synchronized boolean updatePlayer(Player updated) {
        if (updated == null) return false;
        UUID id = updated.getPlayerID();
        if (id == null) return false;
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p != null && id.equals(p.getPlayerID())) {
                players.set(i, updated);
                return true;
            }
        }
        return false;
    }
}

