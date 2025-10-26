package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a single word puzzle.
 * Stores the question, answers, and hints for a word-based puzzle.
 */
public class WordPuzzle 
{

    private int puzzleId;
    private String question;
    private List<String> wordList;
    private String correctAnswer;
    private List<String> hints;
    private int hintsUsed;
    private final Random rng = new Random();

    /** Creates an empty puzzle with default values. */
    public WordPuzzle() 
    {
        this.wordList = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.hintsUsed = 0;
    }

    /** Creates a puzzle with full details. */
    public WordPuzzle(int puzzleId, String question, List<String> wordList, String correctAnswer, List<String> hints) {
        this();
        this.puzzleId = puzzleId;
        this.question = question;
        if (wordList != null) this.wordList = new ArrayList<>(wordList);
        this.correctAnswer = correctAnswer;
        if (hints != null) this.hints = new ArrayList<>(hints);
    }

    /** Creates a puzzle and picks a random answer from the list. */
    public WordPuzzle(List<String> wordList, List<String> hints) 
    {
        this();
        if (wordList != null) this.wordList = new ArrayList<>(wordList);
        if (hints != null) this.hints = new ArrayList<>(hints);
        pickRandomAnswerAndBuildQuestion();
    }

    /** Picks a random answer and builds a question string. */
    private void pickRandomAnswerAndBuildQuestion() 
    {
        if (wordList == null || wordList.isEmpty())
        {
            this.correctAnswer = null;
            if (this.question == null || this.question.isBlank()) 
            {
                this.question = "Select the correct word.";
            }
            return;
        }
        this.correctAnswer = wordList.get(rng.nextInt(wordList.size()));
        if (this.question == null || this.question.isBlank()) 
        {
            StringBuilder sb = new StringBuilder("Choose the correct word from: ");
            for (int i = 0; i < wordList.size(); i++) 
            {
                sb.append(wordList.get(i));
                if (i < wordList.size() - 1) sb.append(", ");
            }
            this.question = sb.toString();
        }
    }

        /**
     * Checks the user's answer and returns feedback.
     *
     * @param userAnswer the answer the user gave
     * @return feedback text showing if it’s correct or not
     */

    public String evaluateSolution(String userAnswer) 
    {
        if (userAnswer == null || userAnswer.isBlank()) 
        {
            return "No answer provided.";
        }
        if (correctAnswer == null) 
        {
            return "This puzzle has no correct answer configured.";
        }

        String attempt = userAnswer.trim();
        if (attempt.equalsIgnoreCase(correctAnswer.trim())) 
        {
            this.correctAnswer = correctAnswer.trim();
            return "Correct! Your answer is correct.";
        } else {
            if (wordList != null && !wordList.isEmpty() && wordList.stream().anyMatch(w -> w.equalsIgnoreCase(attempt))) 
            {
                return String.format("Incorrect. \"%s\" is not the right word.", attempt);
            } else 
            {
                return "Incorrect answer.";
            }
        }
    }

        /** Returns the next hint, if available. */
    public synchronized String getHint() 
    {
        if (hints == null || hints.isEmpty()) return "No hints available.";
        if (hintsUsed >= hints.size()) return "All hints have been used.";
        return hints.get(hintsUsed++);
    }

    /** Resets puzzle progress and picks a new random answer. */
    public synchronized void resetPuzzle() 
    {
        this.hintsUsed = 0;
        if (this.wordList != null && !this.wordList.isEmpty()) 
        {
            pickRandomAnswerAndBuildQuestion();
        }
    }

    public int getPuzzleId() { return puzzleId; }
    public void setPuzzleId(int puzzleId) { this.puzzleId = puzzleId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getWordList() { return Collections.unmodifiableList(wordList); }
    public void setWordList(List<String> wordList) 
    {
        this.wordList = wordList == null ? new ArrayList<>() : new ArrayList<>(wordList);
        pickRandomAnswerAndBuildQuestion();
    }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public List<String> getHints() { return Collections.unmodifiableList(hints); }
    public void setHints(List<String> hints) {
        this.hints = hints == null ? new ArrayList<>() : new ArrayList<>(hints);
        this.hintsUsed = 0;
    }

    public int getHintsUsed() { return hintsUsed; }

    /** Returns a readable summary of the puzzle for debugging. */
    @Override
    public String toString()
    {
        return "WordPuzzle{" +
                "puzzleId=" + puzzleId +
                ", question='" + question + '\'' +
                ", wordList=" + wordList +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", hints=" + hints +
                ", hintsUsed=" + hintsUsed +
                '}';
    }
}
