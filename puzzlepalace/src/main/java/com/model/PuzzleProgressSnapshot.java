package com.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Captures the state of a single puzzle attempt at a point in time.
 *
 * Instances are mutable: calling {@link #recordAnswer(String, boolean)} or
 * {@link #addHint(String)} updates the stored answer/status/hints and updates
 * the {@link #lastUpdated} timestamp. For external callers the hints list is
 * exposed as an unmodifiable copy via {@link #getHintsUsed()}.
 */
public class PuzzleProgressSnapshot {

    /**
     * Unique identifier for the puzzle this snapshot relates to.
     */
    private final int puzzleId;

    /**
     * Question text or description for the puzzle. Never null (empty string if
     * not supplied).
     */
    private final String question;

    /**
     * Most recent answer provided by the player. Empty string represents no answer.
     */
    private String answer;

    /**
     * Current status string (e.g. "UNSOLVED", "ATTEMPTED", "SOLVED"). Never null.
     */
    private String status;

    /**
     * Ordered list of hints used for this puzzle. Null/blank hints are filtered
     * out at construction and when added.
     */
    private final List<String> hintsUsed;

    /**
     * Timestamp of the last change to this snapshot (answer added, hint added,
     * or constructed). Never null.
     */
    private LocalDateTime lastUpdated;

    /**
     * Constructs a new snapshot for the given puzzle id and question. The
     * created snapshot defaults to status "UNSOLVED", no answer, an empty list
     * of hints, and {@link LocalDateTime#now()} as the last-updated timestamp.
     *
     * @param puzzleId numeric puzzle identifier
     * @param question question or description text (may be {@code null})
     */
    public PuzzleProgressSnapshot(int puzzleId, String question) {
        this(puzzleId, question, null, "UNSOLVED", new ArrayList<>(), LocalDateTime.now());
    }

    /**
     * Full constructor allowing all fields to be specified. Null values for
     * {@code question}, {@code answer} or {@code status} are normalized to
     * non-null defaults. The provided {@code hintsUsed} list is copied and
     * filtered to remove null/blank entries. If {@code lastUpdated} is {@code null}
     * then {@link LocalDateTime#now()} is used.
     *
     * @param puzzleId    numeric puzzle identifier
     * @param question    question text (may be {@code null}; becomes empty string)
     * @param answer      most recent answer (may be {@code null}; becomes empty string)
     * @param status      status string (may be {@code null}; becomes "UNSOLVED")
     * @param hintsUsed   list of previously used hints (may be {@code null})
     * @param lastUpdated timestamp for last update (may be {@code null})
     */
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

    /**
     * Returns the puzzle id this snapshot represents.
     *
     * @return puzzle id
     */
    public int getPuzzleId() {
        return puzzleId;
    }

    /**
     * Returns the puzzle question/description text.
     *
     * @return question text (never {@code null})
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Returns the most recent answer recorded for this puzzle.
     *
     * @return answer string (never {@code null}; empty string if none)
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Returns the current status string for this snapshot (case preserved).
     *
     * @return status string (never {@code null})
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns an unmodifiable copy of the list of hints used for this puzzle.
     * The returned list is a snapshot and further modifications to the snapshot
     * instance will not affect the returned list.
     *
     * @return unmodifiable list of hint strings
     */
    public List<String> getHintsUsed() {
        return Collections.unmodifiableList(new ArrayList<>(hintsUsed));
    }

    /**
     * Returns the timestamp when this snapshot was last modified.
     *
     * @return last-updated timestamp (never {@code null})
     */
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Record an answer for the puzzle. The provided answer is trimmed (or set
     * to empty string if null) and the status will be set to "SOLVED" when
     * {@code solved} is true. If {@code solved} is false and the current status
     * is {@code null} the status will become "ATTEMPTED". The {@link #lastUpdated}
     * timestamp is updated to {@link LocalDateTime#now()}.
     *
     * @param answer answer submitted by the player (may be {@code null})
     * @param solved whether this answer solved the puzzle
     */
    public void recordAnswer(String answer, boolean solved) {
        this.answer = answer == null ? "" : answer.trim();
        this.status = solved ? "SOLVED" : (status == null ? "ATTEMPTED" : status);
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Adds a hint to the snapshot's hints list. Null or blank strings are
     * ignored. The added hint is trimmed and {@link #lastUpdated} is updated.
     *
     * @param hint hint text to record (may be {@code null} or blank)
     */
    public void addHint(String hint) {
        if (hint == null || hint.isBlank()) {
            return;
        }
        hintsUsed.add(hint.trim());
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Returns whether the snapshot represents a solved puzzle. Comparison is
     * case-insensitive.
     *
     * @return {@code true} if status equals "SOLVED" (case-insensitive)
     */
    public boolean isSolved() {
        return "SOLVED".equalsIgnoreCase(status);
    }

    /**
     * Returns the number of hints recorded for this snapshot.
     *
     * @return hint count (>= 0)
     */
    public int getHintCount() {
        return hintsUsed.size();
    }

    /**
     * Human-readable representation including puzzle id, status and hint list.
     *
     * @return debug-friendly string
     */
    @Override
    public String toString() {
        return "PuzzleProgressSnapshot{" +
                "puzzleId=" + puzzleId +
                ", status='" + status + '\'' +
                ", hintsUsed=" + hintsUsed +
                '}';
    }

    /**
     * Equality is based solely on {@link #puzzleId} — two snapshots with the
     * same puzzle id are considered equal (this mirrors identity by puzzle).
     *
     * @param o other object to compare
     * @return {@code true} if {@code o} is a {@code PuzzleProgressSnapshot} with the same id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PuzzleProgressSnapshot)) return false;
        PuzzleProgressSnapshot that = (PuzzleProgressSnapshot) o;
        return puzzleId == that.puzzleId;

    }

    /**
     * Hash code derived from {@link #puzzleId}.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(puzzleId);
    }

}
