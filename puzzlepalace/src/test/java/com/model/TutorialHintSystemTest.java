package com.model;

import org.junit.Test;

public class TutorialHintSystemTest {

    @Test
    public void getHintIsNoOp() {
        TutorialHintSystem system = new TutorialHintSystem();
        system.getHint();
    }
}