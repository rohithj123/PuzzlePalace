package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Player {
    private UUID playerID;
    private String username;
    private String email;
    private String passwordHash;
    private boolean isGuest;
    private List<Item> inventory;
    private int score;
    private Certificate certificate;

    public Player() {
        this.playerID = UUID.randomUUID();
        this.inventory = new ArrayList<>();

    }

    public Player(String username, String email) {
        this();
        this.username = username;
        this.email = email;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public String getUsername() {
        return username;
    }

    public boolean addItem(Item item) {
        return inventory.add(item);
    }

    public boolean removeItem(Item item) {
        return inventory.remove(item);  
    }

    public boolean solvePuzzle( Puzzle puzzle, String answer) {
        return false;
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        this.username = username;
        return true;
    }

    public void logout() {
        this.username = null;
    }

    public int getScore() {
        return score;
    }

    public void setCertificate(Certificate c) {
        this.certificate = c;
    }

    @Override
    public String toString() {
        return "Player{" + "id=" + playerID + ", username=" + username + "}";
    }


    
}
