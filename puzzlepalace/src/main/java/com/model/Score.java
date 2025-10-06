package com.model;

public class Score {
    private int points;
    private int puzzlesSolved;
    private int hintsUsed;
    private int timeTaken;
    private Progress progress;

    public Score() {

    }
    
    public Score(int ponts, int puzzlesSolved, int hintsUsed, int timeTaken) {
        this.points = ponts;
        this.puzzlesSolved = puzzlesSolved;
        this.hintsUsed = hintsUsed;
        this.timeTaken = timeTaken;
    }

    public int calculateScore() {
        return points;
    }

    public int compare(Score other) {
        return 0;
    }
    
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPuzzlesSolved() {
        return puzzlesSolved;
    }

    public void setPuzzlesSolved(int puzzlesSolved) {
        this.puzzlesSolved = puzzlesSolved;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }   

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }   

    public int getTimeTaken() {
        return timeTaken;
    }   

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }   

    public Progress getProgress() {
        return progress;
    }   

    public void setPregress(Progress progress) {
        this.progress = progress;
    }   

    @Override
    public String toString() {
        return "Score [points=" + points + ", puzzlesSolved=" + puzzlesSolved + ", hintsUsed=" + hintsUsed
                + ", timeTaken=" + timeTaken + "]";
    }
    
    
}
