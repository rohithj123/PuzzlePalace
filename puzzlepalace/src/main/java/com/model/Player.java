package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player profile including authentication state, inventory,
 * progress tracking, and scoring details.
 *
 * The {@code Player} holds identifying information (UUID, username, email),
 * authentication (password hash / guest flag), an item inventory and progress
 * data for puzzles. Many operations that mutate collections are synchronized
 * on internal locks (see {@code inventoryLock} and {@code progressLock}) to be
 * thread-safe.
 */
public class Player {

    /**
     * Unique identifier for this player.
     */
    private final UUID playerID;

    /**
     * Display username (may be an auto-generated guest alias).
     */
    private String username;

    /**
     * Email address associated with the player (trimmed).
     */
    private String email;

    /**
     * Stored hashed password (may be {@code null} for guest accounts).
     */
    private String passwordHash;

    /**
     * True if this is a guest account (no persistent password).
     */
    private boolean guest;

    /**
     * Inventory of items owned by the player. Access must be synchronized on
     * {@link #inventoryLock}.
     */
    private final List<Item> inventory;

    /**
     * Lock object for synchronizing access to {@link #inventory}.
     */
    private final Object inventoryLock = new Object();

    /**
     * Player progress object which also exposes the {@link Score} instance.
     */
    private final Progress progress;

    /**
     * Score details associated with this player (provided by {@link Progress}).
     */
    private final Score score;

    /**
     * Optional certificate awarded to the player.
     */
    private Certificate certificate;

    /**
     * Mapping of puzzleId to {@link PuzzleProgressSnapshot}. Access must be
     * synchronized on {@link #progressLock}.
     */
    private final Map<Integer, PuzzleProgressSnapshot> puzzleProgress;

    /**
     * Lock object for synchronizing access to {@link #puzzleProgress}.
     */
    private final Object progressLock = new Object();

    /**
     * Creates a new guest player with generated UUID and guest alias.
     */
    public Player() {
        this(null, null, null, true);
    }

    /**
     * Creates a new player with the provided username, email and raw password.
     * If {@code rawPassword} is {@code null} or blank, the player will be
     * treated as a guest.
     *
     * @param username    initial username (trimmed internally); if {@code null}
     *                    or blank and the player is a guest a generated guest
     *                    alias will be used
     * @param email       email address (trimmed) or {@code null}
     * @param rawPassword raw password to be hashed and stored; may be {@code null}
     *                    to indicate a guest account
     */
    public Player(String username, String email, String rawPassword) {
        this(username, email, rawPassword, false);
    }

    /**
     * Internal constructor used by other constructors to initialize fields.
     *
     * @param username     initial username
     * @param email        initial email
     * @param rawPassword  raw password to store (may be {@code null})
     * @param guestAccount whether to force creation as a guest account
     */
    private Player(String username, String email, String rawPassword, boolean guestAccount) {
        this.playerID = UUID.randomUUID();
        this.inventory = new ArrayList<>();
        this.progress = new Progress(this, null);
        this.score = progress.getScore();
        this.puzzleProgress = new LinkedHashMap<>();
        this.email = sanitizeEmail(email);
        this.guest = guestAccount;
        initialiseUsername(username);
        if (!this.guest && rawPassword != null && !rawPassword.isBlank()) {
            setPassword(rawPassword);
        } else {
            this.passwordHash = null;
        }
    }

    /**
     * Initialize the username field. If a valid username is not provided or the
     * account is a guest account, a generated guest alias will be used and the
     * guest flag will be set.
     *
     * @param providedUsername username to use (may be {@code null})
     */
    private void initialiseUsername(String providedUsername) {
        String cleaned = sanitizeUsername(providedUsername);
        if (this.guest || cleaned == null || cleaned.isEmpty()) {
            this.username = generateGuestAlias();
            this.guest = true;
        } else {
            this.username = cleaned;
        }
    }

    /**
     * Generate a short guest alias derived from the {@link #playerID}.
     *
     * @return generated guest alias (format {@code "Guest-xxxxxxxx"})
     */
    private String generateGuestAlias() {
        return "Guest-" + playerID.toString().substring(0, 8);
    }

    /**
     * Trim and return the provided username.
     *
     * @param username input username or {@code null}
     * @return trimmed username or {@code null} if input was {@code null}
     */
    private static String sanitizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    /**
     * Trim and return the provided email.
     *
     * @param email input email or {@code null}
     * @return trimmed email or {@code null} if input was {@code null}
     */
    private static String sanitizeEmail(String email) {
        return email == null ? null : email.trim();
    }

    /**
     * Returns the unique player identifier.
     *
     * @return player's {@link UUID}
     */
    public UUID getPlayerID() {
        return playerID;
    }

    /**
     * Returns the player's username.
     *
     * @return username, never trimmed here (already sanitized at set time)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the player's username after trimming. If the provided username is
     * {@code null} or empty after trimming, the method does nothing.
     *
     * @param username new username to set (trimmed internally)
     */
    public void setUsername(String username) {
        String cleaned = sanitizeUsername(username);
        if (cleaned == null || cleaned.isEmpty()) {
            return;
        }
        this.username = cleaned;
    }

    /**
     * Returns the player's email.
     *
     * @return email, or {@code null} if not set
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the player's email (trimmed).
     *
     * @param email new email, may be {@code null}
     */
    public void setEmail(String email) {
        this.email = sanitizeEmail(email);
    }

    /**
     * Indicates if this player is a guest (no persistent password).
     *
     * @return {@code true} if guest account, {@code false} otherwise
     */
    public boolean isGuest() {
        return guest;
    }

    /**
     * Returns the {@link Score} object associated with this player.
     *
     * @return score details
     */
    public Score getScoreDetails() {
        return score;
    }

    /**
     * Convenience to calculate the player's current score using the {@link Score}
     * implementation.
     *
     * @return calculated score as an {@code int}
     */
    public int getScore() {
        return score.calculateScore();
    }

    /**
     * Returns the {@link Progress} instance used to manage the game's lifecycle
     * and hint usage for this player.
     *
     * @return player's progress manager
     */
    public Progress getProgress() {
        return progress;
    }

    /**
     * Returns the certificate awarded to the player, if any.
     *
     * @return {@link Certificate} or {@code null} if none
     */
    public Certificate getCertificate() {
        return certificate;
    }

    /**
     * Assigns a certificate to the player.
     *
     * @param certificate certificate to assign; may be {@code null} to clear
     */
    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * Returns whether the player currently has a certificate.
     *
     * @return {@code true} if a certificate is present, otherwise {@code false}
     */
    public boolean hasCertificate() {
        return certificate != null;
    }

    /**
     * Clears any certificate assigned to this player.
     */
    public void clearCertificate() {
        this.certificate = null;
    }

    /**
     * Returns an unmodifiable snapshot of the player's current inventory.
     *
     * Snapshot is produced while synchronizing on {@link #inventoryLock} to
     * ensure a consistent view.
     *
     * @return unmodifiable {@link List} copy of inventory items
     */
    public List<Item> getInventorySnapshot() {
        synchronized (inventoryLock) {
            return Collections.unmodifiableList(new ArrayList<>(inventory));
        }
    }

    /**
     * Adds an item to the inventory.
     *
     * @param item item to add; if {@code null} the method returns {@code false}
     * @return {@code true} if the item was added, {@code false} otherwise
     */
    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }
        synchronized (inventoryLock) {
            inventory.add(item);
            return true;
        }
    }

    /**
     * Removes the first matching item from the inventory.
     *
     * Matching uses {@link #itemsMatch(Item, Item)} which compares identity,
     * id or name.
     *
     * @param item item to remove; if {@code null} the method returns {@code false}
     * @return {@code true} if an item was removed, {@code false} otherwise
     */
    public boolean removeItem(Item item) {
        if (item == null) {
            return false;
        }
        synchronized (inventoryLock) {
            int index = indexOfItem(item);
            if (index >= 0) {
                inventory.remove(index);
                return true;
            }
            return false;
        }
    }

    /**
     * Clears the player's inventory.
     *
     * Operation is synchronized on {@link #inventoryLock}.
     */
    public void clearInventory() {
        synchronized (inventoryLock) {
            inventory.clear();
        }
    }

    /**
     * Checks whether the inventory contains an item matching the provided one.
     *
     * @param item candidate item to check for; may be {@code null}
     * @return {@code true} if a matching item exists, {@code false} otherwise
     */
    public boolean hasItem(Item item) {
        if (item == null) {
            return false;
        }
        synchronized (inventoryLock) {
            return indexOfItem(item) >= 0;
        }
    }

    /**
     * Uses an item on an optional target. Current implementation simply removes
     * the item from inventory; side-effects and target handling are left to
     * higher-level code.
     *
     * @param item   item to use
     * @param target optional target for the item (may be {@code null})
     * @return {@code true} if the item was consumed/removed, otherwise {@code false}
     */
    public boolean useItem(Item item, Object target) {
        return removeItem(item);
    }

    /**
     * Return the index of the first inventory item matching {@code candidate}
     * or -1 if no match exists. Matching details are delegated to
     * {@link #itemsMatch(Item, Item)}.
     *
     * Must be called while holding {@link #inventoryLock} if used externally.
     *
     * @param candidate candidate item to compare
     * @return index of matching item or -1
     */
    private int indexOfItem(Item candidate) {
        for (int i = 0; i < inventory.size(); i++) {
            Item stored = inventory.get(i);
            if (itemsMatch(stored, candidate)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determines whether two {@link Item} instances should be considered the
     * same for inventory operations. Comparison order:
     *
     * @param stored    stored item from inventory
     * @param candidate candidate item to compare
     * @return {@code true} if the items match, {@code false} otherwise
     */
    private static boolean itemsMatch(Item stored, Item candidate) {
        if (stored == candidate) {
            return true;
        }
        if (stored == null || candidate == null) {
            return false;
        }
        if (stored.getId() == candidate.getId()) {
            return true;
        }
        String storedName = stored.getName();
        String candidateName = candidate.getName();
        return storedName != null && storedName.equals(candidateName);
    }

    /**
     * Sets the player's password. If {@code raw} is {@code null} or blank the
     * stored hash is cleared and the account is converted to a guest account.
     *
     * @param raw raw password string to hash and store
     */
    public void setPassword(String raw) {
        if (raw == null || raw.isBlank()) {
            this.passwordHash = null;
            this.guest = true;
            return;
        }
        this.passwordHash = hashPassword(raw);
        this.guest = false;
    }

    /**
     * Hashes a raw password together with username and email seeds (simple
     * implementation using {@link Objects#hash(Object...)}).
     *
     * @param raw raw password to hash
     * @return deterministic hex string representing the hash
     */
    private String hashPassword(String raw) {
        String nameSeed = username == null ? "" : username;
        String emailSeed = email == null ? "" : email;
        return Integer.toHexString(Objects.hash(raw, nameSeed, emailSeed));
    }

    /**
     * Verifies a raw password against the stored password hash.
     *
     * @param raw raw password to check
     * @return {@code true} if the password matches or both stored and provided
     *         passwords are effectively empty; {@code false} otherwise
     */
    public boolean verifyPassword(String raw) {
        if (passwordHash == null) {
            return raw == null || raw.isBlank();
        }
        if (raw == null) {
            return false;
        }
        return passwordHash.equals(hashPassword(raw));
    }

    /**
     * Attempt to login with a username and password.
     *
     * If the current account has no username or is a guest, a successful
     * login will set the username to the supplied value and set the password.
     * If a username is already set it must match the supplied username and
     * the password must verify successfully.
     *
     * @param username username to authenticate with (trimmed)
     * @param password raw password to verify
     * @return {@code true} if login/authentication succeeded, {@code false}
     *         otherwise
     */
    public boolean login(String username, String password) {
        if (username == null || username.isBlank() || password == null) {
            return false;
        }
        String cleaned = sanitizeUsername(username);
        if (cleaned == null || cleaned.isEmpty()) {
            return false;
        }
        if (this.username == null || this.guest) {
            this.username = cleaned;
            setPassword(password);
            return true;
        }
        if (!this.username.equals(cleaned)) {
            return false;
        }
        boolean authenticated = verifyPassword(password);
        if (authenticated) {
            this.guest = false;
        }
        return authenticated;
    }

    /**
     * Logs the player out by marking the account as a guest.
     */
    public void logout() {
        this.guest = true;
    }

    /**
     * Allows setting a stored password hash directly (for example when loading
     * from persistent storage). Passing a null/blank value clears the hash and
     * marks the account as a guest.
     *
     * @param passwordHash direct password hash to store
     */
    public void setStoredPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            this.passwordHash = null;
            this.guest = true;
            return;
        }
        this.passwordHash = passwordHash;
        this.guest = false;
    }

    /**
     * Awards bonus points to the player's score. Points <= 0 are ignored.
     * Overflow above {@link Integer#MAX_VALUE} is clamped to
     * {@link Integer#MAX_VALUE}.
     *
     * @param points number of bonus points to add (must be positive)
     */
    public void awardBonusPoints(int points) {
        if (points <= 0) {
            return;
        }
        long total = (long) score.getPoints() + points;
        int clamped = total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
        score.setPoints(clamped);
    }

    /**
     * Grants one free hint token to the player, up to {@link Integer#MAX_VALUE}.
     * Does nothing if {@link #score} is {@code null}.
     */
    public void addFreeHintToken() {
        if (score == null) {
            return;
        }
        int current = score.getFreeHintTokens();
        if (current < Integer.MAX_VALUE) {
            score.setFreeHintTokens(current + 1);
        }
    }

    /**
     * Checks whether the player currently has any free hint tokens.
     *
     * @return {@code true} if there is at least one free hint token
     */
    public boolean hasFreeHintTokens() {
        return score != null && score.getFreeHintTokens() > 0;
    }

    /**
     * Consumes one free hint token if available.
     *
     * @return {@code true} if a token was consumed, otherwise {@code false}
     */
    public boolean consumeFreeHintToken() {
        if (score == null) {
            return false;
        }
        int current = score.getFreeHintTokens();
        if (current <= 0) {
            return false;
        }
        score.setFreeHintTokens(current - 1);
        return true;
    }

    /**
     * Returns the current count of free hint tokens (never negative).
     *
     * @return free hint token count
     */
    public int getFreeHintTokenCount() {
        return score == null ? 0 : Math.max(0, score.getFreeHintTokens());
    }

    /**
     * Records time spent (seconds) into the player's score. Non-positive
     * seconds are ignored. Accumulation is clamped to
     * {@link Integer#MAX_VALUE}.
     *
     * @param seconds number of seconds to add
     */
    public void recordTimeSpent(int seconds) {
        if (seconds <= 0) {
            return;
        }
        long total = (long) score.getTimeTaken() + seconds;
        int clamped = total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
        score.setTimeTaken(clamped);
    }

    /**
     * Attempts to solve the supplied puzzle using the provided answer. The
     * method will call {@code puzzle.trySolve(answer)} if available, otherwise
     * it will attempt to call a fallback {@code puzzle.solve()} method.
     *
     * If the puzzle is solved, {@link #recordPuzzleCompletion(Puzzle, String)}
     * is invoked to update progress and score.
     *
     * @param puzzle puzzle to attempt (may be {@code null})
     * @param answer answer string to try (may be {@code null})
     * @return {@code true} if the puzzle was solved, {@code false} otherwise
     */
    public boolean solvePuzzle(Puzzle puzzle, String answer) {
        if (puzzle == null) {
            return false;
        }
        boolean solved = false;
        try {
            solved = puzzle.trySolve(answer);
        } catch (UnsupportedOperationException | AbstractMethodError | NoSuchMethodError e) {
            try {
                solved = puzzle.solve();
            } catch (Exception ignored) {
                solved = false;
            }
        } catch (Exception ignored) {
            solved = false;
        }
        if (solved) {
            recordPuzzleCompletion(puzzle, answer);
        }
        return solved;
    }

    /**
     * Increments the count of puzzles solved in {@link Score}. This method
     * guards against integer overflow.
     */
    public void recordPuzzleSolved() {
        int current = score.getPuzzlesSolved();
        if (current < Integer.MAX_VALUE) {
            score.setPuzzlesSolved(current + 1);
        }
    }

    /**
     * Record completion for a specific puzzle and optionally the provided
     * answer. If {@code puzzle} is {@code null} the method simply increments the
     * puzzles-solved counter.
     *
     * The mutation of {@link #puzzleProgress} is synchronized on
     * {@link #progressLock}.
     *
     * @param puzzle puzzle that was completed (may be {@code null})
     * @param answer answer submitted for the puzzle (may be {@code null})
     * @return {@code true} if this call resulted in a newly-marked solved puzzle,
     *         {@code false} if the puzzle was already recorded as solved
     */
    public boolean recordPuzzleCompletion(Puzzle puzzle, String answer) {
        if (puzzle == null) {
            recordPuzzleSolved();
            return true;
        }
        synchronized (progressLock) {
            PuzzleProgressSnapshot snapshot = puzzleProgress.computeIfAbsent(
                puzzle.getPuzzleId(),
                id -> new PuzzleProgressSnapshot(id, puzzle.getDescription())
            );
            boolean newlySolved = !snapshot.isSolved();
            if (newlySolved) {
                recordPuzzleSolved();
            }
            snapshot.recordAnswer(answer, true);
            return newlySolved;
        }
    }

    /**
     * Records that a hint was used for the given puzzle and updates the total
     * hints used in {@link Score}. No-op if {@code puzzle} is {@code null}.
     *
     * Mutation and calculation of total hints is synchronized on
     * {@link #progressLock}.
     *
     * @param puzzle   puzzle for which the hint was used
     * @param hintText textual hint provided (may be {@code null})
     */
    public void recordHintUsed(Puzzle puzzle, String hintText) {
        if (puzzle == null) {
            return;
        }
        synchronized (progressLock) {
            PuzzleProgressSnapshot snapshot = puzzleProgress.computeIfAbsent(
                puzzle.getPuzzleId(),
                id -> new PuzzleProgressSnapshot(id, puzzle.getDescription())
            );
            snapshot.addHint(hintText);
            int total = 0;
            for (PuzzleProgressSnapshot entry : puzzleProgress.values()) {
                if (entry != null) {
                    total += entry.getHintCount();
                }
            }
            score.setHintsUsed(total);
        }
    }

    /**
     * Returns a list copy of the puzzle progress snapshots representing the
     * player's recorded history.
     *
     * Snapshot is produced while synchronizing on {@link #progressLock}.
     *
     * @return new {@link List} containing the snapshots
     */
    public List<PuzzleProgressSnapshot> getPuzzleProgressSnapshots() {
        synchronized (progressLock) {
            return new ArrayList<>(puzzleProgress.values());
        }
    }

    /**
     * Replaces the player's stored puzzle progress history with the supplied
     * snapshots. Null snapshots in the input list are ignored.
     *
     * Operation is synchronized on {@link #progressLock}.
     *
     * @param snapshots list of snapshots to use as the new history, or {@code null}
     *                  to clear history
     */
    public void replaceProgressHistory(List<PuzzleProgressSnapshot> snapshots) {
        synchronized (progressLock) {
            puzzleProgress.clear();
            if (snapshots == null) {
                return;
            }
            for (PuzzleProgressSnapshot snapshot : snapshots) {
                if (snapshot == null) {
                    continue;
                }
                puzzleProgress.put(snapshot.getPuzzleId(), snapshot);
            }
        }
    }

    /**
     * Computes the number of solved puzzles present in the stored progress
     * history. Access is synchronized on {@link #progressLock}.
     *
     * @return count of solved puzzles from history
     */
    public int getSolvedPuzzleCountFromHistory() {
        synchronized (progressLock) {
            int solved = 0;
            for (PuzzleProgressSnapshot snapshot : puzzleProgress.values()) {
                if (snapshot != null && snapshot.isSolved()) {
                    solved++;
                }
            }
            return solved;
        }
    }

    /**
     * Computes the total hints used across all stored puzzle progress snapshots.
     * Access is synchronized on {@link #progressLock}.
     *
     * @return total hint count from history
     */
    public int getTotalHintsUsedFromHistory() {
        synchronized (progressLock) {
            int total = 0;
            for (PuzzleProgressSnapshot snapshot : puzzleProgress.values()) {
                if (snapshot != null) {
                    total += snapshot.getHintCount();
                }
            }
            return total;
        }
    }

    /**
     * Overwrites the values in the {@link Score} object with the provided data.
     *
     * @param points        new points value
     * @param puzzlesSolved new puzzles solved count
     * @param hintsUsed     new hints used count
     * @param timeTaken     new total time taken (seconds)
     */
    public void applyScoreData(int points, int puzzlesSolved, int hintsUsed, int timeTaken) {
        score.setPoints(points);
        score.setPuzzlesSolved(puzzlesSolved);
        score.setHintsUsed(hintsUsed);
        score.setTimeTaken(timeTaken);
    }

    /**
     * Start a new game via {@link Progress}. Optionally set the available hints
     * that will be used by the progress manager.
     *
     * @param hints list of hint strings (may be {@code null} to leave unchanged)
     */
    public void startNewGame(List<String> hints) {
        if (hints != null) {
            progress.setAvailableHints(hints);
        }
        progress.startGame();
    }

    /**
     * Completes the current game via {@link Progress}.
     */
    public void completeGame() {
        progress.endGame();
    }

    /**
     * Returns whether the current game has been completed (delegates to {@link Progress}).
     *
     * @return {@code true} if the game is marked as won/completed
     */
    public boolean hasCompletedGame() {
        return progress.checkWin();
    }

    /**
     * Requests a hint from the current {@link Progress} instance.
     *
     * @return hint string or {@code null} if none available
     */
    public String requestHint() {
        return progress.useHint();
    }

    /**
     * Overwrites the set of available hints used by {@link Progress}.
     *
     * @param hints new list of hints (may be {@code null})
     */
    public void updateAvailableHints(List<String> hints) {
        progress.setAvailableHints(hints);
    }

    /**
     * Resets the hint state in {@link Progress}.
     */
    public void resetHints() {
        progress.resetHints();
    }

    /**
     * Instructs {@link Progress} to load persisted progress data for this player.
     * Implementation details depend on {@link Progress#loadProgress()}.
     */
    public void loadProgress() {
        progress.loadProgress();
    }

    /**
     * Instructs {@link Progress} to save current progress state for this player.
     * Implementation details depend on {@link Progress#saveProgress()}.
     */
    public void saveProgress() {
        progress.saveProgress();
    }

    /**
     * Equality is based on {@link #playerID}.
     *
     * @param obj other object to compare
     * @return {@code true} if {@code obj} is a {@code Player} with the same id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player) obj;
        return playerID.equals(other.playerID);
    }

    /**
     * Hash code derived from {@link #playerID}.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return playerID.hashCode();
    }

    /**
     * Short debug-friendly string representation of the player.
     *
     * @return textual representation containing id, username and guest flag
     */
    @Override
    public String toString() {
        return "Player{" + "id=" + playerID + ", username=" + username + ", guest=" + guest + "}";
    }
}


