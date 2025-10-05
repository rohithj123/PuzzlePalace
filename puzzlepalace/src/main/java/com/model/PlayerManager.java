package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    private List<Player> players;

    public PlayerManager() {
        players = new ArrayList<>();
        System.out.println("PlayerManager() constructor called (stub)");
    }

    public void addPlayer(Player player) {
        System.out.println("addPlayer() called for " + player.getUsername() + " (stub)");
        players.add(player);
    }

    public Player getPlayerById(UUID id) {
        System.out.println("getPlayerById() called (stub)");
        for (Player p : players) {
            if (p.getPlayerID().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public List<Player> getAllPlayers() {
        System.out.println("getAllPlayers() called (stub)");
        return new ArrayList<>(players);
    }
}
