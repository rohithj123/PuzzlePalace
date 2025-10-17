package com.model;

public class Clue {
    private final int id;
    private final String text;
    private boolean revealed;

    public Clue(int id, String text) {
        this.id = id;
        this.text = text == null ? "" : text;
        this.revealed = false;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void reveal() {
        this.revealed = true;
    }
}