package com.model;

public class LogicPuzzleEvaluateSolution {

    public String evaluateSolution(String question, String correctAnswer, String userAnswer) {
        if (question == null || question.isBlank()) return "No puzzle question provided.";
        if (correctAnswer == null || correctAnswer.isBlank()) return "No correct answer stored for puzzle.";
        if (userAnswer == null || userAnswer.isBlank()) return "No answer provided.";

        // Normalize text (lowercase, trim, remove punctuation)
        String normalizedUser = userAnswer.trim().toLowerCase().replaceAll("[^a-z0-9 ]", "");
        String normalizedAnswer = correctAnswer.trim().toLowerCase().replaceAll("[^a-z0-9 ]", "");

        if (normalizedUser.equals(normalizedAnswer)) {
            return "✅ Correct! The circuits are now off.";
        }

        // Simple partial-match feedback
        if (normalizedUser.contains("red") && normalizedAnswer.contains("red")) {
            return "Almost there! You’re on the right track—check your connections again.";
        }

        return "❌ Incorrect. Try again or use a hint.";
    }
}
