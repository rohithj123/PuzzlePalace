package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ClueTest {

    @Test
    public void constructorNormalisesNullTextAndStartsHidden() {
        Clue clue = new Clue(7, null);

        assertEquals(7, clue.getId());
        assertEquals("", clue.getText());
        assertFalse(clue.isRevealed());
    }

    @Test
    public void revealMarksClueAsVisible() {
        Clue clue = new Clue(3, "Look behind the painting");

        clue.reveal();

        assertTrue(clue.isRevealed());
    }
}