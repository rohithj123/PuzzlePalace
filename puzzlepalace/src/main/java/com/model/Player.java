package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Objects;
import java.lang.reflect.Method;


public class Player {
    private UUID playerID;
    private String username;
    private String email;
    private String passwordHash;
    private boolean isGuest;
    private final List<Item> inventory;
    private int score;
    private Certificate certificate;

    public Player() {
        this.playerID = UUID.randomUUID();
        this.inventory = new ArrayList<>();
        this.isGuest = true;
        this.score = 0;
    }

    public Player(String username, String email, String rawPassword) {
        this();
        this.username = username;
        this.email = email;
        this.isGuest = false;
        setPassword(rawPassword);
    }

    public void setPassword(String raw) {
        if(raw == null) {
            this.passwordHash = null;
            return;
        }
        this.passwordHash = Integer.toHexString(Objects.hash(raw, username, email));
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public String getUsername() {
        return username;
    }

    public boolean addItem(Item item) {
        if (item == null) return false;
        synchronized (inventory) {
            return inventory.add(item);
        }
    }

    public boolean removeItem(Item item) {
        if (item == null) return false;
        synchronized (inventory) {
            return inventory.remove(item);
        } 
    }

    public boolean hasItem(Item item) {
        if(item == null) return false;
        synchronized (inventory) {
            for (Item it : inventory) {
                try {
                    if (it.getId() == item.getId()) return true;
                } catch (Exception e) {
                    if (it.toString().equals(item.toString())) return true;
                }
            }
            return false;
        }
    }

    public boolean useItem(Item item, Object target) {
        if(item == null) return false;
        synchronized (inventory) {
            boolean present = inventory.stream().anyMatch(i -> {
                try {
                    return i.getId() == item.getId();
                } catch (Exception e) {
                    return i.toString().equals(item.toString());
                }
            });
            if(!present) return false;

            boolean removed = inventory.remove(item);
            if(!removed) {
                inventory.removeIf(i -> {
                    try {
                        return i.getId() == item.getId();
                    } catch (Exception e) {
                        return i.toString().equals(item.toString());
                    }
                });
            }
            return true;
        }
    }

    public boolean solvePuzzle( Puzzle puzzle, String answer) {
        if(puzzle == null) return false;
        boolean solved = false;
        try {
            solved = puzzle.trySolve(answer);
        } catch (NoSuchMethodError | AbstractMethodError | UnsupportedOperationException e) {
            try {
                solved = puzzle.solve();
            } catch (Exception ignored) {
                solved = false;
            }
        } catch (Throwable ignored) {
            solved = false;
        }
        if(solved) {
            int earned = 100;
            this.score += earned;
        }
        return solved;
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        if (this.username != null && this.username.equals(username)) {
            return false;
        }

        if(this.passwordHash == null) {
            this.username = username;
            this.setPassword(password);
            this.isGuest = false;
            return true;
        }

        String candidateHash = Integer.toHexString(Objects.hash(password, this.username, this.email));
        boolean ok = candidateHash.equals(this.passwordHash);
        if(ok) {
            this.isGuest = false;
        }
        return ok;
        
    }

    public void logout() {
        this.isGuest = true;
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
