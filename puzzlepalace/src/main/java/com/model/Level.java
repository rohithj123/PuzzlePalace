package com.model;
/**
 * This represents a level in the game.
 * This stores the level number, name, and difficulty.
 */

public class Level {

    private int levelNumber;
    private String levelName;
    private String difficulty;

    /**
     * This loads the level data.
     */
    public void loadLevel() { }

     /**
     * This starts the level.
     */
    public void startLevel() { }

    /**
     * This marks the level as completed.
     */
    public void completeLevel() { }

    /**
     * This checks if the level is completed.
     *
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() { 
        return false; 
    }
}