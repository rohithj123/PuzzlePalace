package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Thread-safe manager responsible for storing, authenticating, and retrieving
 * player accounts within the application.
 * 
 * @author Everlast Chigoba
 */
public class PlayerManager 
{
    private final List<Player> players;

    public PlayerManager() 
    {
        this.players = new ArrayList<>();
    }

    public synchronized boolean addPlayer(Player player) 
    {
        if (player == null || player.getUsername() == null || player.getUsername().isBlank()) 
        {
            return false;
        }

        String newName = player.getUsername().trim();
        for (Player p : players) {
            if (p != null && p.getUsername() != null && p.getUsername().equalsIgnoreCase(newName)) 
            {
                return false;
            }
        }

        players.add(player);
        return true;
    }

    public synchronized Player getPlayerById(UUID id) 
    {
        if (id == null) return null;
        for (Player p : players) {
            if (p != null && id.equals(p.getPlayerID())) 
            {
                return p;
            }
        }
        return null;
    }

    public synchronized List<Player> getAllPlayers() 
    {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }

    public synchronized List <Player> loadPlayersFromFile(String filePath) {
        List<Player> loadedPlayers = DataLoader.loadUsers(filePath);
        players.clear();
        if (loadedPlayers != null) {
            for (Player player : loadedPlayers) {
                addPlayer(player);
            }
        }
        return getAllPlayers();
    }

    public synchronized Player getPlayerByUsername(String username) 
    {
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

    public synchronized boolean removePlayer(Player player) {
        if (player == null) return false;
        return players.remove(player);  
    }

    public synchronized boolean updatePlayer(Player updated)
     {
        if (updated == null) return false;
        UUID id = updated.getPlayerID();
        if (id == null) return false;
        for (int i = 0; i < players.size(); i++) 
        {
            Player p = players.get(i);
            if (p != null && id.equals(p.getPlayerID())) 
            {
                players.set(i, updated);
                return true;
            }
        }
        return false;
    }
}
