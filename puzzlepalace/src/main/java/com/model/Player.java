package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Player {

    private final UUID playerID;
    private String username;
    private String email;
    private String passwordHash;
    private boolean guest;
    private final List<Item> inventory;
    private final Object inventoryLock = new Object();
    private final Progress progress;
    private final Score score;
    private Certificate certificate;
    private final Map<Integer, PuzzleProgressSnapshot> puzzleProgress;
    private final Object progressLock = new Object();

    public Player() {
        this(null, null, null, true);
    }

    public Player(String username, String email, String rawPassword) {
        this(username, email, rawPassword, false);
    }

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

    private void initialiseUsername(String providedUsername) {
        String cleaned = sanitizeUsername(providedUsername);
        if (this.guest || cleaned == null || cleaned.isEmpty()) {
            this.username = generateGuestAlias();
            this.guest = true;
        } else {
            this.username = cleaned;
        }
    }

    private String generateGuestAlias() {
        return "Guest-" + playerID.toString().substring(0, 8);
    }

    private static String sanitizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    private static String sanitizeEmail(String email) {
        return email == null ? null : email.trim();
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        String cleaned = sanitizeUsername(username);
        if (cleaned == null || cleaned.isEmpty()) {
            return;
        }
        this.username = cleaned;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = sanitizeEmail(email);
    }

    public boolean isGuest() {
        return guest;
    }

    public Score getScoreDetails() {
        return score;
    }

    public int getScore() {
        return score.calculateScore();
    }

    public Progress getProgress() {
        return progress;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public boolean hasCertificate() {
        return certificate != null;
    }

    public void clearCertificate() {
        this.certificate = null;
    }

    public List<Item> getInventorySnapshot() {
        synchronized (inventoryLock) {
            return Collections.unmodifiableList(new ArrayList<>(inventory));
        }
    }

    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }
        synchronized (inventoryLock) {
            inventory.add(item);
            return true;
        }
    }

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

    public void clearInventory() {
        synchronized (inventoryLock) {
            inventory.clear();
        }
    }

    public boolean hasItem(Item item) {
        if (item == null) {
            return false;
        }
        synchronized (inventoryLock) {
            return indexOfItem(item) >= 0;
        }
    }

    public boolean useItem(Item item, Object target) {
        return removeItem(item);
    }

    private int indexOfItem(Item candidate) {
        for (int i = 0; i < inventory.size(); i++) {
            Item stored = inventory.get(i);
            if (itemsMatch(stored, candidate)) {
                return i;
            }
        }
        return -1;
    }

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

    public void setPassword(String raw) {
        if (raw == null || raw.isBlank()) {
            this.passwordHash = null;
            this.guest = true;
            return;
        }
        this.passwordHash = hashPassword(raw);
        this.guest = false;
    }

    private String hashPassword(String raw) {
        String nameSeed = username == null ? "" : username;
        String emailSeed = email == null ? "" : email;
        return Integer.toHexString(Objects.hash(raw, nameSeed, emailSeed));
    }

    public boolean verifyPassword(String raw) {
        if (passwordHash == null) {
            return raw == null || raw.isBlank();
        }
        if (raw == null) {
            return false;
        }
        return passwordHash.equals(hashPassword(raw));
    }

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

    public void logout() {
        this.guest = true;
    }

    public void setStoredPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            this.passwordHash = null;
            this.guest = true;
            return;
        }
        this.passwordHash = passwordHash;
        this.guest = false;
    }

    public void awardBonusPoints(int points) {
        if (points <= 0) {
            return;
        }
        long total = (long) score.getPoints() + points;
        int clamped = total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
        score.setPoints(clamped);
    }
    public void addFreeHintToken() {
        if (score == null) {
            return;
        }
        int current = score.getFreeHintTokens();
        if (current < Integer.MAX_VALUE) {
            score.setFreeHintTokens(current + 1);
        }
    }

    public boolean hasFreeHintTokens() {
        return score != null && score.getFreeHintTokens() > 0;
    }

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

    public int getFreeHintTokenCount() {
        return score == null ? 0 : Math.max(0, score.getFreeHintTokens());
    }

    public void recordTimeSpent(int seconds) {
        if (seconds <= 0) {
            return;
        }
        long total = (long) score.getTimeTaken() + seconds;
        int clamped = total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
        score.setTimeTaken(clamped);
    }

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

    public void recordPuzzleSolved() {
        int current = score.getPuzzlesSolved();
        if (current < Integer.MAX_VALUE) {
            score.setPuzzlesSolved(current + 1);
        }
    }

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

    public List<PuzzleProgressSnapshot> getPuzzleProgressSnapshots() {
        synchronized (progressLock) {
            return new ArrayList<>(puzzleProgress.values());
        }
    }

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

    public void applyScoreData(int points, int puzzlesSolved, int hintsUsed, int timeTaken) {
        score.setPoints(points);
        score.setPuzzlesSolved(puzzlesSolved);
        score.setHintsUsed(hintsUsed);
        score.setTimeTaken(timeTaken);
    }
    
    public void startNewGame(List<String> hints) {
        if (hints != null) {
            progress.setAvailableHints(hints);
        }
        progress.startGame();
    }

    public void completeGame() {
        progress.endGame();
    }

    public boolean hasCompletedGame() {
        return progress.checkWin();
    }

    public String requestHint() {
        return progress.useHint();
    }

    public void updateAvailableHints(List<String> hints) {
        progress.setAvailableHints(hints);
    }

    public void resetHints() {
        progress.resetHints();
    }

    public void loadProgress() {
        progress.loadProgress();
    }

    public void saveProgress() {
        progress.saveProgress();
    }

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

    @Override
    public int hashCode() {
        return playerID.hashCode();
    }

    @Override
    public String toString() {
        return "Player{" + "id=" + playerID + ", username=" + username + ", guest=" + guest + "}";
    }
}

