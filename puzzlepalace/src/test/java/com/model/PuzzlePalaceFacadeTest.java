package com.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PuzzlePalaceFacadeTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private PuzzlePalaceFacade newFacade() throws Exception {
        File store = temp.newFile("users.json");
        PuzzlePalaceFacade facade = new PuzzlePalaceFacade(store.getAbsolutePath());
        Field randomField = PuzzlePalaceFacade.class.getDeclaredField("random");
        randomField.setAccessible(true);
        randomField.set(facade, new Random(0));
        return facade;
    }

    @Test
    public void createAccountPersistsUserAndPreventsDuplicates() throws Exception {
        PuzzlePalaceFacade facade = newFacade();
        Player created = facade.createAccount("NewUser", "Password1!");
        assertNotNull(created);

        Path path = Path.of(facade.getUserDataPath());
        String json = Files.readString(path);
        assertTrue(json.contains("NewUser"));
        assertTrue(facade.readUserDataFileContents().contains("NewUser"));

        Player duplicate = facade.createAccount("NewUser", "AnotherPass1!");
        org.junit.Assert.assertNull(duplicate);
    }

    @Test
    public void loginInteractWithPuzzleAndTokens() throws Exception {
        PuzzlePalaceFacade facade = newFacade();
        Player player = facade.login("PlayerOne", "SecretPass1!");
        assertNotNull(player);

        Puzzle puzzle = facade.getActivePuzzle();
        assertNotNull(puzzle);

        String statusDescription = facade.describeCurrentPuzzleStatus();
        assertTrue(statusDescription.contains("Difficulty: Easy"));

        List<Room> rooms = facade.listAvailableRooms();
        assertFalse(rooms.isEmpty());

        int puzzleId = puzzle.getPuzzleId();
        String hint = facade.requestHint(puzzleId);
        assertNotNull(hint);
        assertFalse(facade.hasFreeHintToken());

        HintRequestResult failure = facade.useFreeHintToken(puzzleId);
        assertFalse(failure.isSuccess());

        player.addFreeHintToken();
        HintRequestResult success = facade.useFreeHintToken(puzzleId);
        assertTrue(success.isSuccess());
        assertTrue(success.isTokenConsumed());

        boolean solved;
        if (puzzle instanceof MathChallengePuzzle) {
            double solution = ((MathChallengePuzzle) puzzle).getSolution();
            solved = facade.submitPuzzleAnswer(puzzleId, String.valueOf(solution));
        } else {
            solved = facade.submitPuzzleAnswer(puzzleId, "42");
        }
        assertTrue(solved);
        assertTrue(player.getScoreDetails().getPuzzlesSolved() >= 1);

        facade.resetProgressToFirstRoom();
        Puzzle resetPuzzle = facade.getActivePuzzle();
        assertNotNull(resetPuzzle);
        assertTrue("UNSOLVED".equalsIgnoreCase(resetPuzzle.getStatus()));
    }
}