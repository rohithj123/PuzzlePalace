package com.model;

public class Hint {
    private int hintsUsed;
    private int maxHints = 3;
    
    public Hint() {

    }

    public String getHint() {
        return "Hint: ";
    }

    public boolean checkHintLimit() {
        return hintsUsed < maxHints;
    }
    
}
