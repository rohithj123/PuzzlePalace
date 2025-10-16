package com.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Progress {

    private int currentRoom;
    private Player player;
    private long timer;
    private boolean isCompleted;
    private Score score;
    private int hintsUsed;
    private int hintsRemaining;
    private List<String> availableHints;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private static final String DEFAULT_SAVE_DIR = "data";

    public Progress() {
        this.currentRoom = 0;
        this.player = null;
        this.timer = 0L;
        this.isCompleted = false;
        this.score = new Score();
        this.hintsUsed = 0;
        this.availableHints = new ArrayList<>();
        this.hintsRemaining = 0;
        this.startTime = null;
        this.endTime = null;
    }

    public Progress(Player player, List<String> availableHints) {
        this();
        this.player = player;
        if (availableHints != null) {
            this.availableHints = new ArrayList<>(availableHints);
        } else {
            this.availableHints = new ArrayList<>();
        }
        resetHints();
    }

    public void startGame() {
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.isCompleted = false;
        this.hintsUsed = 0;
        this.hintsRemaining = availableHints.size();
        this.timer = 0L;
        
    }

    public void endGame() {
        if (this.startTime == null) {

            this.endTime = LocalDateTime.now();
            this.timer = 0L;
        } else {
            this.endTime = LocalDateTime.now();
            Duration d = Duration.between(startTime, endTime);
            this.timer = d.toMillis();
        }
        this.isCompleted = true;
        
    }

    public boolean checkWin() {
        
        return isCompleted;
    }

    public void saveProgress() {
        String filename;
        if (player != null && player.getPlayerID() != null) {
            filename = DEFAULT_SAVE_DIR + "/progress-" + player.getPlayerID() + ".txt";
        }

    }

    public void loadProgress() {
    }

    public String useHint() {

        return null;
    }

    public void resetHints() {
    }
}
