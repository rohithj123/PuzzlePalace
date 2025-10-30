package com.model;

import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class LevelTest {

    @Test
    public void stubbedBehaviourRemainsIncomplete() {
        Level level = new Level();

        level.loadLevel();
        level.startLevel();
        level.completeLevel();

        assertFalse(level.isCompleted());
    }
}