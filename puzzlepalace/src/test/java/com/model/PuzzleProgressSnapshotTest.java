package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;

public class PuzzleProgressSnapshotTest {

    @Test
    public void constructorNormalisesNullValues() {
        PuzzleProgressSnapshot snapshot = new PuzzleProgressSnapshot(5, null, null, null,
                Arrays.asList(" Hint ", null, ""), null);

        assertEquals("", snapshot.getQuestion());
        assertEquals("", snapshot.getAnswer());
        assertEquals("UNSOLVED", snapshot.getStatus());
        assertEquals(1, snapshot.getHintsUsed().size());
        assertTrue(snapshot.getHintsUsed().contains("Hint"));
    }

    @Test
    public void recordAnswerUpdatesStatusAndTimestamp() {
        PuzzleProgressSnapshot snapshot = new PuzzleProgressSnapshot(6, "Q");
        LocalDateTime before = snapshot.getLastUpdated();
        snapshot.recordAnswer("guess", false);
        assertEquals("guess", snapshot.getAnswer());
        assertEquals("UNSOLVED", snapshot.getStatus());
        assertTrue(snapshot.getLastUpdated().isAfter(before) || snapshot.getLastUpdated().isEqual(before));

        snapshot.recordAnswer("solution", true);
        assertEquals("SOLVED", snapshot.getStatus());
    }

    @Test
    public void addHintTrimsAndIgnoresBlank() {
        PuzzleProgressSnapshot snapshot = new PuzzleProgressSnapshot(7, "Q");
        snapshot.addHint("   First   ");
        snapshot.addHint("   ");
        assertEquals(1, snapshot.getHintCount());
        assertTrue(snapshot.getHintsUsed().contains("First"));
    }

    @Test
    public void equalsAndHashCodeUsePuzzleIdOnly() {
        PuzzleProgressSnapshot first = new PuzzleProgressSnapshot(10, "Q");
        PuzzleProgressSnapshot second = new PuzzleProgressSnapshot(10, "Other");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        PuzzleProgressSnapshot different = new PuzzleProgressSnapshot(11, "Q");
        assertFalse(first.equals(different));
    }
}