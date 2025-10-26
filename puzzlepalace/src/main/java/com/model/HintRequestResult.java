package com.model;
/**
 * This represents the result of a hint request.
 * This stores whether the request was successful, the message, and if a token was used.
 */
public class HintRequestResult {

    private final boolean success;
    private final String message;
    private final boolean tokenConsumed;

    /**
     * This creates a new HintRequestResult.
     *
     * @param success whether the hint request was successful
     * @param message a message describing the result
     * @param tokenConsumed true if a token was used for this request
     */
    public HintRequestResult(boolean success, String message, boolean tokenConsumed) {
        this.success = success;
        this.message = message == null ? "" : message;
        this.tokenConsumed = tokenConsumed;
    }

    /**
     * This checks if the request was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * This returns the result message.
     *
     * @return the result message
     */
    public String getMessage() {
        return message;
    }
    /**
     * This checks if a token was used for the request.
     *
     * @return true if a token was used, false otherwise
     */
    public boolean isTokenConsumed() {
        return tokenConsumed;
    }
}