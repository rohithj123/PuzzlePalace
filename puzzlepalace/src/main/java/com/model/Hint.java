package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This manages the hints available to the player.
 * This keeps track of how many hints have been used and how many are allowed.
 */

public class Hint 
{
    private int hintsUsed;
    private int maxHints = 3;
    private List<String> availableHints = new ArrayList<>();
    private int penaltyHintsUsed;
    private int bonusHintsUsed;
    /**
     * This creates a default Hint object.
     */
    public Hint() { }

    /**
     * This creates a Hint object with a list of hints and a limit.
     *
     * @param hints the list of available hints
     * @param maxHints the maximum number of hints that can be used
     */
    public Hint(List<String> hints, int maxHints) 
    {
        setAvailableHints(hints);
        setMaxHints(maxHints);
    }

    /**
     * This gives the next available hint.
     *
     * @return the next hint or a message if none are available
     */
    public synchronized String getHint() 
    {
        if (availableHints == null || availableHints.isEmpty()) 
        {
            return "No hints available.";
        }

        int allowedHints = Math.min(maxHints, availableHints.size());
        int effectiveHintsUsed = Math.max(0, hintsUsed - Math.max(0, bonusHintsUsed));

        if (effectiveHintsUsed >= allowedHints)
        {
            return "All hints have been used.";
        }

        String hint = availableHints.get(hintsUsed);
        hintsUsed++;
        penaltyHintsUsed++;
        return Objects.toString(hint, "No hint.");
    }
    
    /**
     * This checks if the player can still use more hints.
     *
     * @return true if more hints can be used, false otherwise
     */
    public synchronized boolean checkHintLimit() 
    {
        if (availableHints == null || availableHints.isEmpty()) 
        {
            return false;
        }
        int allowedHints = Math.min(maxHints, availableHints.size());
        int effectiveHintsUsed = Math.max(0, hintsUsed - Math.max(0, bonusHintsUsed));
        return effectiveHintsUsed < allowedHints;    }

    /**
     * This sets the list of available hints.
     *
     * @param hints the list of hints to set
     */
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
        this.penaltyHintsUsed = 0;
        this.bonusHintsUsed = 0;

    }
    /**
     * This returns a copy of the available hints.
     *
     * @return an unmodifiable list of hints
     */
    public synchronized List<String> getAvailableHintsSnapshot() 
    {
        return Collections.unmodifiableList(new ArrayList<>(availableHints));
    }

    /**
     * This resets all hint counters.
     */
    public synchronized void resetHintsUsed() 
    {
        this.hintsUsed = 0;
        this.penaltyHintsUsed = 0;
        this.bonusHintsUsed = 0;

    }

    /**
     * This returns how many hints have been used.
     *
     * @return the number of used hints
     */
    public synchronized int getHintsUsed() 
    {
        return hintsUsed;
    }
    /**
     * This returns how many penalty hints were used.
     *
     * @return the number of penalty hints used
     */
    public synchronized int getPenaltyHintsUsed()
    {
        return Math.max(0, penaltyHintsUsed);
    }
    /**
     * This returns how many bonus hints were used.
     *
     * @return the number of bonus hints used
     */
    public synchronized int getBonusHintsUsed()
    {
        return Math.max(0, bonusHintsUsed);
    }

    /**
     * This marks the last hint used as free (no penalty).
     */
    public synchronized void markLastHintFree()
    {
        if (penaltyHintsUsed > 0)
        {
            penaltyHintsUsed--;
        }
        if (hintsUsed > 0)
        {
            bonusHintsUsed = Math.min(hintsUsed, bonusHintsUsed + 1);
        }
    }

    /**
     * This returns the maximum number of hints allowed.
     *
     * @return the maximum hints
     */
    public synchronized int getMaxHints() 
    {
        return maxHints;
    }
    /**
     * This sets the maximum number of hints allowed.
     *
     * @param maxHints the number of allowed hints
     */
    public synchronized void setMaxHints(int maxHints) 
    {
        this.maxHints = Math.max(0, maxHints);
    }

    /**
     * This returns a string version of the hint data.
     *
     * @return a string with hint details
     */
    @Override
    public synchronized String toString() 
    {
        return "Hint{hintsUsed=" + hintsUsed + ", maxHints=" + maxHints + ", availableHints=" + availableHints + "}";
    }
}
