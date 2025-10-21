package com.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Room {

    private String roomId;
    private String name;
    private String description;
    private String difficulty;
    private int estimatedTimeMinutes;
    private final List<Puzzle> puzzles;
    private final List<String> exits;

    public Room() {
        this.puzzles = new ArrayList<>();
        this.exits = new ArrayList<>();
    }

  
    public Room(String roomId,
                String name,
                String description,
                String difficulty,
                int estimatedTimeMinutes,
                List<Puzzle> puzzles,
                List<String> exits) {
        this();
        this.roomId = roomId;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        if (puzzles != null) {
            this.puzzles.addAll(puzzles);
        }
        if (exits != null) {
            this.exits.addAll(exits);
        }
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(int estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

  
    public List<Puzzle> getPuzzles() {
        return Collections.unmodifiableList(puzzles);
    }

    public void setPuzzles(List<Puzzle> puzzles) {
        this.puzzles.clear();
        if (puzzles != null) {
            this.puzzles.addAll(puzzles);
        }
    }

    public void addPuzzle(Puzzle puzzle) {
        if (puzzle != null) {
            this.puzzles.add(puzzle);
        }
    }

    public List<String> getExits() {
        return Collections.unmodifiableList(exits);
    }

    public void setExits(List<String> exits) {
        this.exits.clear();
        if (exits != null) {
            this.exits.addAll(exits);
        }
    }

    public void addExit(String exit) {
        if (exit != null && !exit.isBlank()) {
            this.exits.add(exit);
        }
    }


    public boolean isCompleted() {
        for (Puzzle puzzle : puzzles) {
            if (puzzle == null) {
                return false;
            }
            if (!"SOLVED".equalsIgnoreCase(puzzle.status)) {
                return false;
            }
        }
        return true;
    }

    public Puzzle getPuzzleById(int puzzleId) {
        for (Puzzle puzzle : puzzles) {
            if (puzzle != null && puzzle.puzzleId == puzzleId) {
                return puzzle;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                ", name='" + name + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", estimatedTimeMinutes=" + estimatedTimeMinutes +
                ", puzzles=" + puzzles.size() +
                ", exits=" + exits +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(roomId, room.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
}