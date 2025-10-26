package com.model;

public class HintRequestResult {

    private final boolean success;
    private final String message;
    private final boolean tokenConsumed;

    public HintRequestResult(boolean success, String message, boolean tokenConsumed) {
        this.success = success;
        this.message = message == null ? "" : message;
        this.tokenConsumed = tokenConsumed;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isTokenConsumed() {
        return tokenConsumed;
    }
}