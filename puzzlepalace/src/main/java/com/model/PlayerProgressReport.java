package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerProgressReport {

    private final int completionPercent;
    private final int solvedCount;
    private final int totalPuzzles;
    private final List<PuzzleProgressSnapshot> snapshots;

    public PlayerProgressReport(int completionPercent, int solvedCount, int totalPuzzles, List<PuzzleProgressSnapshot> snapshots) {
        this.completionPercent = Math.max(0, Math.min(100, completionPercent));
        this.solvedCount = Math.max(0, solvedCount);
        this.totalPuzzles = Math.max(0, totalPuzzles);
        this.snapshots = new ArrayList<>();
        if (snapshots != null) {
            this.snapshots.addAll(snapshots);
        }
    }

    public static PlayerProgressReport empty() {
        return new PlayerProgressReport(0, 0, 0, Collections.emptyList());
    }

    public int getCompletionPercent() {
        return completionPercent;
    }

    public int getSolvedCount() {
        return solvedCount;
    }
    
    public int getTotalPuzzles() {
        return totalPuzzles;
    }

    public List<PuzzleProgressSnapshot> getSnapshots() {
        return Collections.unmodifiableList(new ArrayList<>(snapshots));
    }
}
