package com.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SettingsTest {

    @Test
    public void difficultyFromNameMatchesDisplayAndEnumName() {
        assertEquals(Settings.Difficulty.MEDIUM, Settings.Difficulty.fromName("medium"));
        assertEquals(Settings.Difficulty.HARD, Settings.Difficulty.fromName("Hard"));
        assertEquals(Settings.Difficulty.EASY, Settings.Difficulty.fromName("unknown"));
        assertEquals(Settings.Difficulty.EASY, Settings.Difficulty.fromName(null));
    }

    @Test
    public void setDifficultyDefaultsToEasyWhenNull() {
        Settings settings = new Settings();
        settings.setDifficulty(Settings.Difficulty.HARD);
        assertEquals("Hard", settings.getDifficultyDisplayName());

        settings.setDifficulty(null);
        assertEquals(Settings.Difficulty.EASY, settings.getDifficulty());
    }
}