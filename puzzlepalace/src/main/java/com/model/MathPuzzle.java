package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (answer == null || question == null) return false;
        Map<String, Double> vars = parseAnswerString(answer);
        return checkAnswer(vars);
    }

    public boolean checkAnswer(Map<String, Double> variables) 
    {
        try {
            String[] sides = question.split("=");
            if (sides.length != 2) return false;

            double left = evaluate(sides[0], variables);
            double right = evaluate(sides[1], variables);

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

    private double evaluate(String expr, Map<String, Double> vars) 
    {
        expr = expr.replaceAll("\\s+", "");
        return new Parser(expr, vars).parseExpression();
    }

    private static class Parser 
    {
        private final String input;
        private final Map<String, Double> vars;
        private int pos = -1, ch;

        Parser(String input, Map<String, Double> vars) 
        {
            this.input = input;
            this.vars = vars;
            nextChar();
        }

        void nextChar() 
        {
            ch = (++pos < input.length()) ? input.charAt(pos) : -1;
        }

        boolean eat(int charToEat) 
        {
            while (ch == ' ') nextChar();
            if (ch == charToEat) 
            {
                nextChar();
                return true;
            }
            return false;
        }

        double parseExpression() 
        {
            double x = parseTerm();
            for (;;) 
            {
                if (eat('+')) x += parseTerm();
                else if (eat('-')) x -= parseTerm();
                else return x;
            }
        }

        double parseTerm() 
        {
            double x = parseFactor();
            for (;;) 
            {
                if (eat('*')) x *= parseFactor();
                else if (eat('/')) x /= parseFactor();
                else return x;
            }
        }

        double parseFactor() 
        {
            if (eat('+')) return parseFactor();
            if (eat('-')) return -parseFactor();

            double x;
            int startPos = this.pos;

            if (eat('(')) 
            {
                x = parseExpression();
                eat(')');
            } else if ((ch >= '0' && ch <= '9') || ch == '.') 
            {
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                x = Double.parseDouble(input.substring(startPos, this.pos));
            } else if (Character.isLetter(ch)) 
            {
                while (Character.isLetterOrDigit(ch)) nextChar();
                String var = input.substring(startPos, this.pos);
                x = vars.getOrDefault(var, 0.0);
            } else 
            {
                throw new RuntimeException("Unexpected: " + (char) ch);
            }

            if (eat('^')) x = Math.pow(x, parseFactor());
            return x;
        }
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

    private Map<String, Double> parseAnswerString(String str) 
    {
        Map<String, Double> map = new HashMap<>();
        if (str == null || str.isEmpty()) return map;

        String[] parts = str.split(",");
        for (String p : parts) 
        {
            String[] kv = p.split("=");
            if (kv.length == 2) 
            {
                try {
                    map.put(kv[0].trim(), Double.parseDouble(kv[1].trim()));
                } catch (NumberFormatException ignored) {}
            }
        }
        return map;
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
