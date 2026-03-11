import java.util.*;

public class NextStatementIsALie {
    //Enums
    public enum characterNames {
        mother, father, olderSister, littleBrother, uncle, cousin, familyFriend
    }

    public enum playableCharacter {
        olderSister, familyFriend
    }

    public enum gameState {
        notStarted, characterSelect, exploring, finalGathering, gameOverWin, gameOverLose
    }

    public enum endingType {
        correctGuessEscape, correctGuessTooLate, wrongGuess, escapedUnsolved, everyoneDead
    }

    //Constants
    private static final int maxSuspicion = 10;
    private static final int maxDanger = 10;
    private static final int dangerAmbushThreshold = 8;
    private static final int minCluesToAccuse = 2;

    private gameState gameState;
    private endingType endingType;
    private Character killer;
    private boolean killerLocked = false;
    private playableCharacter playableCharacter;
    private Map<characterNames, Character> characters;
    private Scene currentScene;
    private Map<String, Scene> sceneRegistry;
    private List<String> inventory;

    private int dangerLevel;
    private boolean finalGatheringTriggered;
    private boolean ambushTriggered = false;
    private Set<characterNames> deadCharacters;
    private List<gameListener> listeners;

    //Constructor
    public NextStatementIsALie(Map<String, Scene> sceneRegistry) {
        this.sceneRegistry = Collections.unmodifiableMap(sceneRegistry);
        this.characters = buildCharacters();
        this.inventory = new ArrayList<>();
        this.deadCharacters = new HashSet<>();
        this.listeners = new ArrayList<>();
        this.dangerLevel = 0;
        this.finalGatheringTriggered = false;
        this.gameState = gameState.notStarted;

        killer.setKiller(true);
    }

    private Map<characterNames, Character> buildCharacters() {
        Map<characterNames, Character> map = new LinkedHashMap<>();
        map.put(characterNames.mother, new Character(characterNames.mother, "Mother", 0));
        map.put(characterNames.father, new Character(characterNames.father, "Father", 0));
        map.put(characterNames.olderSister, new Character(characterNames.olderSister, "Older sister", 0));
        map.put(characterNames.littleBrother, new Character(characterNames.littleBrother, "Little brother", 0));
        map.put(characterNames.uncle, new Character(characterNames.uncle, "Uncle", 0));
        map.put(characterNames.cousin, new Character(characterNames.cousin, "Cousin", 0));
        map.put(characterNames.familyFriend, new Character(characterNames.familyFriend, "Family Friend", 0));
        return map;
    }

    //Randomize Killer
    private Character randomizeKiller(playableCharacter chosenCharacter) {
        List<characterNames> possibleKillers = new ArrayList<>(Arrays.asList(
                characterNames.mother,
                characterNames.father,
                characterNames.olderSister,
                characterNames.uncle,
                characterNames.cousin,
                characterNames.familyFriend
        ));

        //Remove player's character
        possibleKillers.remove(toCharacterName(chosenCharacter));

        Collections.shuffle(possibleKillers);
        return characters.get(possibleKillers.get(0));
    }

    private characterNames toCharacterName(playableCharacter pc) {
        switch (pc) {
            case olderSister:
                return characterNames.olderSister;
            case familyFriend:
                return characterNames.familyFriend;
            default:
                throw new IllegalArgumentException("Unknown playable character: " + pc);
        }
    }

    public void startGame() {
        gameState = gameState.characterSelect;
        loadScene("OpeningGathering");
        notifyListeners(gameEvent.gameStarted);
    }

    public void selectCharacter(playableCharacter chosen) {
        this.playableCharacter = chosen;
        this.killer = randomizeKiller(chosen);
        this.killer.setKiller(true);
        this.ambushTriggered = false;
        gameState = gameState.exploring;
        loadScene("Opening Scene");
        notifyListeners(gameEvent.characterSelected);
    }

    public void loadScene(String sceneID) {
        Scene scene = sceneRegistry.get(sceneID);
        if (scene == null) {
            throw new IllegalArgumentException("Unknown scene ID: " + sceneID);
        }
        currentScene = scene;

        //apply automatic effects
        applySceneEffects(scene);
        checkDangerLevel();
        checkFinalGatheringTrigger();

        notifyListeners(gameEvent.sceneChanged);
    }

    public void makeChoice(Choice choice) {
        if (!isChoiceAvailable(choice)) {
            notifyListeners(gameEvent.choiceUnavailable);
            return;
        }

        loadScene(choice.getTargetSceneId());
    }

    //Scene effects
    public void applySceneEffects(Scene scene) {
        //This is for adding to the inventory
        for (String itemID : scene.getInventoryAdds()) {
            addToInventory(itemID);
        }

        //This is to add to the suspicion meter
        for (Map.Entry<characterNames, Integer> entry : scene.getSuspicionChanges().entrySet()) {
            adjustSuspicion(entry.getKey(), entry.getValue());
        }

        //This is to apply killer-conditional changes
        for (Clue.KillerConditionalEffect effect : scene.getKillerConditionalEffects()) {
            if (killer.getName() == effect.getKillerName()) {
                adjustSuspicion(effect.getTargetCharacter(), effect.getSuspicionLevel());
                dangerLevel = Math.min(maxDanger, dangerLevel + effect.getDangerLevel());
            }
        }

        //Apply danger level changes
        dangerLevel = Math.min(maxDanger, Math.max(0, dangerLevel + scene.getDangerLevel()));

        //Kill characters if the scene specifies it
        for (characterNames victim : scene.getCharacterDeaths()) {
            killCharacter(victim);
        }
    }

    //Suspicion System
    public void adjustSuspicion(characterNames name, int num) {
        Character c = characters.get(name);
        if (c == null) return;
        if (num > 0) {
            int current = c.getSuspicionLevel();
            int capped = Math.min(num, maxSuspicion - current);
            if (capped > 0) {
                c.increaseSuspicionLevel(capped);
            }
        } else if (num < 0) {
            c.decreaseSuspicionLevel(-num);
        }
        notifyListeners(gameEvent.suspicionChanged);
    }

    public int getSuspicion(characterNames name) {
        Character c = characters.get(name);
        return (c != null) ? c.getSuspicionLevel() : 0;
    }

    public Map<characterNames, Integer> getAllSuspicionScores() {
        Map<characterNames, Integer> scores = new LinkedHashMap<>();
        for (Map.Entry<characterNames, Character> entry : characters.entrySet()) {
            scores.put(entry.getKey(), entry.getValue().getSuspicionLevel());
        }
        return Collections.unmodifiableMap(scores);
    }

    //Inventory System
    public void addToInventory(String itemID) {
        if (!inventory.contains(itemID)) {
            inventory.add(itemID);
            notifyListeners(gameEvent.inventoryChanged);
        }
    }

    public boolean hasItem(String itemID) {
        return inventory.contains(itemID);
    }

    public List<String> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    //Returns how many clues actually implicate the killer
    public int getIncriminatingClueCount() {
        characterNames killerKey = null;
        for (Map.Entry<characterNames, Character> entry : characters.entrySet()) {
            if (entry.getValue() == killer) {
                killerKey = entry.getKey();
                break;
            }
        }
        if (killerKey == null) return 0;
        int count = 0;
        for (String itemID : inventory) {
            Clue clue = Clue.getByClueID(itemID);
            if (clue != null && clue.implicates(killerKey)) {
                count++;
            }
        }
        return count;
    }

    //Guess - Accusation System
    public void processGuess(characterNames accused) {
        boolean correct = (characters.get(accused) == killer);

        if (correct) {
            handleCorrectGuess();
        } else {
            handleIncorrectGuess();
        }
    }

    private void handleCorrectGuess() {
        if (dangerLevel >= dangerAmbushThreshold) {
            triggerEnding(endingType.correctGuessTooLate);
        } else {
            triggerEnding(endingType.correctGuessEscape);
        }
    }

    private void handleIncorrectGuess() {
        triggerEnding(endingType.wrongGuess);
    }

    public void escapeWithoutSolving() {
        triggerEnding(endingType.escapedUnsolved);
    }

    //Death System
    public void killCharacter(characterNames name) {
        deadCharacters.add(name);
        Character c = characters.get(name);
        if (c != null) {
            c.setAlive(false);
        }
        notifyListeners(gameEvent.characterDied);
        checkEveryoneDead();
    }

    public boolean isAlive(characterNames name) {
        return !deadCharacters.contains(name);
    }

    public List<characterNames> getAliveCharacters() {
        List<characterNames> alive = new ArrayList<>();
        for (characterNames name : characters.keySet()) {
            if (!deadCharacters.contains(name)) {
                alive.add(name);
            }
        }
        return Collections.unmodifiableList(alive);
    }

    private void checkEveryoneDead() {
        if (deadCharacters.size() >= characters.size() - 1) {
            triggerEnding(endingType.everyoneDead);
        }
    }

    //Danger level
    public void increaseDanger(int amount) {
        dangerLevel = Math.min(maxDanger, dangerLevel + amount);
        checkDangerLevel();
        notifyListeners(gameEvent.dangerChanged);
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    //Check if danger has reached threshold
    private void checkDangerLevel() {
        if (!ambushTriggered && dangerLevel >= dangerAmbushThreshold && gameState == gameState.exploring) {
            ambushTriggered = true;
            loadScene("Ambush " + killer.getName().toString());
        }
    }

    //Final Gathering
    private void checkFinalGatheringTrigger() {
        if (!finalGatheringTriggered && shouldTriggerFinalGathering()) {
            finalGatheringTriggered = true;
            gameState = gameState.finalGathering;
            loadScene("Final Gathering");
        }
    }

    private boolean shouldTriggerFinalGathering() {
        if (dangerLevel >= maxDanger) {
            return true;
        }
        if (deadCharacters.size() >= 2) {
            return true;
        }
        return false;
    }

    public void triggerFinalGatheringManually() {
        finalGatheringTriggered = true;
        gameState = gameState.finalGathering;
        loadScene("Final Gathering");
    }

    //Ending
    private void triggerEnding(endingType type) {
        this.endingType = type;
        if (type == endingType.wrongGuess
                || type == endingType.correctGuessTooLate
                || type == endingType.everyoneDead) {
            this.gameState = gameState.gameOverLose;
        } else {
            this.gameState = gameState.gameOverWin;
        }
        notifyListeners(gameEvent.gameEnded);
    }

    private boolean isChoiceAvailable(Choice choice) {
        if (choice.getRequiredItem() != null && !hasItem(choice.getRequiredItem())) {
            return false;
        }
        if (choice.getRequiredCharacter() != null && choice.getRequiredCharacter() != toCharacterName(playableCharacter)) {
            return false;
        }

        if (choice.getRequiredAliveCharacter() != null && !isAlive(choice.getRequiredAliveCharacter())) {
            return false;
        }

        if (choice.getMinSuspicionTarget() != null) {
            if (getSuspicion(choice.getMinSuspicionTarget()) < choice.getMinSuspicionValue()) {
                return false;
            }
        }
        return true;
    }

    public List<Choice> getAvailableChoices() {
        List<Choice> available = new ArrayList<>();
        if (currentScene == null) return available;
        for (Choice c : currentScene.getChoices()) {
            if (isChoiceAvailable(c)) {
                available.add(c);
            }
        }
        return Collections.unmodifiableList(available);
    }

    //GUI Observers
    public void addListener(gameListener listener) {
        listeners.add(listener);
    }

    public void removeListener(gameListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(gameEvent event) {
        for (gameListener l : listeners) {
            l.onGameEvent(event, this);
        }
    }

    //Read-only getters for GUI
    public gameState getGameState() {
        return gameState;
    }

    public endingType getEndingType() {
        return endingType;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public playableCharacter getPlayableCharacter() {
        return playableCharacter;
    }

    public boolean isFinalGatheringTriggered() {
        return finalGatheringTriggered;
    }

    public String getKillerReveal() {
        if (gameState == gameState.gameOverWin || gameState == gameState.gameOverLose) {
            return killer.getDisplayName();
        }
        throw new IllegalArgumentException("Killer identity not available until the game ends.");
    }

    public String getCharacterDisplayName(characterNames name) {
        Character c = characters.get(name);
        return (c != null) ? c.getDisplayName() : name.toString();
    }

    public int getMinCluesRequired() {
        return minCluesToAccuse;
    }

    //Inner interfaces
    public enum gameEvent {
        gameStarted, characterSelected, sceneChanged, inventoryChanged, suspicionChanged, dangerChanged, characterDied, choiceUnavailable, gameEnded
    }

    public interface gameListener {
        void onGameEvent(gameEvent event, NextStatementIsALie game);
    }


    public static void main(String[] args) {
        Map<String, Scene> registry = new LinkedHashMap<>();

        // Opening
        registry.put("OpeningGathering", Scene.openingGathering);

        // Family Friend scenes
        registry.put("OpenSceneForFriend", Scene.openSceneFriend);
        registry.put("GoToStudy", Scene.goToStudy);
        registry.put("CheckTheWill", Scene.checkTheWill);
        registry.put("DefendYourselfOldSister", Scene.defendYourselfOldSister);
        registry.put("NoActionOldSister", Scene.noActionOldSister);
        registry.put("CheckFrame", Scene.checkFrame);
        registry.put("AccuseCousin", Scene.accuseCousin);
        registry.put("AskCousin", Scene.askCousin);
        registry.put("CheckLaptop", Scene.checkLaptop);
        registry.put("CheckLoginHistory", Scene.checkLoginHistory);
        registry.put("AccuseFromLaptop", Scene.accuseFromLaptop);
        registry.put("GoToAttic", Scene.goToAttic);
        registry.put("CheckLockedChest", Scene.checkLockedChest);
        registry.put("ReadConfession", Scene.readConfession);
        registry.put("SuspectUncleFramed", Scene.suspectUncleFramed);
        registry.put("SuspectFatherFaked", Scene.suspectFatherFaked);
        registry.put("SuspectMotherConfession", Scene.suspectMotherConfession);
        registry.put("InspectVial", Scene.inspectVial);
        registry.put("SuspectCousinVial", Scene.suspectCousinVial);
        registry.put("SuspectSelf", Scene.suspectSelf);
        registry.put("SuspectOlderSisterVial", Scene.suspectOlderSisterVial);
        registry.put("ExamineCloak", Scene.examineCloak);
        registry.put("SuspectMotherCloak", Scene.suspectMotherCloak);
        registry.put("SuspectUncle", Scene.suspectUncleCloak);
        registry.put("CheckTrunk", Scene.checkTrunk);
        registry.put("InspectPhotos", Scene.inspectPhotos);
        registry.put("ArguePhoto", Scene.arguePhoto);
        registry.put("LeaveTheTrunk", Scene.leaveTheTrunk);
        registry.put("CheckLetters", Scene.checkLetters);
        registry.put("ReadAllLetters", Scene.readAllLetters);
        registry.put("TakeThreatLetter", Scene.takeThreatLetter);
        registry.put("LeaveLetters", Scene.leaveLetters);
        registry.put("GoToBathroom", Scene.goToBathroom);
        registry.put("ExaminePillBottle", Scene.examinePillBottle);
        registry.put("HideBottle", Scene.hideBottle);
        registry.put("LeaveBottle", Scene.leaveBottle);
        registry.put("CatchHallwayPerson", Scene.catchHallwayPerson);
        registry.put("ConfrontHallwayPerson", Scene.confrontHallwayPerson);
        registry.put("EscapeHallway", Scene.escapeHallway);
        registry.put("StayHallway", Scene.stayHallway);
        registry.put("RunFromHallway", Scene.runFromHallway);
        registry.put("ExamineTowel", Scene.examineTowel);
        registry.put("TakeTowel", Scene.takeTowel);
        registry.put("IgnoreHallwayPerson", Scene.ignoreHallwayPerson);
        registry.put("SmellTowel", Scene.smellTowel);
        registry.put("OpenDoorSuddenly", Scene.openDoorSuddenly);
        registry.put("ListenAtDoor", Scene.listenAtDoor);
        registry.put("ExamineStain", Scene.examineStain);
        registry.put("TouchResidue", Scene.touchResidue);
        registry.put("KeepCufflink", Scene.keepCufflink);
        registry.put("LeaveCufflink", Scene.leaveCufflink);
        registry.put("FollowScrapeMarks", Scene.followScrapeMarks);
        registry.put("HideInCloset", Scene.hideInCloset);
        registry.put("StandGround", Scene.standGround);

        // Older Sister scenes
        registry.put("OpenSceneForSister", Scene.openSceneSister);
        registry.put("GoToPantry", Scene.goToPantry);
        registry.put("InvestigateArguing", Scene.investigateArguing);
        registry.put("RecordParents", Scene.recordParents);
        registry.put("ConfrontParents", Scene.confrontParents);
        registry.put("RunFromArguing", Scene.runFromArguing);
        registry.put("SisterExitOption", Scene.sisterExitOption);
        registry.put("GoToNursery", Scene.goToNursery);
        registry.put("CheckCrib", Scene.checkCrib);
        registry.put("OpenWardrobeWithPresence", Scene.openWardrobeWithPresence);
        registry.put("CheckWardrobe", Scene.checkWardrobe);
        registry.put("HideVialFromCousin", Scene.hideVialFromCousin);
        registry.put("CheckNurseryPhotos", Scene.checkNurseryPhotos);
        registry.put("GoToCellar", Scene.goToCellar);
        registry.put("ExamineCellarRack", Scene.examineCellarRack);
        registry.put("BangCellarDoor", Scene.bangCellarDoor);
        registry.put("AccuseFatherCellar", Scene.accuseFatherDirectly);
        registry.put("LieFatherCellar", Scene.lieFatherCellar);
        registry.put("FindCellarPassage", Scene.findCellarPassage);
        registry.put("RevealCellarPassage", Scene.revealCellarPassage);
        registry.put("RecordCellarPassage", Scene.recordCellarPassage);
        registry.put("FollowCellarFootprint", Scene.followCellarFootprint);
        registry.put("HideInTunnel", Scene.hideInTunnel);
        registry.put("WaitInCellar", Scene.waitInCellar);
        registry.put("TrustCousin", Scene.trustCousin);
        registry.put("InspectBrokenGlass", Scene.inspectBrokenGlass);
        registry.put("CellarDarkTurnAround", Scene.cellarDarkTurnAround);
        registry.put("CellarDarkRun", Scene.cellarDarkRun);
        registry.put("CellarDarkStill", Scene.cellarDarkStill);
        registry.put("GoToStudySister", Scene.goToStudySister);
        registry.put("ReadWillSister", Scene.readWillSister);
        registry.put("ConfrontMotherWill", Scene.confrontMotherWill);
        registry.put("HideWillFromMother", Scene.hideWillFromMother);
        registry.put("ForceDrawer", Scene.forceDrawer);
        registry.put("GoUpstairsGlass", Scene.goUpstairsGlass);
        registry.put("TakeRevolver", Scene.takeRevolver);
        registry.put("CallOutFromStudy", Scene.callOutFromStudy);
        registry.put("HideWillSister", Scene.hideWillSister);
        registry.put("GoToFireplace", Scene.goToFireplace);
        registry.put("PieceDocument", Scene.pieceDocument);
        registry.put("SearchChimney", Scene.searchChimney);
        registry.put("StayInKitchen", Scene.stayInKitchen);

        // Final Gathering and endings
        registry.put("Final Gathering", Scene.finalGathering);
        registry.put("AccuseOutcomeMother", Scene.accuseOutcomeMother);
        registry.put("AccuseOutcomeFather", Scene.accuseOutcomeFather);
        registry.put("AccuseOutcomeOlderSister", Scene.accuseOutcomeOlderSister);
        registry.put("AccuseOutcomeUncle", Scene.accuseOutcomeUncle);
        registry.put("AccuseOutcomeCousin", Scene.accuseOutcomeCousin);
        registry.put("AccuseOutcomeFamilyFriend", Scene.accuseOutcomeFamilyFriend);
        registry.put("EndingCorrectGuessEscape", Scene.endingCorrectGuessEscape);
        registry.put("EndingCorrectGuessTooLate", Scene.endingCorrectGuessTooLate);
        registry.put("EndingWrongGuess", Scene.endingWrongGuess);
        registry.put("EndingEscapedUnsolved", Scene.endingEscapedUnsolved);
        registry.put("EndingEveryoneDead", Scene.endingEveryoneDead);

        // Ambush scenes — one per possible killer
        registry.put("Ambush Mother", Scene.ambushMother);
        registry.put("Ambush Father", Scene.ambushFather);
        registry.put("Ambush Older sister", Scene.ambushOlderSister);
        registry.put("Ambush Uncle", Scene.ambushUncle);
        registry.put("Ambush Cousin", Scene.ambushCousin);
        registry.put("Ambush Family Friend", Scene.ambushFamilyFriend);

        NextStatementIsALie game = new NextStatementIsALie(registry);
        game.startGame();
    }
    }

