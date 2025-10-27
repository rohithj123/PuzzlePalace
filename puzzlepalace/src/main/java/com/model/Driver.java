package com.model;

import com.speech.Speak;

/**
 * This is the main driver class for the program.
 * This starts the application and plays a welcome message.
 */

public class Driver {
    /**
     * This runs the program.
     * This speaks a welcome message when the game starts.
     *
     */
    public static void main(String[] args) {
        String message = "Welcome to Puzzle Palace!";
        Speak.speak(message);
    }
    
}
