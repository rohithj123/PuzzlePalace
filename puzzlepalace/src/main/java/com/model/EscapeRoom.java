package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EscapeRoom {
    private UUID roomID;
    private String name;
    private String description;
    private final List<Puzzle> puzzles;
    private final List<String> exits;

    public EscapeRoom() {
        this.puzzles = new ArrayList<>();
        this.exits = new ArrayList<>();
    }

    public EscapeRoom(UUID roomID, String name, String description, List<Puzzle> puzzles, List<String> exits) {
        this();
        this.roomID = roomID;
        this.name = name;
        this.description = description;
        if (puzzles != null) {
            this.puzzles.addAll(puzzles);
        }
        if (exits != null) {
            this.exits.addAll(exits);
        }
    }

    public UUID getRoomID() {
        return roomID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns an unmodifiable view of the puzzles contained in this EscapeRoom.
     */
    public List<Puzzle> getPuzzles() {
        return Collections.unmodifiableList(puzzles);
    }

    /**
     * Returns an unmodifiable view of the exits for this EscapeRoom.
     */
    public List<String> getExits() {
        return Collections.unmodifiableList(exits);
    }

    /**
     * Lightweight loader: set the room's UUID. Extend to actually populate
     * room data from a manager or data source as needed.
     */
    public void loadRoom(UUID id) {
        if (id == null) return;
        this.roomID = id;
        // TODO: populate name/description/puzzles/exits from a data source if available
    }

    /**
     * A room is completed when every contained Puzzle is non-null and has status "SOLVED"
     * (case-insensitive). If there are no puzzles the room is not considered completed.
     */
    public boolean isCompleted() {
        if (puzzles == null || puzzles.isEmpty()) {
            return false;
        }

        for (Puzzle puzzle : puzzles) {
            if (puzzle == null) {
                return false;
            }
            // Puzzle.status is protected in Puzzle and this class is in same package -> accessible
            if (!"SOLVED".equalsIgnoreCase(puzzle.status)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EscapeRoom)) return false;
        EscapeRoom that = (EscapeRoom) o;
        return Objects.equals(roomID, that.roomID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomID);
    }

    @Override
    public String toString() {
        return "EscapeRoom{" +
                "roomID=" + roomID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", puzzles=" + puzzles.size() +
                ", exits=" + exits +
                '}';
    }
}
