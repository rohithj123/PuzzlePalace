package com.model;

public class Score {
    private static final int POINTS_PER_PUZZLE = 100;
    private static final int PENALTY_PER_HINT = 15;
    private static final int TIME_PENALTY_INTERVAL_SECONDS = 60;
    private static final int PENALTY_PER_TIME_INTERVAL = 2;
    private int points;
    private int puzzlesSolved;
    private int hintsUsed;
    private int timeTaken;
    private Progress progress;

    public Score() {
        this(0, 0, 0, 0);

    }
    
    public Score(int points, int puzzlesSolved, int hintsUsed, int timeTaken) {
        this.points = Math.max(0, points);
        this.puzzlesSolved = Math.max(0, puzzlesSolved);
        this.hintsUsed = Math.max(0, hintsUsed);
        this.timeTaken = Math.max(0, timeTaken);
    }

    public int calculateScore() {
        int total = Math.max(0, points);
        total += Math.max(0, puzzlesSolved) * POINTS_PER_PUZZLE;
        total -= Math.max(0, hintsUsed) * PENALTY_PER_HINT;

        if(timeTaken > 0) {
            int intervals = Math.max(0, timeTaken) / TIME_PENALTY_INTERVAL_SECONDS;
            total -= intervals * PENALTY_PER_TIME_INTERVAL;
        }
        
        return Math.max(total, 0);
    }

    public int compare(Score other) {
        if(other == null) {
            throw new IllegalArgumentException("Score to compare against must not be null");
        }
        return Integer.compare(this.calculateScore(), other.calculateScore());
    }
    
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = Math.max(0, points);
    }

    public int getPuzzlesSolved() {
        return puzzlesSolved;
    }

    public void setPuzzlesSolved(int puzzlesSolved) {
        this.puzzlesSolved = Math.max(0, puzzlesSolved);
    }

    public int getHintsUsed() {
        return hintsUsed;
    }   

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = Math.max(0, hintsUsed);
    }   

    public int getTimeTaken() {
        return timeTaken;
    }   

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = Math.max(0, timeTaken);
    }   

    public Progress getProgress() {
        return progress;
    }   

    public void setProgress(Progress progress) {
        this.progress = progress;
    }   

    @Override
    public String toString() {
        return "Score [points=" + points + ", puzzlesSolved=" + puzzlesSolved + ", hintsUsed=" + hintsUsed
                + ", timeTaken=" + timeTaken + "]";
    }
    
    
}
