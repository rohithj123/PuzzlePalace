package com.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Captures the state of a puzzle attempt at a point in time including answers,
 * status, hints used, and timestamps for progress tracking.
 * 
 * @author Everlast Chigoba
 */
public class PuzzleProgressSnapshot {

    private final int puzzleId;
    private final String question;
    private String answer;
    private String status;
    private final List<String> hintsUsed;
    private LocalDateTime lastUpdated;

    public PuzzleProgressSnapshot(int puzzleId, String question) {
        this(puzzleId, question, null, "UNSOLVED", new ArrayList<>(), LocalDateTime.now());
    }

    public PuzzleProgressSnapshot(int puzzleId, String question, String answer, String status, List<String> hintsUsed, LocalDateTime lastUpdated) {
        this.puzzleId = puzzleId;
        this.question = question == null ? "" : question;
        this.answer = answer == null ? "" : answer;
        this.status = status == null ? "UNSOLVED" : status;
        this.hintsUsed = new ArrayList<>();
        if (hintsUsed != null) {
            for (String hint : hintsUsed) {
                if (hint != null && !hint.isBlank()) {
                    this.hintsUsed.add(hint.trim());
                }
            }
        }
        this.lastUpdated = lastUpdated == null ? LocalDateTime.now() : lastUpdated;
    }

    public int getPuzzleId() {
        return puzzleId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getHintsUsed() {
        return Collections.unmodifiableList(new ArrayList<>(hintsUsed));
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void recordAnswer(String answer, boolean solved) {
        this.answer = answer == null ? "" : answer.trim();
        this.status = solved ? "SOLVED" : (status == null ? "ATTEMPTED" : status);
        this.lastUpdated = LocalDateTime.now();
    }

    public void addHint(String hint) {
        if (hint == null || hint.isBlank()) {
            return;
        }
        hintsUsed.add(hint.trim());
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isSolved() {
        return "SOLVED".equalsIgnoreCase(status);
    }

    public int getHintCount() {
        return hintsUsed.size();
    }

    @Override
    public String toString() {
        return "PuzzleProgressSnapshot{" +
                "puzzleId=" + puzzleId +
                ", status='" + status + '\'' +
                ", hintsUsed=" + hintsUsed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PuzzleProgressSnapshot)) return false;
        PuzzleProgressSnapshot that = (PuzzleProgressSnapshot) o;
        return puzzleId == that.puzzleId;

    }

    @Override
    public int hashCode() {
        return Objects.hash(puzzleId);
    }
    
}
