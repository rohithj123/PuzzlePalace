package com.model;

import java.util.List;

/**
 * Handles checking answers for word puzzles.
 * Compares user answers with correct answers and gives feedback.
 */
public class WordPuzzleEvaluateSolution 
{
/**
     * Checks if the user's answer matches the correct one.
     *
     * @param userAnswer the answer the user gave
     * @param correctAnswer the correct word
     * @param wordList list of valid words for this puzzle
     * @return message saying if it's correct or not
     */

    public String evaluateSolution(String userAnswer, String correctAnswer, List<String> wordList) {
        if (userAnswer == null || userAnswer.isBlank()) 
        {
            return "No answer provided.";
        }
        if (correctAnswer == null || correctAnswer.isBlank()) 
        {
            return "No correct answer configured for this puzzle.";
        }

        String attempt = userAnswer.trim();
        String expected = correctAnswer.trim();

        if (attempt.equalsIgnoreCase(expected)) 
        {
            return "Correct! You selected the right word.";
        }

        if (wordList != null && !wordList.isEmpty()) 
        {
            boolean inList = wordList.stream().anyMatch(w -> w.equalsIgnoreCase(attempt));
            if (inList) 
            {
                return "Incorrect. \"" + attempt + "\" is not the correct word.";
            }
        }

        return "Incorrect answer. The correct word was \"" + expected + "\".";
    }

    /**
     * Checks an answer using a puzzle object.
     *
     * @param puzzle the puzzle to check
     * @param userAnswer the player's answer
     * @return message saying if it's correct or not
     */

    public String evaluateSolution(WordPuzzle puzzle, String userAnswer) 
    {
        if (puzzle == null) 
        {
            return "Error: puzzle not provided.";
        }
        return evaluateSolution(userAnswer, puzzle.getCorrectAnswer(), puzzle.getWordList());
    }

    public boolean isCorrect(String userAnswer, String correctAnswer) 
    {
        if (userAnswer == null || correctAnswer == null) return false;
        return userAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
    }
}
