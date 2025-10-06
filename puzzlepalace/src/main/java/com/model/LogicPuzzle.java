package com.model;

public class LogicPuzzle 
{
    private int puzzleId;
    private String logicType;
    private String question;
    private String correctAnswer;
    private boolean isSolved;

    public LogicPuzzle()
    {
    }

    public LogicPuzzle(int puzzleId, String logicType, String question, String correctAnswer) 
    {
        this.puzzleId = puzzleId;
        this.logicType = logicType;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.isSolved = false;
    }

    public int getPuzzleId() 
    {
        return puzzleId;
    }

    public String getLogicType() 
    {
        return logicType;
    }

    public String getQuestion() 
    {
        return question;
    }

    public boolean isSolved() 
    {
        return isSolved;
    }

    public boolean checkAnswer(String attempt) 
    {
        System.out.println("checkAnswer() called with attempt: " + attempt);
        if (attempt == null) return false;

        boolean correct = attempt.trim().equalsIgnoreCase(correctAnswer);
        if (correct) 
        {
            System.out.println("Correct answer! Logic puzzle solved (stub).");
            isSolved = true;
        } else 
        {
            System.out.println("Incorrect answer (stub).");
        }
        return correct;
    }

    public void validateLogic() 
    {
        System.out.println("validateLogic() called for type: " + logicType + " (stub)");
    }

    public void resetPuzzle() 
    {
        System.out.println("resetPuzzle() called (stub)");
        isSolved = false;
    }

    @Override
    public String toString() 
    {
        return "LogicPuzzle{id=" + puzzleId +
               ", type='" + logicType + '\'' +
               ", question='" + question + '\'' +
               ", solved=" + isSolved + "}";
    }
}
