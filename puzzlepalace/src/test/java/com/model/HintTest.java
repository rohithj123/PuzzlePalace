package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class HintTest {

    @Test
    public void getHintRespectsMaximumAndTracksPenaltyUsage() {
        Hint hint = new Hint(Arrays.asList("First", "Second", "Third"), 2);

        assertEquals("First", hint.getHint());
        assertEquals("Second", hint.getHint());

        assertEquals("All hints have been used.", hint.getHint());
        assertEquals(2, hint.getHintsUsed());
        assertEquals(2, hint.getPenaltyHintsUsed());
        assertEquals(0, hint.getBonusHintsUsed());
        assertFalse(hint.checkHintLimit());
    }

    @Test
    public void markLastHintFreeReducesPenaltyAndAddsBonus() {
        Hint hint = new Hint(Arrays.asList("Only"), 5);

        assertEquals("Only", hint.getHint());
        assertEquals(1, hint.getPenaltyHintsUsed());

        hint.markLastHintFree();
        assertEquals(0, hint.getPenaltyHintsUsed());
        assertEquals(1, hint.getBonusHintsUsed());

        hint.markLastHintFree();
        assertEquals(0, hint.getPenaltyHintsUsed());
        assertEquals(1, hint.getBonusHintsUsed());
    }

    @Test
    public void availableHintsSnapshotIsImmutableAndIndependent() {
        Hint hint = new Hint(Arrays.asList("Alpha", "Beta"), 3);
        List<String> snapshot = hint.getAvailableHintsSnapshot();

        assertEquals(Arrays.asList("Alpha", "Beta"), snapshot);

        try {
            snapshot.add("Gamma");
            assertFalse("Snapshot list should be immutable", true);
        } catch (UnsupportedOperationException expected) {
        }

        hint.setAvailableHints(Arrays.asList("Delta"));
        assertEquals(Arrays.asList("Delta"), hint.getAvailableHintsSnapshot());
    }

    @Test
    public void resetPuzzleClearsUsageCounters() {
        Hint hint = new Hint(Arrays.asList("One", "Two"), 2);
        hint.getHint();
        hint.getHint();
        assertEquals(2, hint.getHintsUsed());

        hint.resetHintsUsed();
        assertEquals(0, hint.getHintsUsed());
        assertEquals(0, hint.getPenaltyHintsUsed());
        assertEquals(0, hint.getBonusHintsUsed());
        assertTrue(hint.checkHintLimit());
    }
}