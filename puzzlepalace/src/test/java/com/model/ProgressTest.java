package com.model;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ProgressTest {

    @Test
    public void beginGame_initialisesHintsAndStartTime() {
        Progress progress = new Progress();
        progress.beginGame(List.of("Alpha", "Beta"));

        assertEquals("Alpha", progress.useHint());
        assertEquals("Beta", progress.useHint());
        assertNull(progress.useHint());
        assertEquals(2, progress.getScore().getHintsUsed());
    }

    @Test
    public void updateHintPool_adjustsRemainingHints() {
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
    public void updateHintPool_withNullClearsHints() {
        Progress progress = new Progress();
        progress.beginGame(List.of("Hint"));
        progress.updateHintPool(null);

        assertNull(progress.useHint());
        assertEquals(0, progress.getScore().getHintsUsed());
    }

    @Test
    public void resetHints_replenishesPool() {
        Progress progress = new Progress();
        progress.beginGame(List.of("One", "Two"));
        progress.useHint();
        progress.resetHints();

        assertEquals("One", progress.useHint());
    }

    @Test
    public void endGame_recordsElapsedTimeEvenWithoutStart() {
        Progress progress = new Progress();
        progress.endGame();

        assertTrue(progress.checkWin());
        assertEquals(0, progress.getScore().getTimeTaken());
    }

    @Test
    public void beginGame_withEmptyHintListBehavesGracefully() {
        Progress progress = new Progress();
        progress.beginGame(List.of());
        assertNull(progress.useHint());
        assertEquals(0, progress.getScore().getHintsUsed());
    }

    @Test
    public void updateHintPool_replacesExistingHintsCorrectly() {
        Progress progress = new Progress();
        progress.beginGame(List.of("OldHint"));
        assertEquals("OldHint", progress.useHint());

        progress.updateHintPool(List.of("NewHint1", "NewHint2"));
        assertEquals("NewHint1", progress.useHint());
        assertEquals("NewHint2", progress.useHint());
        assertNull(progress.useHint());
    }
}
