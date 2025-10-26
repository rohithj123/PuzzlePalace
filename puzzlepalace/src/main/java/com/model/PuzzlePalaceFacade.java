package com.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;



public class PuzzlePalaceFacade {

    private static final int FREEZE_TIMER_DURATION_SECONDS = 10;
    private Player currentPlayer;
    private Progress progress;
    private Leaderboard leaderboard;
    private Settings settings;
    private Room currentRoom;
    private Puzzle activePuzzle;
    private final PlayerManager playerManager;
    private final String userDataPath;
    private Instant puzzleStartTime;
    private long lastCompletionSeconds;
    private final List<Room> availableRooms;
    private int currentRoomIndex;
    private final Random random = new Random();
    private int consecutiveHintFreeSolves;
    private boolean freezeTimerActive;
    private Instant freezeEndTime;
    private long freezeStartElapsedSeconds;
    private long freezeCompensationSeconds;

    public PuzzlePalaceFacade() {
        this("json/users.json");
    }

    public PuzzlePalaceFacade(String userDataPath) {
        this.playerManager = new PlayerManager();
        this.userDataPath = userDataPath;
        this.settings = new Settings();
        this.availableRooms = new ArrayList<>();
        this.currentRoomIndex = -1;
        loadUsers();
    }

    private void loadUsers() {
        try {
            List<Player> loaded = playerManager.loadPlayersFromFile(userDataPath);
            if (loaded == null || loaded.isEmpty()) {
                seedDefaultPlayers();
            }
        } catch (NoClassDefFoundError error) {
            System.out.println("PuzzlePalaceFacade: JSON parser unavailable, using fallback players.");
            seedDefaultPlayers();
        }
    }

    private void seedDefaultPlayers() {
        Player fallback = new Player("PlayerOne", "playerone@example.com", "SecretPass1!");
        playerManager.addPlayer(fallback);
    }

    public Player login(String userName, String password) {
        Player authenticated = playerManager.authenticate(userName, password);
        if (authenticated == null) {
            return null;
        }
        this.currentPlayer = authenticated;
        this.progress = currentPlayer.getProgress();
        if (this.progress != null) {
            this.progress.loadProgress();
        }
        startEscapeRoom();
        return this.currentPlayer;
    }

    private Room summarisePlayerRoom(Player player) {
        if (player == null) {
            availableRooms.clear();
            activePuzzle = null;
            currentRoom = null;
            currentRoomIndex = -1;
            return null;
        }
        if (availableRooms.isEmpty() || currentRoom == null) {
            buildRoomsFor(player);
        }
        return currentRoom;
    }

    private void buildRoomsFor(Player player) {
        availableRooms.clear();
        currentRoom = null;
        activePuzzle = null;
        currentRoomIndex = -1;
        puzzleStartTime = null;
        resetFreezeState();
        consecutiveHintFreeSolves = 0;

        if (player == null) {
            lastCompletionSeconds = 0L;
            return;
        }

        List<Room> rooms = createRoomsForDifficulty(getSelectedDifficulty());
        availableRooms.addAll(rooms);

        if (!availableRooms.isEmpty()) {
            currentRoomIndex = 0;
            currentRoom = availableRooms.get(0);
            activePuzzle = currentRoom.getPuzzles().isEmpty() ? null : currentRoom.getPuzzles().get(0);
        }

        Score score = player.getScoreDetails();
        lastCompletionSeconds = score != null ? Math.max(0, score.getTimeTaken()) : 0;
    }

    private List<Room> createRoomsForDifficulty(Settings.Difficulty difficulty) {
        if (difficulty == null) {
            difficulty = Settings.Difficulty.EASY;
        }
        switch (difficulty) {
            case MEDIUM:
                return createMediumRooms();
            case HARD:
                return createHardRooms();
            case EASY:
            default:
                return createEasyRooms();
        }
    }

    private <T> T chooseRandom(List<T> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        return options.get(random.nextInt(options.size()));
    }



    private List<Room> createEasyRooms() {
        List<Room> rooms = new ArrayList<>();
        Settings.Difficulty difficulty = Settings.Difficulty.EASY;
    
        MathChallengePuzzle mathPuzzle = chooseRandom(List.of(
            new MathChallengePuzzle(
                2001,
                "A glowing equation hovers over the vault: (12 + 8) / 4 + 3^2 = ?\n" +
                    "Punch in the final number to power the escape hatch.",
                14,
                "Work from the inside out—parentheses first!",
                "Remember that exponents come before addition.",
                "After dividing by four, you still need to add the value of 3².",
                "Re-evaluate each step slowly — order of operations (PEMDAS) matters."
            ),
            new MathChallengePuzzle(
                2002,
                "Crystalline numbers orbit the lock: 6 + (18 / 3) + 2^3 = ?\n" +
                    "Type the total to calm the restless orbs.",
                20,
                "Start by taming the division inside the parentheses.",
                "2^3 means two multiplied by itself three times.",
                "Add the three partial results together for the final surge.",
                "Check your arithmetic by recomputing each component separately."
            ),
            new MathChallengePuzzle(
                2003,
                "An animated chalkboard scribbles: 7 × 2 + 15 / 3 = ?\n" +
                    "Give the correct value to silence the squeaky chalk.",
                19,
                "Let multiplication take the stage before addition.",
                "Fifteen divided by three is a friendly whole number.",
                "Combine the product of seven and two with the division result.",
                "Do multiplication and division left-to-right before adding."
            ),
            new MathChallengePuzzle(
                2004,
                "Lanterns blink in rhythm: (5^2 - 10) / 5 + 4 = ?\n" +
                    "Whisper the answer to steady their light.",
                7,
                "Square five before touching the subtraction.",
                "Divide the new numerator by five.",
                "Finish by adding the final four.",
                "Work the numerator fully, then handle the division to avoid mistakes."
            ),
            new MathChallengePuzzle(
                2005,
                "Pixies scrawl a dare: 3 × (4 + 5) - 6 = ?\n" +
                    "Solve it before the ink flutters away.",
                21,
                "Add the numbers inside the parentheses first.",
                "Multiply that total by three.",
                "Don't forget to subtract the final six.",
                "Re-check each arithmetic step to ensure no slip in addition or multiplication."
            )
        ));
    
        Room mathRoom = new Room();
        mathRoom.setRoomId("math-gate");
        mathRoom.setName("Math Gate");
        mathRoom.setDescription("A glowing equation blocks the exit.");
        mathRoom.setDifficulty(difficulty.getDisplayName());
        mathRoom.setEstimatedTimeMinutes(5);
        mathRoom.addPuzzle(mathPuzzle);
    
        SimplePuzzle wordPuzzle = chooseRandom(List.of(
            new SimplePuzzle(
                2006,
                "Shelves whisper riddles: unscramble the letters T L G H I to reveal the password.",
                "light",
                "Think about what helps you see in the dark.",
                "The answer is something that shines brightly.",
                "It has five letters and often hangs from a fixture.",
                "Try rearranging to form a common word associated with illumination."
            ),
            new SimplePuzzle(
                2007,
                "A clockwork raven scatters letters: C O C K L. Reassemble its lost name.",
                "clock",
                "It's fond of ticking on the wall.",
                "Two of the letters repeat, just like its steady chime.",
                "Think of something with hands but no fingers.",
                "Arrange the letters to spell an object that tells time."
            ),
            new SimplePuzzle(
                2008,
                "A rolled map murmurs: 'I have cities, but no houses. I have mountains, but no trees.' What am I?",
                "map",
                "You can fold me up and tuck me away.",
                "Explorers rely on me long before they set foot outside.",
                "I'm flat, often printed on paper, and used for navigation.",
                "Consider common riddle answers about representations of geography."
            ),
            new SimplePuzzle(
                2009,
                "Sparks swirl into the letters E P A L C A. Arrange them to unlock the study door.",
                "Palace",
                "The word describes exactly what you're trying to do.",
                "It starts and ends with the same letter.",
                "It contains six letters.",
                "Think of leaving a place or breaking free."
            ),
            new SimplePuzzle(
                2010,
                "A musical note poses a riddle: 'I have keys but no locks, and hammers that never strike.' What am I?",
                "piano",
                "People sit before me to fill the air with melodies.",
                "My keys are meant to be pressed, not carried.",
                "I usually have 88 keys in the modern form.",
                "Consider large musical instruments with keys and pedals."
            )
        ));
    
        Room wordRoom = new Room();
        wordRoom.setRoomId("word-puzzle");
        wordRoom.setName("Word Puzzle Room");
        wordRoom.setDescription("Stacks of books hide a secret word.");
        wordRoom.setDifficulty(difficulty.getDisplayName());
        wordRoom.setEstimatedTimeMinutes(5);
        wordRoom.addPuzzle(wordPuzzle);
    
        SimplePuzzle logicPuzzle = chooseRandom(List.of(
            new SimplePuzzle(
                2011,
                "The final vault presents three gemstone buttons: Ruby says 'Sapphire is the key,' " +
                    "Sapphire insists 'I am not the key,' and Emerald claims 'Ruby is lying.' " +
                    "Only one statement can be true. Which button will open the vault?",
                "sapphire",
                "Remember, exactly one of the statements is telling the truth.",
                "Try assuming each gemstone is correct and see which assumption keeps only a single statement true.",
                "If Sapphire were the key, check the truth-values of the other two statements.",
                "Work through each possible true-statement scenario until only one statement remains true."
            ),
            new SimplePuzzle(
                2012,
                "Three levers await: Lever A says 'Lever B is telling the truth.' Lever B says 'Lever C opens the door.' " +
                    "Lever C says 'Lever A is lying.' Only one statement can be true. Which lever should you pull?",
                "lever a",
                "Start by testing what happens if Lever A really opened the door.",
                "If Lever A is right, do the other statements stay false?",
                "Count truths for each assumption—only one true statement is allowed.",
                "Eliminate contradictions and pick the lever that leaves exactly one true claim."
            ),
            new SimplePuzzle(
                2013,
                "Three torches burn blue: Torch A claims 'Torch B is the safe choice.' Torch B argues 'Torch C is the safe choice.' " +
                    "Torch C declares 'Torch A lies.' Only one statement is true. Which torch reveals the passage?",
                "torch b",
                "Pick one torch and imagine it is correct.",
                "The right answer leaves the other two statements false.",
                "Test each torch's claim and count true vs false outcomes.",
                "Use elimination: whichever choice yields exactly one true statement is correct."
            ),
            new SimplePuzzle(
                2014,
                "A trio of runes glow: Rune A whispers 'Rune C is not the answer.' Rune B boasts 'Rune A is wrong.' " +
                    "Rune C states 'I am the correct rune.' Only one statement can be true. Which rune do you trace?",
                "rune c",
                "Try taking Rune C at its word first.",
                "Exactly one rune tells the truth—two must be lying.",
                "Check consistency: if C is true, A and B must both be false.",
                "Confirm the chosen rune does not create contradictions among the three claims."
            ),
            new SimplePuzzle(
                2015,
                "Three statues guard the exit. The owl says 'The fox lies.' The fox says 'The hare knows the way.' " +
                    "The hare says 'The owl speaks truth.' Only one statement is true. Which statue hides the release switch?",
                "hare",
                "Follow the chain of claims starting with the hare.",
                "The correct statue's statement makes the other two collapse.",
                "Assume each statue's claim is true in turn and see which scenario yields only one truth.",
                "Check how each assumption affects the truth of the other two statements."
            )
        ));
    
        Room logicRoom = new Room();
        logicRoom.setRoomId("logic-vault");
        logicRoom.setName("Logic Vault");
        logicRoom.setDescription("Gemstone buttons challenge your reasoning.");
        logicRoom.setDifficulty(difficulty.getDisplayName());
        logicRoom.setEstimatedTimeMinutes(5);
        logicRoom.addPuzzle(logicPuzzle);
    
        rooms.add(mathRoom);
        rooms.add(wordRoom);
        rooms.add(logicRoom);
        return rooms;
    }
    
    private List<Room> createMediumRooms() {
        List<Room> rooms = new ArrayList<>();
        Settings.Difficulty difficulty = Settings.Difficulty.MEDIUM;
    
        MathChallengePuzzle mathPuzzle = chooseRandom(List.of(
            new MathChallengePuzzle(
                2101,
                "Runed gears align to display: (18 / 3) + 4 × (5 - 1) = ?\n" +
                    "Set the mechanism to the correct number to advance.",
                22,
                "Pay attention to the operations inside the parentheses first.",
                "After dividing eighteen by three, tackle the multiplication.",
                "Your final step subtracts nothing—add the two partial results together.",
                "Compute each bracketed piece separately, then combine."
            ),
            new MathChallengePuzzle(
                2102,
                "Steam vents pulse in rhythm: 6 × (7 - 2) + 4^2 = ?\n" +
                    "Balance the pressure with the right result.",
                46,
                "Complete the subtraction before multiplying.",
                "4^2 is the same as four times four.",
                "Add the two results carefully—the machine is picky.",
                "Be careful with the order: parentheses, exponents, multiplication, then addition."
            ),
            new MathChallengePuzzle(
                2103,
                "Clockwork scribes etch: ((3^3) + 24) / 3 + 2 = ?\n" +
                    "Speak the answer to quiet the gears.",
                19,
                "Cube three first to calm the eager scribes.",
                "Add the twenty-four before dividing.",
                "Once divided, don't forget the final +2.",
                "Work top-down through the nested parentheses to avoid errors."
            ),
            new MathChallengePuzzle(
                2104,
                "Brass panels flicker: 5 × (8 + 2) - 3^2 = ?\n" +
                    "The door only opens for the exact figure.",
                41,
                "Resolve the parentheses before touching multiplication.",
                "Square the three before subtracting.",
                "Subtract the square from the product at the end.",
                "Double-check multiplication before performing the final subtraction."
            ),
            new MathChallengePuzzle(
                2105,
                "A metronome ticks out: (64 / 8) + (7 × 3) - 5 = ?\n" +
                    "Match the tempo with your calculation.",
                24,
                "The division gives you a neat whole number.",
                "Seven times three sits in the middle waiting to be added.",
                "Complete the subtraction last to keep the beat.",
                "Compute each parenthetical group separately, then combine them."
            )
        ));
    
        Room mathRoom = new Room();
        mathRoom.setRoomId("math-gears");
        mathRoom.setName("Clockwork Calculations");
        mathRoom.setDescription("Intricate gears demand a precise calculation.");
        mathRoom.setDifficulty(difficulty.getDisplayName());
        mathRoom.setEstimatedTimeMinutes(7);
        mathRoom.addPuzzle(mathPuzzle);
    
        SimplePuzzle wordPuzzle = chooseRandom(List.of(
            new SimplePuzzle(
                2106,
                "Carved runes glow softly: Arrange the letters L A E R P S to reveal the password whispered by the mages.",
                "pearls",
                "Think of treasure formed within a humble shell.",
                "The solution is plural and glimmers brightly.",
                "These treasures are often strung together as jewelry.",
                "Try rearranging into a common word associated with jewelry and shine."
            ),
            new SimplePuzzle(
                2107,
                "A silver mirror mistypes itself: N E C H A N T. Restore the spell's true command.",
                "enchant",
                "The proper word begins with the same letter it ends with.",
                "It's the very action you'd use to empower a charm.",
                "Consider the verb used when imbuing magic.",
                "Fix the typo by moving one letter into place to read as a known magical verb."
            ),
            new SimplePuzzle(
                2108,
                "An illuminated manuscript poses a riddle:\n" +
                    "Pages without ink,\n" +
                    "Worlds in every fold,\n" +
                    "Travelers trace my links.\n" +
                    "Name what you behold.",
                "atlas",
                "Focus on the first letters of each line.",
                "It's thicker than a map and packed with destinations.",
                "Think of a book of maps rather than a single map.",
                "The acrostic points to a navigational collection."
            ),
            new SimplePuzzle(
                2109,
                "A whispering quill offers a clue: Shift each letter in UIF QBTTXPSE one step backward to free the library.",
                "the password",
                "Treat it like a simple Caesar cipher.",
                "Every letter hides just one step beyond the truth.",
                "Reverse the shift by moving each letter back one in the alphabet.",
                "Spaces remain spaces—only letters are shifted."
            ),
            new SimplePuzzle(
                2110,
                "A mosaic of tiles says: 'Steal the first letter from every word in the phrase \"Brilliant Owls Rarely Nap Easily.\"'",
                "borne",
                "Collect the initials carefully.",
                "You aren't rearranging—just extracting.",
                "The phrase gives you the letters in order: B O R N E.",
                "Read the first letter of each word consecutively to form the answer."
            )
        ));
    
        Room wordRoom = new Room();
        wordRoom.setRoomId("word-runes");
        wordRoom.setName("Rune Library");
        wordRoom.setDescription("Ancient runes hide a shimmering word.");
        wordRoom.setDifficulty(difficulty.getDisplayName());
        wordRoom.setEstimatedTimeMinutes(7);
        wordRoom.addPuzzle(wordPuzzle);
    
        SimplePuzzle logicPuzzle = chooseRandom(List.of(
            new SimplePuzzle(
                2111,
                "Three clockwork gears are labeled A, B, and C. A claims 'B's statement is false.' " +
                    "B insists 'C is the key.' C declares 'B is lying.' Exactly one statement is true. " +
                    "Which gear unlocks the door? (Answer with A, B, or C)",
                "C",
                "If B were correct, what would that mean for the others?",
                "Try assuming each gear is the key and count how many statements stay true.",
                "Only one statement can be true—find the assumption that makes that possible.",
                "Work through the logic by marking each statement true/false for each assumption."
            ),
            new SimplePuzzle(
                2112,
                "Three enchanted books debate: Volume A says 'Volume B lies.' Volume B says 'Volume C holds the key.' " +
                    "Volume C says 'Volume A speaks truth.' Exactly one statement is true. Which volume should you open?",
                "volume a",
                "Imagine Volume A is telling the truth and see what follows.",
                "Two volumes must be wrong—track the consequences.",
                "Eliminate inconsistent scenarios until one remains.",
                "Test each volume being truthful and count total truths to find the valid case."
            ),
            new SimplePuzzle(
                2113,
                "A triad of portals shimmer. Portal Sun says 'Moon leads nowhere.' Portal Moon says 'Star is the exit.' " +
                    "Portal Star says 'Sun tells lies.' Exactly one statement holds. Which portal do you enter?",
                "moon",
                "Start by trusting the Moon and testing the others.",
                "Only one claim survives—choose the portal that makes it possible.",
                "Assume each portal's claim and check truth consistency across all three.",
                "The correct portal leaves exactly one true statement and two false ones."
            ),
            new SimplePuzzle(
                2114,
                "Gargoyle guardians boast:\n" +
                    "North: 'East opens the gate.'\n" +
                    "East: 'West speaks falsehoods.'\n" +
                    "West: 'North is lying.'\n" +
                    "Exactly one direction can be trusted. Which guardian's lever do you pull?",
                "north",
                "Test each direction as if it were correct.",
                "Remember only one statement survives your test.",
                "Assume one guardian tells the truth and verify the others become false.",
                "Use elimination to find the single consistent truth."
            ),
            new SimplePuzzle(
                2115,
                "Three stained-glass windows hum. Azure says 'Crimson is wrong.' Crimson says 'Gold hides the passage.' " +
                    "Gold says 'Azure tells the truth.' Only one window's words are accurate. Which color slides open?",
                "crimson",
                "Assume Crimson is right and check the others.",
                "You want exactly one truth—the correct window makes it happen.",
                "Work through each assumption and tally truth values.",
                "Pick the color that leaves only one true claim among the three."
            )
        ));
    
        Room logicRoom = new Room();
        logicRoom.setRoomId("logic-gears");
        logicRoom.setName("Gearwork Logic");
        logicRoom.setDescription("Synchronised gears debate which one is vital.");
        logicRoom.setDifficulty(difficulty.getDisplayName());
        logicRoom.setEstimatedTimeMinutes(7);
        logicRoom.addPuzzle(logicPuzzle);
    
        rooms.add(mathRoom);
        rooms.add(wordRoom);
        rooms.add(logicRoom);
        return rooms;
    }
    
    private List<Room> createHardRooms() {
        List<Room> rooms = new ArrayList<>();
        Settings.Difficulty difficulty = Settings.Difficulty.HARD;
    
        MathChallengePuzzle mathPuzzle = chooseRandom(List.of(
            new MathChallengePuzzle(
                2201,
                "A crystalline equation pulses: ((4^3) + 6 × 5 - 18) / 2 = ?\n" +
                    "Only the correct final value will stabilise the portal.",
                38,
                "Resolve the exponent before anything else.",
                "Handle the multiplication and subtraction before dividing.",
                "Once the numerator is ready, divide by two to finish.",
                "Recompute numerator components individually to verify final division."
            ),
            new MathChallengePuzzle(
                2202,
                "Arcane glyphs spiral: (9 × 7) - (4^2) + 3^3 = ?\n" +
                    "Recite the total to keep the glyphs from exploding.",
                74,
                "Keep multiplication and exponents in order.",
                "Remember that 3^3 is three times three times three.",
                "Combine the results carefully—signs matter.",
                "Work stepwise and re-check signs when summing all parts."
            ),
            new MathChallengePuzzle(
                2203,
                "Floating crystals ask: ((5^2) + 48) / 3 - 2^3 = ?\n" +
                    "Answer before the crystals drift apart.",
                17,
                "Square five first, then add forty-eight.",
                "Divide by three before handling the final exponent.",
                "Subtract the value of 2^3 to finish.",
                "Validate each stage to avoid confusion between division and exponent order."
            ),
            new MathChallengePuzzle(
                2204,
                "Lightning arcing across the room spells: (3 × 14) + (6^2 / 3) - 11 = ?\n" +
                    "Only the precise answer will ground the energy.",
                53,
                "Square the six before dividing.",
                "Treat the multiplication and division separately before combining.",
                "Remember to subtract eleven at the end.",
                "Check division results carefully—they can change the final sum by a lot."
            ),
            new MathChallengePuzzle(
                2205,
                "A dragon statue intones: ((8^2) - 5 × 7 + 36) / 4 = ?\n" +
                    "Satisfy the statue with the correct quotient.",
                21,
                "Compute the exponent first.",
                "Group the multiplication before combining terms.",
                "Divide the final numerator by four.",
                "Re-evaluate the numerator arithmetic twice to be safe."
            )
        ));
    
        Room mathRoom = new Room();
        mathRoom.setRoomId("math-portal");
        mathRoom.setName("Arcane Calculus");
        mathRoom.setDescription("Mystic numbers swirl around a crystal portal.");
        mathRoom.setDifficulty(difficulty.getDisplayName());
        mathRoom.setEstimatedTimeMinutes(9);
        mathRoom.addPuzzle(mathPuzzle);
    
        SimplePuzzle wordPuzzle = chooseRandom(List.of(
            new SimplePuzzle(
                2206,
                "A riddle is etched into the lock:\n" +
                    "Sentinels guard the ancient vault.\n" +
                    "Allies answer every call.\n" +
                    "Fables unlock hidden truths.\n" +
                    "Enter the word they form.",
                "safe",
                "Focus on the first letters of each line.",
                "Those letters combine to form a single, familiar word.",
                "It's exactly what the vault wants to be.",
                "Collect the initial letters S, A, F, E to read the answer."
            ),
            new SimplePuzzle(
                2207,
                "Runes shimmer with an anagram: T R A N S F O R M. Reveal the command that stabilises the portal.",
                "transform",
                "The letters already spell a word—shuffle them until it sounds like powerful magic.",
                "It begins with the same letter as 'transmute'.",
                "Look for a common English verb that fits the letters.",
                "Try permutations that make a strong single-word command."
            ),
            new SimplePuzzle(
                2208,
                "A prophetic mural chants:\n" +
                    "Guardians trade riddled lore,\n" +
                    "Atop the silent keeps.\n" +
                    "Legends echo evermore,\n" +
                    "Learn the word that sleeps.\n" +
                    "Take the last letter of every line.",
                "rope",
                "Read only the final letters this time.",
                "Together they form something you might climb.",
                "Check the last character of each line and assemble them in order.",
                "The resulting letters spell an object used to ascend."
            ),
            new SimplePuzzle(
                2209,
                "A brass plaque warns: 'Swap every vowel in the word ORACLE with the next vowel in the alphabet to reveal the password.'",
                "uricli",
                "A becomes E, E becomes I, and so on—wrap back to A after U.",
                "Only vowels move; consonants stay put.",
                "Apply the vowel shift to each vowel in ORACLE in sequence.",
                "Verify each replaced vowel against the vowel cycle (A→E→I→O→U→A)."
            ),
            new SimplePuzzle(
                2210,
                "A cursed dictionary flips to pages whose numbers spell 19-8-1-4-15-23. Decode the hidden word.",
                "shadow",
                "Match each number to its alphabet position.",
                "The letters describe something that follows you closely.",
                "Translate 19→S, 8→H, 1→A, 4→D, 15→O, 23→W.",
                "Assemble the letters in sequence to reveal the answer."
            )
        ));
    
        Room wordRoom = new Room();
        wordRoom.setRoomId("word-vault");
        wordRoom.setName("Vault of Verses");
        wordRoom.setDescription("Poetic wards conceal the password.");
        wordRoom.setDifficulty(difficulty.getDisplayName());
        wordRoom.setEstimatedTimeMinutes(9);
        wordRoom.addPuzzle(wordPuzzle);
    
        SimplePuzzle logicPuzzle = chooseRandom(List.of(
            new SimplePuzzle(
                2211,
                "Three enchanted switches A, B, and C guard the final chamber. Exactly two of the following statements are true:\n" +
                    "A: 'Switch B will not open the door.'\n" +
                    "B: 'Switch C unlocks the door.'\n" +
                    "C: 'Switch A is lying.'\n" +
                    "Which switch actually opens the door? (Answer with A, B, or C)",
                "C",
                "Assume each switch opens the door in turn and test the statements.",
                "Remember that exactly two statements must be true at the same time.",
                "Only one assumption satisfies the requirement—identify which switch makes it work.",
                "Check consistency across all three statements for each assumed true switch."
            ),
            new SimplePuzzle(
                2212,
                "A trio of crystals shimmer. Crystal Red says 'Blue is lying.' Crystal Blue says 'Green is the key.' " +
                    "Crystal Green says 'Exactly one of us tells the truth.' Which crystal activates the gateway?",
                "blue",
                "Test each crystal as the key and count the truthful statements.",
                "Green's statement tells you how many truths there can be.",
                "Try assuming Blue is correct and see if the statements fit.",
                "Balance the truth counts until you find the scenario that matches the clue."
            ),
            new SimplePuzzle(
                2213,
                "Three spirit bells ring in succession. Bell One says 'Bell Two's claim is false.' Bell Two says 'Bell Three opens the vault.' " +
                    "Bell Three says 'Either Bell One or I am correct, but not both.' Which bell reveals the passage?",
                "bell three",
                "Translate Bell Three's clue into logic: exactly one of them is right.",
                "Check which bell being correct yields consistent truth values.",
                "Work through the possibilities and eliminate contradictions.",
                "The correct bell leaves the other statements false while keeping one true."
            ),
            new SimplePuzzle(
                2214,
                "Three time-locked safes stand before you. Safe Alpha states 'Safe Beta contains the key.' Safe Beta claims 'Safe Gamma is empty.' " +
                    "Safe Gamma whispers 'Alpha is telling the truth.' Exactly two statements are true. Which safe should you open?",
                "alpha",
                "If Alpha is right, what does that say about Gamma?",
                "Count carefully—two truths, one lie.",
                "Try each assumption and verify whether exactly two statements become true.",
                "The correct safe yields the required two-true, one-false pattern."
            ),
            new SimplePuzzle(
                2215,
                "Four guardians debate, but only one directs you correctly. Guardian North says 'South misleads you.' Guardian South says 'East hides the exit.' " +
                    "Guardian East says 'West lies and I tell the truth.' Guardian West says 'North is wrong.' Exactly one guardian tells the truth. Which direction should you follow?",
                "south",
                "Test each direction by assuming that guardian alone speaks truth.",
                "Only one scenario keeps the remaining three statements false.",
                "Simulate each guardian's claim and check the truth values of the others.",
                "The valid direction makes three other statements false and one true."
            )
        ));
    
        Room logicRoom = new Room();
        logicRoom.setRoomId("logic-vault-hard");
        logicRoom.setName("Hall of Guardians");
        logicRoom.setDescription("Eldritch logic stands between you and the final door.");
        logicRoom.setDifficulty(difficulty.getDisplayName());
        logicRoom.setEstimatedTimeMinutes(9);
        logicRoom.addPuzzle(logicPuzzle);
    
        rooms.add(mathRoom);
        rooms.add(wordRoom);
        rooms.add(logicRoom);
        return rooms;
    }
    

    public void logout() {
        saveCurrentPlayerProgress();
        if (currentPlayer != null) {
            currentPlayer.logout();
        }
        currentPlayer = null;
        progress = null;
        currentRoom = null;
        activePuzzle = null;
        availableRooms.clear();
        currentRoomIndex = -1;
        puzzleStartTime = null;
    }

    public Player createAccount(String userName, String password) {
        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        String trimmedUsername = userName.trim();
        if (playerManager.getPlayerByUsername(trimmedUsername) != null) {
            return null;
        }

        Player newPlayer = new Player(trimmedUsername, null, password);
        boolean added = playerManager.addPlayer(newPlayer);
        if (!added) {
            return null;
        }

        DataWriter.saveUsers(playerManager.getAllPlayers(), userDataPath);
        return newPlayer;

    }

    public void deleteAccount(int playerId) {
    }

    public void startNewGame() {
    }

    public void continueGame() {
    }

    public String showInstructions() {
        return null;
    }

    public void updateSettings(Settings settings) {
    }

    public Settings getSettings() {
        return settings;
    }

    public void toggleSound(boolean on) {
    }

    public void setLanguage(String langCode) {
    }

    public void setDifficulty(String level) {

        setSelectedDifficulty(Settings.Difficulty.fromName(level));
    }

    public Settings.Difficulty getSelectedDifficulty() {
        if (settings == null) {
            settings = new Settings();
        }
        Settings.Difficulty difficulty = settings.getDifficulty();
        return difficulty == null ? Settings.Difficulty.EASY : difficulty;
    }

    public void setSelectedDifficulty(Settings.Difficulty difficulty) {
        if (settings == null) {
            settings = new Settings();
        }
        Settings.Difficulty resolved = difficulty == null ? Settings.Difficulty.EASY : difficulty;
        if (resolved == settings.getDifficulty()) {
            return;
        }
        settings.setDifficulty(resolved);
        if (currentPlayer != null) {
            buildRoomsFor(currentPlayer);
        }

    }

    public void enterRoom(int roomIndex) {
        if (roomIndex < 0 || roomIndex >= availableRooms.size()) {
            return;
        }
        currentRoomIndex = roomIndex;
        currentRoom = availableRooms.get(roomIndex);
        activePuzzle = currentRoom.getPuzzles().isEmpty() ? null : currentRoom.getPuzzles().get(0);
        puzzleStartTime = null;
        resetFreezeState();
    }

    public Room getCurrentRoom() {
        if (currentPlayer == null) {
            return null;
        }
        if (currentRoom == null || availableRooms.isEmpty()) {
            currentRoom = summarisePlayerRoom(currentPlayer);
        }
        return currentRoom;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Puzzle getActivePuzzle() {
        if (activePuzzle == null) {
            Room room = getCurrentRoom();
            if (room != null) {
                activePuzzle = room.getPuzzles().isEmpty() ? null : room.getPuzzles().get(0);
            }
        }
        ensureActivePuzzleTimerStarted();
        return activePuzzle;
    }

    public void ensureActivePuzzleTimerStarted() {
        if (activePuzzle != null && puzzleStartTime == null && !"SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            puzzleStartTime = Instant.now();
        }
    }

    public void restartActivePuzzleTimer() {
        resetFreezeState();
        if (activePuzzle == null) {
            puzzleStartTime = null;
            return;
        }
        if ("SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            puzzleStartTime = null;
        } else {
            puzzleStartTime = Instant.now();
        }
    }

    public long getActivePuzzleElapsedSeconds() {
        if (puzzleStartTime == null) {
            return 0L;
        }
        Instant now = Instant.now();
        long rawElapsed = Math.max(0L, Duration.between(puzzleStartTime, now).getSeconds());
        if (freezeTimerActive && freezeEndTime != null) {
            if (now.isBefore(freezeEndTime)) {
                return Math.max(0L, freezeStartElapsedSeconds);
            }
            freezeTimerActive = false;
            freezeEndTime = null;
        }
        long adjusted = rawElapsed - freezeCompensationSeconds;
        return Math.max(0L, adjusted);    }

    public long getLastCompletionSeconds() {
        return Math.max(0L, lastCompletionSeconds);
    }
    


    public String describeCurrentPuzzleStatus() {
        Puzzle puzzle = getActivePuzzle();
        if (puzzle == null) {
            return "Difficulty: " + getSelectedDifficulty().getDisplayName() + ". No puzzle loaded.";
        }
        String status = puzzle.getStatus();
        String prefix = "Difficulty: " + getSelectedDifficulty().getDisplayName() + ". ";

        if ("SOLVED".equalsIgnoreCase(status)) {
            return prefix + "You cracked the current puzzle!";
        }
        if ("ATTEMPTED".equalsIgnoreCase(status)) {
            return prefix + "The keypad is still locked. Try another code.";
        }
        return prefix + "A puzzle is waiting for you.";
    }

    public List<Room> listAvailableRooms() {
        if (currentPlayer == null) {
            return Collections.emptyList();
        }
        if (availableRooms.isEmpty()) {
            summarisePlayerRoom(currentPlayer);
        }
        return Collections.unmodifiableList(new ArrayList<>(availableRooms));
    }

    public Puzzle getPuzzle(int puzzleId) {
        Room room = getCurrentRoom();
        if (room == null) {
            return null;
        }
        return room.getPuzzleById(puzzleId);
    }

    public boolean moveToNextRoom() {
        if (!hasNextRoom()) {
            return false;
        }
        enterRoom(currentRoomIndex + 1);
        return activePuzzle != null;
    }

    public boolean hasNextRoom() {
        return currentRoomIndex >= 0 && currentRoomIndex + 1 < availableRooms.size();
    }

    public boolean isNextRoomFinal() {
        return hasNextRoom() && currentRoomIndex + 1 == availableRooms.size() - 1;
    }

    public String getCurrentRoomName() {
        Room room = getCurrentRoom();
        if (room == null || room.getName() == null || room.getName().isBlank()) {
            return "Mystery Room";
        }
        return room.getName();
    }

    public void resetProgressToFirstRoom() {
        if (currentPlayer == null) {
            return;
        }
        if (availableRooms.isEmpty()) {
            buildRoomsFor(currentPlayer);
        }
        for (Room room : availableRooms) {
            for (Puzzle puzzle : room.getPuzzles()) {
                puzzle.resetPuzzle();
            }
        }
        enterRoom(0);
        puzzleStartTime = null;
        resetFreezeState();
        consecutiveHintFreeSolves = 0;
    }
    public boolean submitPuzzleAnswer(int puzzleId, String answer) {
        Puzzle puzzle = getPuzzle(puzzleId);
        if (puzzle == null) {
            return false;
        }
        String previousStatus = puzzle.getStatus();
        boolean solved = puzzle.trySolve(answer);
        if (solved && (previousStatus == null || !"SOLVED".equalsIgnoreCase(previousStatus))) {
            boolean newlySolved = false;
            if (currentPlayer != null) {
                newlySolved = currentPlayer.recordPuzzleCompletion(puzzle, answer);
                if (newlySolved) {
                    currentPlayer.awardBonusPoints(100);
                    if (puzzle != null && puzzle.getHintsUsed() == 0) {
                        currentPlayer.addFreeHintToken();
                        consecutiveHintFreeSolves++;
                        if (consecutiveHintFreeSolves >= 2) {
                            currentPlayer.addFreezeTimerCharge();
                            consecutiveHintFreeSolves = 0;
                        }
                    } else {
                        consecutiveHintFreeSolves = 0;
                    }
                }
            }
            long completionSeconds = getActivePuzzleElapsedSeconds();
            lastCompletionSeconds = Math.max(0L, completionSeconds);
            Score score = currentPlayer != null ? currentPlayer.getScoreDetails() : null;
            if (score != null) {
                int seconds = (int) Math.min(Integer.MAX_VALUE, Math.max(0L, lastCompletionSeconds));
                score.setTimeTaken(seconds);
            }
            puzzleStartTime = null;
            resetFreezeState();
        }
        if (solved && (puzzle == null || puzzle.getHintsUsed() != 0)) {
            consecutiveHintFreeSolves = 0;
        }
        return solved;    }

    public List<Clue> getCluesForPuzzle(int puzzleId) {
        return null;
    }

    public boolean useItem(int itemId, int targetId) {
        return false;
    }

    public List<Player> getUserList() {
        return playerManager.getAllPlayers();
    }

    public void saveCurrentPlayerProgress() {
        
        if (currentPlayer == null) {
            return;
        }
        currentPlayer.saveProgress();
        Score score = currentPlayer.getScoreDetails();
        if (score != null) {
            score.setHintsUsed(currentPlayer.getTotalHintsUsedFromHistory());
            score.setPuzzlesSolved(Math.max(score.getPuzzlesSolved(), currentPlayer.getSolvedPuzzleCountFromHistory()));
        }
        DataWriter.saveUsers(playerManager.getAllPlayers(), userDataPath);
    }

    public String requestHint(int puzzleId) {
        Puzzle puzzle = getPuzzle(puzzleId);
        if (puzzle == null) {
            return "No puzzle loaded.";
        }
        String hint = puzzle.requestHint();
        if (currentPlayer != null) {
            currentPlayer.recordHintUsed(puzzle, hint);  
        }
        return hint;
    }

    public boolean hasFreeHintToken() {
        return currentPlayer != null && currentPlayer.hasFreeHintTokens();
    }

    public int getFreeHintTokenCount() {
        return currentPlayer == null ? 0 : currentPlayer.getFreeHintTokenCount();
    }

    public HintRequestResult useFreeHintToken(int puzzleId) {
        if (currentPlayer == null) {
            return new HintRequestResult(false, "No player logged in.", false);
        }
        if (!currentPlayer.consumeFreeHintToken()) {
            return new HintRequestResult(false, "No extra hint tokens available.", false);
        }
        Puzzle puzzle = getPuzzle(puzzleId);
        if (puzzle == null) {
            currentPlayer.addFreeHintToken();
            return new HintRequestResult(false, "No puzzle loaded.", false);
        }
        String hint = puzzle.requestHint();
        if (hint == null || hint.isBlank()) {
            currentPlayer.addFreeHintToken();
            return new HintRequestResult(false, "No hints available.", false);
        }
        if (isHintUnavailableMessage(hint)) {
            currentPlayer.addFreeHintToken();
            return new HintRequestResult(false, hint, false);
        }
        puzzle.markLastHintFree();
        currentPlayer.recordHintUsed(puzzle, hint);
        return new HintRequestResult(true, hint, true);
    }

    public boolean hasFreezeTimerCharge() {
        return currentPlayer != null && currentPlayer.hasFreezeTimerCharges();
    }

    public int getFreezeTimerChargeCount() {
        return currentPlayer == null ? 0 : Math.max(0, currentPlayer.getFreezeTimerCharges());
    }

    public boolean isFreezeTimerActive() {
        if (!freezeTimerActive || freezeEndTime == null) {
            return false;
        }
        if (Instant.now().isBefore(freezeEndTime)) {
            return true;
        }
        freezeTimerActive = false;
        freezeEndTime = null;
        return false;
    }

    public boolean isOnFinalPuzzle() {
        if (activePuzzle == null || "SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            return false;
        }
        if (availableRooms.isEmpty()) {
            return false;
        }
        return !hasNextRoom() && currentRoomIndex >= 0 && currentRoomIndex < availableRooms.size();
    }

    public boolean canUseFreezeTimerItem() {
        if (!isOnFinalPuzzle()) {
            return false;
        }
        if (currentPlayer == null || !currentPlayer.hasFreezeTimerCharges()) {
            return false;
        }
        if (isFreezeTimerActive()) {
            return false;
        }
        ensureActivePuzzleTimerStarted();
        return puzzleStartTime != null;
    }

    public boolean activateFreezeTimer() {
        if (!canUseFreezeTimerItem()) {
            return false;
        }
        ensureActivePuzzleTimerStarted();
        if (puzzleStartTime == null) {
            return false;
        }
        if (!currentPlayer.consumeFreezeTimerCharge()) {
            return false;
        }
        Instant now = Instant.now();
        long rawElapsed = Math.max(0L, Duration.between(puzzleStartTime, now).getSeconds());
        long currentElapsed = Math.max(0L, rawElapsed - freezeCompensationSeconds);
        freezeStartElapsedSeconds = currentElapsed;
        freezeTimerActive = true;
        freezeEndTime = now.plusSeconds(FREEZE_TIMER_DURATION_SECONDS);
        freezeCompensationSeconds += FREEZE_TIMER_DURATION_SECONDS;
        return true;
    }

    private boolean isHintUnavailableMessage(String hintMessage) {
        if (hintMessage == null) {
            return true;
        }
        String normalized = hintMessage.trim();
        return normalized.equalsIgnoreCase("No hints available.")
                || normalized.equalsIgnoreCase("All hints have been used.");    }

    public PlayerProgressReport getCurrentPlayerProgressReport() {
        if (currentPlayer == null) {
            return PlayerProgressReport.empty();
        }
        List<PuzzleProgressSnapshot> snapshots = currentPlayer.getPuzzleProgressSnapshots();
        int totalPuzzles = 0;
        for (Room room : availableRooms) {
            if (room == null) {
                continue;
            }
            totalPuzzles += room.getPuzzles().size();
        }
        if (totalPuzzles == 0 && !snapshots.isEmpty()) {
            totalPuzzles = snapshots.size();
        }
        int solved = 0;
        for (PuzzleProgressSnapshot snapshot : snapshots) {
            if (snapshots != null && snapshot.isSolved()) {
                solved++;
            }
        }
        int percent = totalPuzzles == 0 ? 0 : (int) Math.min(100,
                Math.round((solved * 100.0) / Math.max(1, totalPuzzles)));
        return new PlayerProgressReport(percent, solved, totalPuzzles, snapshots);
    }

    public String readUserDataFileContents() {
        Path path = Paths.get(userDataPath);
        try {
            if (!Files.exists(path)) {
                return "Save file not found at " + path.toAbsolutePath();
            }
            return Files.readString(path);
        } catch (IOException e) {
            return  "Unable to read save file: " + e.getMessage();
        }
    }

    public String getUserDataPath() {
        return userDataPath;
    }

    public void startEscapeRoom() {
        if (currentPlayer == null) {
            availableRooms.clear();
            currentRoom = null;
            activePuzzle = null;
            currentRoomIndex = -1;
            puzzleStartTime = null;
            lastCompletionSeconds = 0L;
            resetFreezeState();
            consecutiveHintFreeSolves = 0;
            return;
        }

        buildRoomsFor(currentPlayer);

    }
    private void resetFreezeState() {
        freezeTimerActive = false;
        freezeEndTime = null;
        freezeStartElapsedSeconds = 0L;
        freezeCompensationSeconds = 0L;
    }
}
