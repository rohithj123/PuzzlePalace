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
    private final Score score;
    private int hintsUsed;
    private int hintsRemaining;
    private List<String> availableHints;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private static final String DEFAULT_SAVE_DIR = "data";
    private static final String DEFAULT_SAVE_FILE = "progress-default.txt";
    private static final String HINT_DELIMITER = " ||";

    public Progress() {
        this(null, null, null);
    }

    public Progress(Player player, List<String> availableHints) {
        this(player, null, availableHints);
    }

    public Progress(Player player, Score existingScore, List<String> availableHints) {
        this.currentRoom = 0;
        this.player = player;
        this.timer = 0L;
        this.isCompleted = false;
        this.score = existingScore != null ? existingScore : new Score();
        this.score.setProgress(this);
        this.hintsUsed = 0;
        this.availableHints = new ArrayList<>();
        this.hintsRemaining = 0;
        this.startTime = null;
        this.endTime = null;
        setAvailableHintsInternal(availableHints);
        resetHints();
    }

    public Score getScore() {
        return score;
    }

    public void setAvailableHints(List<String> hints) {
        replaceAvailableHints(hints);
    }
    public void replaceAvailableHints(List<String> hints) {
        setAvailableHintsInternal(hints);
        resetHints();
    }

    public void updateHintPool(List<String> hints) {
        setAvailableHintsInternal(hints);
        hintsRemaining = Math.max(availableHints.size() - hintsUsed, 0);    
    }

    public void beginGame(List<String> hints) {
        if (hints != null) {
            replaceAvailableHints(hints);
        } else {
            resetHints();
        }
        startGame();
    }


    public void startGame() {
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.isCompleted = false;
        resetHints();
        this.timer = 0L;
        
        score.setTimeTaken(0);
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

        score.setTimeTaken((int) Math.min(Integer.MAX_VALUE, Math.max(0L, timer / 1000L)));
        score.setHintsUsed(hintsUsed);
        
    }

    public boolean checkWin() {
        
        return isCompleted;
    }

    public void saveProgress() {
        String filename;
        if (player != null && player.getPlayerID() != null) {
            filename = DEFAULT_SAVE_DIR + "/progress-" + player.getPlayerID() + ".txt";
        } else {
            filename = DEFAULT_SAVE_DIR + "/" + DEFAULT_SAVE_FILE;
        }

        Path savePath = Paths.get(filename);
        try {
            Path parent = savePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }   
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<String> hintsToWrite = new ArrayList<>();
        if (availableHints != null) {
            for (String hint : availableHints) {
                if (hint != null) {
                    hintsToWrite.add(hint);
                }
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(savePath)) {
            writer.write("playerId=" + (player != null && player.getPlayerID() != null ? player.getPlayerID() : ""));
            writer.newLine();
            writer.write("currentRoom=" + currentRoom);
            writer.newLine();
            writer.write("timer=" + timer);
            writer.newLine();
            writer.write("completed=" + isCompleted);
            writer.newLine();
            writer.write("hintsUsed=" + hintsUsed);
            writer.newLine();
            writer.write("hintsRemaining=" + hintsRemaining);
            writer.newLine();
            writer.write("startTime=" + (startTime != null ? startTime : ""));
            writer.newLine();
            writer.write("endTime=" + (endTime != null ? endTime : ""));
            writer.newLine();
            writer.write("scorePoints=" + score.getPoints());
            writer.newLine();
            writer.write("scorePuzzlesSolved=" + score.getPuzzlesSolved());
            writer.newLine();
            writer.write("scoreHintsUsed=" + score.getHintsUsed());
            writer.newLine();
            writer.write("scoreTimeTaken=" + score.getTimeTaken());
            writer.newLine();
            writer.write("availableHints=" + String.join(HINT_DELIMITER, hintsToWrite));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadProgress() {
        String filename;
        if (player != null && player.getPlayerID() != null) {
            filename = DEFAULT_SAVE_DIR + "/progress-" + player.getPlayerID() + ".txt";
        } else {
            filename = DEFAULT_SAVE_DIR + "/" + DEFAULT_SAVE_FILE;
        }

        Path savePath = Paths.get(filename);
        if (!Files.exists(savePath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(savePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int idx = line.indexOf('=');
                if (idx == -1) {
                    continue;
                }
                String key = line.substring(0, idx);
                String value = line.substring(idx + 1);
                switch (key) {
                    case "currentRoom":
                        this.currentRoom = parseInt(value, currentRoom);
                        break;
                    case "timer":
                        this.timer = parseLong(value, timer);
                        break;
                    case "completed":
                        this.isCompleted = Boolean.parseBoolean(value);
                        break;
                    case "hintsUsed":
                        this.hintsUsed = parseInt(value, hintsUsed);
                        break;
                    case "hintsRemaining":
                        this.hintsRemaining = parseInt(value, hintsRemaining);
                        break;
                    case "startTime":
                        this.startTime = parseDateTime(value);
                        break;
                    case "endTime":
                        this.endTime = parseDateTime(value);
                        break;
                    case "scorePoints":
                        this.score.setPoints(parseInt(value, score.getPoints()));
                        break;
                    case "scorePuzzlesSolved":
                        this.score.setPuzzlesSolved(parseInt(value, score.getPuzzlesSolved()));
                        break;
                    case "scoreHintsUsed":
                        this.score.setHintsUsed(parseInt(value, score.getHintsUsed()));
                        break;
                    case "scoreTimeTaken":
                        this.score.setTimeTaken(parseInt(value, score.getTimeTaken()));
                        break;
                    case "availableHints":
                        this.availableHints = parseHints(value);
                        break;
                    default:
                        break;
                }
                
            }
            if (availableHints == null) {
                availableHints = new ArrayList<>();
            }
            hintsRemaining = Math.max(0, availableHints.size() - hintsUsed);
            score.setHintsUsed(hintsUsed);
            score.setTimeTaken((int) Math.min(Integer.MAX_VALUE, Math.max(0L, timer / 1000L)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String useHint() {
        if (availableHints == null || availableHints.isEmpty()) {
            return null;
        }
        if (hintsRemaining <= 0) {
            return null;
        }

        int index = hintsUsed;
        if (index < 0 || index >= availableHints.size()) {
            index = availableHints.size() - hintsRemaining;
            if (index < 0 || index >= availableHints.size()) {
                return null;
            }
        }

        String hint = availableHints.get(index);
        hintsUsed++;
        hintsRemaining = Math.max(availableHints.size() - hintsUsed, 0);
        score.setHintsUsed(hintsUsed);
        return hint;

        
    }

    public void resetHints() {
        this.hintsUsed = 0; 
        if (availableHints == null) {
            this.availableHints = new ArrayList<>();
            this.hintsRemaining = 0;
        } else {
            this.hintsRemaining = availableHints.size();
        }
        score.setHintsUsed(0);
    }

    private void setAvailableHintsInternal(List<String> hints) {
        if (this.availableHints == null) {
            this.availableHints = new ArrayList<>();
        } else {
            this.availableHints.clear();
        }
        if (hints == null) {
            return;
        }
        for (String hint : hints) {
            if (hint != null) {
                this.availableHints.add(hint);
            }
        }
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static long parseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> parseHints(String value) {
        List<String> hints = new ArrayList<>();
        if (value == null || value.isEmpty()) {
            return hints;
        }
        String[] parts = value.split(HINT_DELIMITER);
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                hints.add(part.trim());
            }
        }
        return hints;
    }
}
