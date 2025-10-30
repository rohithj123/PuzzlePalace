package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ProgressTest {

    @Test
    public void beginGameInitialisesHintsAndStartTime() {
        Progress progress = new Progress();
        progress.beginGame(List.of("Alpha", "Beta"));

        assertEquals("Alpha", progress.useHint());
        assertEquals("Beta", progress.useHint());
        assertNull(progress.useHint());
        assertEquals(2, progress.getScore().getHintsUsed());
    }

    @Test
    public void updateHintPoolAdjustsRemainingHints() {
        Progress progress = new Progress();
        progress.beginGame(Arrays.asList("A", "B"));
        assertEquals("A", progress.useHint());

        progress.updateHintPool(Arrays.asList("C", "D", "E"));
        assertEquals("D", progress.useHint());
        assertEquals("E", progress.useHint());
        assertNull(progress.useHint());
        assertEquals(3, progress.getScore().getHintsUsed());
    }

    @Test
    public void updateHintPoolWithNullClearsHints() {
        Progress progress = new Progress();
        progress.beginGame(List.of("Hint"));
        progress.updateHintPool(null);

        assertNull(progress.useHint());
        assertEquals(0, progress.getScore().getHintsUsed());
    }

    @Test
    public void resetHintsReplenishesPool() {
        Progress progress = new Progress();
        progress.beginGame(List.of("One", "Two"));
        progress.useHint();
        progress.resetHints();

        assertEquals("One", progress.useHint());
    }

    @Test
    public void endGameRecordsElapsedTimeEvenWithoutStart() {
        Progress progress = new Progress();
        progress.endGame();
        assertTrue(progress.checkWin());
        assertEquals(0, progress.getScore().getTimeTaken());
    }
}
