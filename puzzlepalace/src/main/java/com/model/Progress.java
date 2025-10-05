package com.model;

import java.time.LocalDateTime;
import java.util.List;


public class Progress {

    private int currentRoom;
    private Player player;
    private int timer;
    private boolean isCompleted;
    private int score;
    private int hintsUsed;
    private int hintsRemaining;
    private List<String> availableHints;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public void startGame() {
        
    }

    public void endGame() {
        
    }

    public boolean checkWin() {
        
        return false;
    }

    public void saveProgress() {

    }

    public void loadProgress() {
    }

    public String useHint() {

        return null;
    }

    public void resetHints() {
    }
}
