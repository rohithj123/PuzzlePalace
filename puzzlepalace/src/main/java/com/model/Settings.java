package com.model;

public class Settings {

    public enum Difficulty {
        EASY("Easy"),
        MEDIUM("Medium"),
        HARD("Hard");

        private final String displayName;

        Difficulty(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public static Difficulty fromName(String value) {
            if (value == null || value.isBlank()) {
                return EASY;
            }
            String trimmed = value.trim();
            for (Difficulty difficulty : values()) {
                if (difficulty.displayName.equalsIgnoreCase(trimmed) ||
                        difficulty.name().equalsIgnoreCase(trimmed)) {
                    return difficulty;
                }
            }
            return EASY;
        }
    }

    private Difficulty difficulty = Difficulty.EASY;

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty == null ? Difficulty.EASY : difficulty;
    }

    public String getDifficultyDisplayName() {
        return getDifficulty().getDisplayName();
    }
}

