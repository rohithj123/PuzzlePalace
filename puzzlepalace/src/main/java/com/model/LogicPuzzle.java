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
    }

    public int getPuzzleId() 
    {
        return 0;
    }

    public String getLogicType() 
    {
        return null;
    }

    public String getQuestion() 
    {
        return null;
    }

    public boolean isSolved() 
    {
        return false;
    }

    public boolean checkAnswer(String attempt) 
    {
        return false;
    }

    public void validateLogic() 
    {
    }

    public void resetPuzzle() 
    {
    }
}
