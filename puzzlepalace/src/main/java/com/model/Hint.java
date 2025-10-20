package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Hint 
{
    private int hintsUsed;
    private int maxHints = 3;
    private List<String> availableHints = new ArrayList<>();

    public Hint() { }

    public Hint(List<String> hints, int maxHints) 
    {
        setAvailableHints(hints);
        setMaxHints(maxHints);
    }

    public synchronized String getHint() 
    {
        if (availableHints == null || availableHints.isEmpty()) 
        {
            return "No hints available.";
        }

        int allowedHints = Math.min(maxHints, availableHints.size());

        if (hintsUsed >= allowedHints) 
        {
            return "All hints have been used.";
        }

        String hint = availableHints.get(hintsUsed);
        hintsUsed++;
        return Objects.toString(hint, "No hint.");
    }

    public synchronized boolean checkHintLimit() 
    {
        if (availableHints == null || availableHints.isEmpty()) 
        {
            return false;
        }
        int allowedHints = Math.min(maxHints, availableHints.size());
        return hintsUsed < allowedHints;
    }

    public synchronized void setAvailableHints(List<String> hints) 
    {
        if (hints == null) 
        {
            this.availableHints = new ArrayList<>();
        } else 
        {
            this.availableHints = new ArrayList<>(hints);
        }
        this.hintsUsed = 0;
    }

    public synchronized List<String> getAvailableHintsSnapshot() 
    {
        return Collections.unmodifiableList(new ArrayList<>(availableHints));
    }

    public synchronized void resetHintsUsed() 
    {
        this.hintsUsed = 0;
    }

    public synchronized int getHintsUsed() 
    {
        return hintsUsed;
    }

    public synchronized int getMaxHints() 
    {
        return maxHints;
    }

    public synchronized void setMaxHints(int maxHints) 
    {
        this.maxHints = Math.max(0, maxHints);
    }

    @Override
    public synchronized String toString() 
    {
        return "Hint{hintsUsed=" + hintsUsed + ", maxHints=" + maxHints + ", availableHints=" + availableHints + "}";
    }
}
