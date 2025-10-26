package com.model;

/**
 * Stores and manages game settings.
 * Includes the selected difficulty level.
 */
public class Settings {

    /**
     * Different difficulty levels for the game.
     */
    public enum Difficulty {
        EASY("Easy"),
        MEDIUM("Medium"),
        HARD("Hard");

        private final String displayName;

                /** Sets the display name for the difficulty. */
        Difficulty(String displayName) {
            this.displayName = displayName;
        }

                /** Returns the display name for this difficulty. */
        public String getDisplayName() {
            return displayName;
        }

        /** Returns the display name when converted to a string. */
        @Override
        public String toString() {
            return displayName;
        }

        /**
         * Converts a string to a matching difficulty.
         *
         * @param value name or label to match
         * @return matching difficulty, or EASY if not found
         */
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

        /** Returns the current difficulty. */
    public Difficulty getDifficulty() {
        return difficulty;
    }

        /** Sets the difficulty (defaults to EASY if null). */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty == null ? Difficulty.EASY : difficulty;
    }

        /** Returns the difficulty’s display name. */
    public String getDifficultyDisplayName() {
        return getDifficulty().getDisplayName();
    }
}

