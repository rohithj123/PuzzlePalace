package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable data class representing a player's overall puzzle progress summary.
 * A {@code PlayerProgressReport} aggregates puzzle progress snapshots and provides
 * metrics such as completion percentage, solved puzzle count, and total puzzles attempted.
 * 
 * Instances of this class are immutable once created.
 */
public class PlayerProgressReport {

    /** 
     * Percentage of completed puzzles (0–100). 
    */
    private final int completionPercent;

    /** 
     * Total number of solved puzzles. 
     * */
    private final int solvedCount;

    /** 
     * Total number of puzzles encountered or assigned. 
     * */
    private final int totalPuzzles;

    /** 
     * Immutable list of individual puzzle progress snapshots. 
     * */
    private final List<PuzzleProgressSnapshot> snapshots;

    /**
     * Constructs a new {@code PlayerProgressReport} instance with the specified progress data.
     *
     * @param completionPercent the player's completion percentage, clamped between 0 and 100
     * @param solvedCount       the number of puzzles solved
     * @param totalPuzzles      the total number of puzzles attempted or available
     * @param snapshots         a list of puzzle progress snapshots; may be {@code null}
     */
    public PlayerProgressReport(int completionPercent, int solvedCount, int totalPuzzles,
                                List<PuzzleProgressSnapshot> snapshots) {
        this.completionPercent = Math.max(0, Math.min(100, completionPercent));
        this.solvedCount = Math.max(0, solvedCount);
        this.totalPuzzles = Math.max(0, totalPuzzles);
        this.snapshots = new ArrayList<>();
        if (snapshots != null) {
            this.snapshots.addAll(snapshots);
        }
    }

    /**
     * Creates an empty {@code PlayerProgressReport} with zeroed values
     * and an empty snapshot list.
     *
     * @return a new empty {@code PlayerProgressReport} instance
     */
    public static PlayerProgressReport empty() {
        return new PlayerProgressReport(0, 0, 0, Collections.emptyList());
    }

    /**
     * Returns the completion percentage of the player.
     *
     * @return the completion percentage (0–100)
     */
    public int getCompletionPercent() {
        return completionPercent;
    }

    /**
     * Returns the number of puzzles solved by the player.
     *
     * @return the number of solved puzzles
     */
    public int getSolvedCount() {
        return solvedCount;
    }

    /**
     * Returns the total number of puzzles encountered or assigned to the player.
     *
     * @return the total puzzle count
     */
    public int getTotalPuzzles() {
        return totalPuzzles;
    }

    /**
     * Returns an unmodifiable copy of the list of puzzle progress snapshots.
     *
     * @return an immutable list of {@link PuzzleProgressSnapshot} instances
     */
    public List<PuzzleProgressSnapshot> getSnapshots() {
        return Collections.unmodifiableList(new ArrayList<>(snapshots));
    }
}
