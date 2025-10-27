package com.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the ongoing state of a player's session including timing, hint
 * management, and simple persistence to disk for resume functionality.
 * 
 * Timer is stored in milliseconds internally and converted to seconds for {@link Score}.
 */
public class Progress {

    /**
     * Current room or stage index for the active session.
     */
    private int currentRoom;

    /**
     * Owning player; may be {@code null} for anonymous/default progress.
     */
    private Player player;

    /**
     * Accumulated timer in milliseconds. Updated when a game ends using the
     * difference between {@link #startTime} and {@link #endTime}.
     */
    private long timer;

    /**
     * True if the current session has been completed (win/finished).
     */
    private boolean isCompleted;

    /**
     * Score object associated with this progress instance.
     */
    private final Score score;

    /**
     * Number of hints consumed in the current session.
     */
    private int hintsUsed;

    /**
     * Number of hints remaining in the current session (derived from
     * {@link #availableHints} and {@link #hintsUsed}).
     */
    private int hintsRemaining;

    /**
     * List of textual hints available for this game/session. Elements are
     * considered ordered: each call to {@link #useHint()} returns the next hint
     * from this list according to {@link #hintsUsed}.
     */
    private List<String> availableHints;

    /**
     * Session start time; set by {@link #startGame()} / {@link #beginGame(List)}.
     */
    private LocalDateTime startTime;

    /**
     * Session end time; set by {@link #endGame()}.
     */
    private LocalDateTime endTime;

    /**
     * Default directory used for storing progress files.
     */
    private static final String DEFAULT_SAVE_DIR = "data";

    /**
     * Filename used when no player ID is available.
     */
    private static final String DEFAULT_SAVE_FILE = "progress-default.txt";

    /**
     * Delimiter used to join multiple hints when writing to a single key/value
     * line in the progress file.
     */
    private static final String HINT_DELIMITER = " ||";

    /**
     * Constructs a default {@code Progress} instance with no player and no
     * available hints.
     */
    public Progress() {
        this(null, null, null);
    }

    /**
     * Constructs a {@code Progress} associated with the specified player and
     * optional hint list.
     *
     * @param player         owning {@link Player} instance (may be {@code null})
     * @param availableHints initial list of available hints (may be {@code null})
     */
    public Progress(Player player, List<String> availableHints) {
        this(player, null, availableHints);
    }

    /**
     * Full constructor allowing an externally created {@link Score} to be used.
     * If {@code existingScore} is {@code null} a new {@link Score} is created
     * and wired back to this {@code Progress} via {@link Score#setProgress(Progress)}.
     *
     * @param player        owning {@link Player} (may be {@code null})
     * @param existingScore optional existing {@link Score} to associate (may be {@code null})
     * @param availableHints initial available hints list (may be {@code null})
     */
    public Progress(Player player, Score existingScore, List<String> availableHints) {
        this.currentRoom = 0;
        this.player = player;
        this.timer = 0L;
        this.isCompleted = false;
        this.score = existingScore != null ? existingScore : new Score();
        this.score.setProgress(this);
        this.hintsUsed = 0;
        this.availableHints = new ArrayList<>();
        this.hintsRemaining = 0;
        this.startTime = null;
        this.endTime = null;
        setAvailableHintsInternal(availableHints);
        resetHints();
    }

    /**
     * Returns the {@link Score} instance managed by this {@link Progress}.
     *
     * @return associated {@link Score}
     */
    public Score getScore() {
        return score;
    }

    /**
     * Replace the set of available hints with the provided list and reset the
     * hint usage counters.
     *
     * @param hints new list of hints (may be {@code null} to clear)
     */
    public void setAvailableHints(List<String> hints) {
        replaceAvailableHints(hints);
    }

    /**
     * Replaces the available hints list without changing other session state,
     * then resets hint counters to reflect the new pool.
     *
     * @param hints new list of hints (may be {@code null})
     */
    public void replaceAvailableHints(List<String> hints) {
        setAvailableHintsInternal(hints);
        resetHints();
    }

    /**
     * Updates the hint pool while preserving {@link #hintsUsed} and adjusts
     * {@link #hintsRemaining} accordingly.
     *
     * @param hints new list of hints (may be {@code null})
     */
    public void updateHintPool(List<String> hints) {
        setAvailableHintsInternal(hints);
        hintsRemaining = Math.max(availableHints.size() - hintsUsed, 0);
    }

    /**
     * Begin a new game using the supplied hints. If {@code hints} is {@code null}
     * the existing hint pool is left unchanged and hint counters are reset.
     *
     * @param hints optional list of hints to initialize for the game
     */
    public void beginGame(List<String> hints) {
        if (hints != null) {
            replaceAvailableHints(hints);
        } else {
            resetHints();
        }
        startGame();
    }

    /**
     * Marks the game as started by setting {@link #startTime}, clearing
     * {@link #endTime}, resetting hint counters, and zeroing the internal
     * timer and the {@link Score#getTimeTaken()} value.
     */
    public void startGame() {
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.isCompleted = false;
        resetHints();
        this.timer = 0L;

        score.setTimeTaken(0);
    }

    /**
     * Marks the game as ended by setting {@link #endTime}, computing the elapsed
     * time (in milliseconds) when {@link #startTime} is present, updating the
     * {@link #isCompleted} flag, and pushing time/hint data into {@link Score}.
     */
    public void endGame() {
        if (this.startTime == null) {
            this.endTime = LocalDateTime.now();
            this.timer = 0L;
        } else {
            this.endTime = LocalDateTime.now();
            Duration d = Duration.between(startTime, endTime);
            this.timer = d.toMillis();
        }
        this.isCompleted = true;

        score.setTimeTaken((int) Math.min(Integer.MAX_VALUE, Math.max(0L, timer / 1000L)));
        score.setHintsUsed(hintsUsed);

    }

    /**
     * Returns whether the current session is marked as completed.
     *
     * @return {@code true} if completed, otherwise {@code false}
     */
    public boolean checkWin() {
        return isCompleted;
    }

    /**
     * Persist the current progress to disk. The file name is derived from the
     * owning player's UUID if available: {@code data/progress-<playerId>.txt},
     * otherwise {@code data/progress-default.txt} is used.
     */
    public void saveProgress() {
        String filename;
        if (player != null && player.getPlayerID() != null) {
            filename = DEFAULT_SAVE_DIR + "/progress-" + player.getPlayerID() + ".txt";
        } else {
            filename = DEFAULT_SAVE_DIR + "/" + DEFAULT_SAVE_FILE;
        }

        Path savePath = Paths.get(filename);
        try {
            Path parent = savePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<String> hintsToWrite = new ArrayList<>();
        if (availableHints != null) {
            for (String hint : availableHints) {
                if (hint != null) {
                    hintsToWrite.add(hint);
                }
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(savePath)) {
            writer.write("playerId=" + (player != null && player.getPlayerID() != null ? player.getPlayerID() : ""));
            writer.newLine();
            writer.write("currentRoom=" + currentRoom);
            writer.newLine();
            writer.write("timer=" + timer);
            writer.newLine();
            writer.write("completed=" + isCompleted);
            writer.newLine();
            writer.write("hintsUsed=" + hintsUsed);
            writer.newLine();
            writer.write("hintsRemaining=" + hintsRemaining);
            writer.newLine();
            writer.write("startTime=" + (startTime != null ? startTime : ""));
            writer.newLine();
            writer.write("endTime=" + (endTime != null ? endTime : ""));
            writer.newLine();
            writer.write("scorePoints=" + score.getPoints());
            writer.newLine();
            writer.write("scorePuzzlesSolved=" + score.getPuzzlesSolved());
            writer.newLine();
            writer.write("scoreHintsUsed=" + score.getHintsUsed());
            writer.newLine();
            writer.write("scoreTimeTaken=" + score.getTimeTaken());
            writer.newLine();
            writer.write("scoreFreeHintTokens=" + score.getFreeHintTokens());
            writer.newLine();
            writer.write("availableHints=" + String.join(HINT_DELIMITER, hintsToWrite));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads a previously saved progress file if present. File name resolution is
     * the same as {@link #saveProgress()}. The loader is resilient: unknown
     * keys are ignored and parsing errors fall back to current values.
     *
     * After loading, the {@link #hintsRemaining} and the {@link Score} hint/time
     * fields are recomputed from loaded values.
     */
    public void loadProgress() {
        String filename;
        if (player != null && player.getPlayerID() != null) {
            filename = DEFAULT_SAVE_DIR + "/progress-" + player.getPlayerID() + ".txt";
        } else {
            filename = DEFAULT_SAVE_DIR + "/" + DEFAULT_SAVE_FILE;
        }

        Path savePath = Paths.get(filename);
        if (!Files.exists(savePath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(savePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int idx = line.indexOf('=');
                if (idx == -1) {
                    continue;
                }
                String key = line.substring(0, idx);
                String value = line.substring(idx + 1);
                switch (key) {
                    case "currentRoom":
                        this.currentRoom = parseInt(value, currentRoom);
                        break;
                    case "timer":
                        this.timer = parseLong(value, timer);
                        break;
                    case "completed":
                        this.isCompleted = Boolean.parseBoolean(value);
                        break;
                    case "hintsUsed":
                        this.hintsUsed = parseInt(value, hintsUsed);
                        break;
                    case "hintsRemaining":
                        this.hintsRemaining = parseInt(value, hintsRemaining);
                        break;
                    case "startTime":
                        this.startTime = parseDateTime(value);
                        break;
                    case "endTime":
                        this.endTime = parseDateTime(value);
                        break;
                    case "scorePoints":
                        this.score.setPoints(parseInt(value, score.getPoints()));
                        break;
                    case "scorePuzzlesSolved":
                        this.score.setPuzzlesSolved(parseInt(value, score.getPuzzlesSolved()));
                        break;
                    case "scoreHintsUsed":
                        this.score.setHintsUsed(parseInt(value, score.getHintsUsed()));
                        break;
                    case "scoreTimeTaken":
                        this.score.setTimeTaken(parseInt(value, score.getTimeTaken()));
                        break;
                    case "scoreFreeHintTokens":
                        this.score.setFreeHintTokens(parseInt(value, score.getFreeHintTokens()));
                        break;
                    case "availableHints":
                        this.availableHints = parseHints(value);
                        break;
                    default:
                        break;
                }

            }
            if (availableHints == null) {
                availableHints = new ArrayList<>();
            }
            hintsRemaining = Math.max(0, availableHints.size() - hintsUsed);
            score.setHintsUsed(hintsUsed);
            score.setTimeTaken((int) Math.min(Integer.MAX_VALUE, Math.max(0L, timer / 1000L)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the next available hint and updates counters. Hints are consumed
     * in order from {@link #availableHints}. If no hints are available or
     * {@link #hintsRemaining} is zero this method returns {@code null}.
     *
     * @return next hint string or {@code null} if none available
     */
    public String useHint() {
        if (availableHints == null || availableHints.isEmpty()) {
            return null;
        }
        if (hintsRemaining <= 0) {
            return null;
        }

        int index = hintsUsed;
        if (index < 0 || index >= availableHints.size()) {
            index = availableHints.size() - hintsRemaining;
            if (index < 0 || index >= availableHints.size()) {
                return null;
            }
        }

        String hint = availableHints.get(index);
        hintsUsed++;
        hintsRemaining = Math.max(availableHints.size() - hintsUsed, 0);
        score.setHintsUsed(hintsUsed);
        return hint;


    }

    /**
     * Resets the hint usage counters for a fresh session. Does not clear the
     * hint pool unless the pool is {@code null} in which case an empty pool is created.
     */
    public void resetHints() {
        this.hintsUsed = 0;
        if (availableHints == null) {
            this.availableHints = new ArrayList<>();
            this.hintsRemaining = 0;
        } else {
            this.hintsRemaining = availableHints.size();
        }
        score.setHintsUsed(0);
    }

    /**
     * Internal helper to replace the backing {@link #availableHints} list while
     * filtering out {@code null} entries.
     *
     * @param hints input hints (may be {@code null})
     */
    private void setAvailableHintsInternal(List<String> hints) {
        if (this.availableHints == null) {
            this.availableHints = new ArrayList<>();
        } else {
            this.availableHints.clear();
        }
        if (hints == null) {
            return;
        }
        for (String hint : hints) {
            if (hint != null) {
                this.availableHints.add(hint);
            }
        }
    }

    /**
     * Parse an integer value returning {@code defaultValue} on parse errors.
     *
     * @param value        string value to parse
     * @param defaultValue fallback value if parsing fails
     * @return parsed integer or {@code defaultValue}
     */
    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse a long value returning {@code defaultValue} on parse errors.
     *
     * @param value        string value to parse
     * @param defaultValue fallback value if parsing fails
     * @return parsed long or {@code defaultValue}
     */
    private static long parseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse a {@link LocalDateTime} from the supplied string, returning {@code null}
     * if parsing fails.
     *
     * @param value string representation of a {@link LocalDateTime}
     * @return parsed {@link LocalDateTime} or {@code null}
     */
    private static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse an available-hints string written with {@link #HINT_DELIMITER} into a
     * {@link List} of hints. Trims each part and ignores empty segments.
     *
     * @param value joined hint string value
     * @return list of individual hint strings (never {@code null})
     */
    private List<String> parseHints(String value) {
        List<String> hints = new ArrayList<>();
        if (value == null || value.isEmpty()) {
            return hints;
        }
        String[] parts = value.split(HINT_DELIMITER);
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                hints.add(part.trim());
            }
        }
        return hints;
    }
}

