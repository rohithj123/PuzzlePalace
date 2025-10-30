package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HintRequestResultTest {

    @Test
    public void constructorNormalisesNullMessage() {
        HintRequestResult result = new HintRequestResult(false, null, false);

        assertFalse(result.isSuccess());
        assertEquals("", result.getMessage());
        assertFalse(result.isTokenConsumed());
    }

    @Test
    public void gettersExposeProvidedValues() {
        HintRequestResult result = new HintRequestResult(true, "Granted", true);

        assertTrue(result.isSuccess());
        assertEquals("Granted", result.getMessage());
        assertTrue(result.isTokenConsumed());
    }
}