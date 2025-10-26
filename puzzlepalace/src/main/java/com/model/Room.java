package com.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a room in the game.
 * Each room has puzzles, exits, a difficulty, and a score.
 */
public class Room {

    private String roomId;
    private String name;
    private String description;
    private String difficulty;
    private int estimatedTimeMinutes;
    private final List<Puzzle> puzzles;
    private final List<String> exits;
    private Score score;

        /** Creates an empty room. */
    public Room() {
        this.puzzles = new ArrayList<>();
        this.exits = new ArrayList<>();
    }

  
    /**
     * Creates a room with details.
     *
     * @param roomId ID of the room
     * @param name room name
     * @param description room description
     * @param difficulty room difficulty level
     * @param estimatedTimeMinutes estimated time to finish
     * @param puzzles list of puzzles in this room
     * @param exits list of exits from the room
     * @param score score for this room
     */
    public Room(String roomId,
                String name,
                String description,
                String difficulty,
                int estimatedTimeMinutes,
                List<Puzzle> puzzles,
                List<String> exits,
                Score score) {
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
        this.score = score;
    }

        /** Returns the room ID. */
    public String getRoomId() {
        return roomId;
    }

        /** Sets the room ID. */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

        /** Returns the room name. */
    public String getName() {
        return name;
    }

        /** Sets the room name. */
    public void setName(String name) {
        this.name = name;
    }

        /** Returns the room description. */
    public String getDescription() {
        return description;
    }

        /** Sets the room description. */
    public void setDescription(String description) {
        this.description = description;
    }

        /** Returns the difficulty label. */
    public String getDifficulty() {
        return difficulty;
    }
    /** Sets the room difficulty. */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

        /** Returns the estimated time to complete in minutes. */
    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

        /** Sets the estimated time to complete. */
    public void setEstimatedTimeMinutes(int estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

  
        /** Returns an unmodifiable list of puzzles. */
    public List<Puzzle> getPuzzles() {
        return Collections.unmodifiableList(puzzles);
    }

    public void setPuzzles(List<Puzzle> puzzles) {
        this.puzzles.clear();
        if (puzzles != null) {
            this.puzzles.addAll(puzzles);
        }
    }

        /** Adds a puzzle to this room. */
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

    public void setScoreDetails(Score score) {
        this.score = score;
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

    /**
     * Finds a puzzle by its ID.
     *
     * @param puzzleId the ID to look for
     * @return the puzzle if found, or null
     */
    public Puzzle getPuzzleById(int puzzleId) {
        for (Puzzle puzzle : puzzles) {
            if (puzzle != null && puzzle.puzzleId == puzzleId) {
                return puzzle;
            }
        }
        return null;
    }

        /** Returns a string summary of the room. */
    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                ", name='" + name + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", estimatedTimeMinutes=" + estimatedTimeMinutes +
                ", puzzles=" + puzzles.size() +
                ", exits=" + exits +
                ", score=" + (score != null ? score.calculateScore() : "null") +
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


    public Score getScoreDetails() {
        return score;
    }
}