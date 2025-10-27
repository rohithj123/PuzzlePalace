package com.model;

import java.util.Map;

/**
 * Evaluates whether a given answer (a number or variable assignments) satisfies
 * a mathematical equation of the form "expression = expression".
 */
public class MathPuzzleEvaluateSolution 
{
    /** 
     * Evaluates an equation using a single numeric answer. The numeric answer is
     * bound to variable "x" and then evaluated.
     */
    public String evaluateSolution(String equation, String userAnswer) 
    {
        if (equation == null || equation.isBlank()) return "No equation provided.";
        if (userAnswer == null || userAnswer.isBlank()) return "No answer provided.";

        try 
        {
            double value = Double.parseDouble(userAnswer);
            Map<String, Double> vars = Map.of("x", value);
            return evaluateSolution(equation, vars);
        } catch (NumberFormatException e) 
        {
            return "Invalid number format for answer.";
        }
    }

    /**
     * Evaluates an equation using a map of variable assignments.
     */
    public String evaluateSolution(String equation, Map<String, Double> variables) 
    {
        if (equation == null || equation.isBlank()) return "No equation provided.";
        if (variables == null || variables.isEmpty()) return "No variable assignments provided.";

        try 
        {
            String[] sides = equation.split("=");
            if (sides.length != 2) return "Invalid equation format — must contain '='.";

            double left = evaluate(sides[0], variables);
            double right = evaluate(sides[1], variables);

            if (Math.abs(left - right) < 1e-6) 
            {
                return "Correct! Your answer satisfies the equation.";
            } else 
            {
                return String.format("Incorrect. Left = %.4f, Right = %.4f", left, right);
            }
        } catch (RuntimeException e) 
        {
            return "Error evaluating: " + e.getMessage();
        }
    }

    /**
     * Helper to evaluate a numeric expression string using provided variables.
     */
    private double evaluate(String expr, Map<String, Double> vars) 
    {
        expr = expr.replaceAll("\\s+", "");
        return new Parser(expr, vars).parseExpression();
    }

    /**
     * Small recursive-descent parser for basic arithmetic expressions:
     * supports +, -, *, /, ^, parentheses, numbers and variables.
     */
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

        void nextChar() { ch = (++pos < input.length()) ? input.charAt(pos) : -1; }

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
                if (!vars.containsKey(var))
                    throw new RuntimeException("Unknown variable: " + var);
                x = vars.get(var);
            } else 
            {
                throw new RuntimeException("Unexpected: " + (char) ch);
            }

            if (eat('^')) x = Math.pow(x, parseFactor());
            return x;
        }
    }
}
