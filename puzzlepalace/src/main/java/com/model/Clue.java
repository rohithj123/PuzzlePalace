package com.model;

/**
 * This represents a clue in the game.
 * This stores the clue's ID, text, and whether it has been revealed.
 */

public class Clue {
    private final int id;
    private final String text;
    private boolean revealed;

    /**
     * This creates a new clue with an ID and text.
     *
     * @param id the ID of the clue
     * @param text the text of the clue
     */
    public Clue(int id, String text) {
        this.id = id;
        this.text = text == null ? "" : text;
        this.revealed = false;
    }

    /**
     * This returns the ID of the clue.
     *
     * @return the clue ID
     */
    public int getId() {
        return id;
    }

    /**
     * This returns the text of the clue.
     *
     * @return the clue text
     */
    public String getText() {
        return text;
    }

    /**
     * This checks if the clue has been revealed.
     *
     * @return true if revealed, false otherwise
     */
    public boolean isRevealed() {
        return revealed;
    }

    /**
     * This reveals the clue.
     */
    public void reveal() {
        this.revealed = true;
    }
}