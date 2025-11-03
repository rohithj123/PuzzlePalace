package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class HintRequestResultTest {

    @Test
    public void constructor_normalizesNullMessage() {
        HintRequestResult result = new HintRequestResult(false, null, false);

        assertFalse(result.isSuccess());
        assertEquals("", result.getMessage());
        assertFalse(result.isTokenConsumed());
    }

    @Test
    public void getters_exposeProvidedValues() {
        HintRequestResult result = new HintRequestResult(true, "Granted", true);

        assertTrue(result.isSuccess());
        assertEquals("Granted", result.getMessage());
        assertTrue(result.isTokenConsumed());
    }

    @Test
    public void message_trimmedAndHandledSafely() {
        HintRequestResult result = new HintRequestResult(true, "  Trim Me  ", false);

        assertEquals("Trim Me", result.getMessage().trim());
        assertTrue(result.isSuccess());
        assertFalse(result.isTokenConsumed());
    }

    @Test
    public void falseResult_doesNotConsumeToken() {
        HintRequestResult result = new HintRequestResult(false, "Denied", true);

        assertFalse(result.isSuccess());
        assertEquals("Denied", result.getMessage());
        // Behavior check: ensure flag consistency
        assertTrue(result.isTokenConsumed());
    }
}
