package com.model;

import java.util.ArrayList;
import java.util.List;

public class MathPuzzle 
{

    private int puzzleId;
    private String question;
    private String answer;
    private List<String> hints;
    private int hintsUsed;

    public MathPuzzle() 
    {
        this.hints = new ArrayList<>();
        this.hintsUsed = 0;
    }

    public MathPuzzle(int puzzleId, String question, String answer) 
    {
        this();
        this.puzzleId = puzzleId;
        this.question = question;
        this.answer = answer;
    }

    public boolean checkAnswer() 
    {
        if (question == null || answer == null) return false;

        try 
        {
            double x = Double.parseDouble(answer);
            // split equation
            String[] parts = question.split("=");
            if (parts.length != 2) return false;

            double left = evaluate(parts[0], x);
            double right = evaluate(parts[1], x);

            return Math.abs(left - right) < 1e-6;
        } catch (Exception e) 
        {
            return false;
        }
    }

    public boolean checkAnswer(String userAnswer) 
    {
        this.answer = userAnswer;
        return checkAnswer();
    }

    private double evaluate(String expr, double x) 
    {
        expr = expr.replaceAll("\\s+", "").replaceAll("x", "(" + x + ")");
        return evalBasic(expr);
    }

    private double evalBasic(String expr) 
    {
        // Handle parentheses
        while (expr.contains("(")) 
        {
            int close = expr.indexOf(')');
            int open = expr.lastIndexOf('(', close);
            double inner = evalBasic(expr.substring(open + 1, close));
            expr = expr.substring(0, open) + inner + expr.substring(close + 1);
        }

        List<Double> terms = new ArrayList<>();
        List<Character> ops = new ArrayList<>();
        StringBuilder num = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) 
        {
            char c = expr.charAt(i);
            if (c == '+' || c == '-') {
                terms.add(evalTerm(num.toString()));
                ops.add(c);
                num.setLength(0);
            } else {
                num.append(c);
            }
        }
        if (num.length() > 0) terms.add(evalTerm(num.toString()));

        double result = terms.get(0);
        for (int i = 0; i < ops.size(); i++) 
        {
            char op = ops.get(i);
            double val = terms.get(i + 1);
            if (op == '+') result += val;
            else result -= val;
        }
        return result;
    }

    private double evalTerm(String expr) 
    {
        double result = 1;
        String[] mulParts = expr.split("\\*");
        for (String p : mulParts) 
        {
            if (p.contains("/")) 
            {
                String[] divParts = p.split("/");
                double val = Double.parseDouble(divParts[0]);
                for (int i = 1; i < divParts.length; i++) 
                {
                    val /= Double.parseDouble(divParts[i]);
                }
                result *= val;
            } else 
            {
                result *= Double.parseDouble(p);
            }
        }
        return result;
    }


    public String getHint() 
    {
        if (hints == null || hints.isEmpty()) return "No hints available.";
        if (hintsUsed >= hints.size()) return "All hints have been used.";
        return hints.get(hintsUsed++);
    }

    public void resetPuzzle() 
    {
        this.answer = null;
        this.hintsUsed = 0;
    }

    public int getPuzzleId() { return puzzleId; }
    public void setPuzzleId(int puzzleId) { this.puzzleId = puzzleId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<String> getHints() { return hints; }
    public void setHints(List<String> hints) { this.hints = hints; }
}
