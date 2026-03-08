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
    private playableCharacter playableCharacter;
    private Map<characterNames, Character> characters;
    private Scene currentScene;
    private Map<String, Scene> sceneRegistry;
    private List<String> inventory;

    private int dangerLevel;
    private boolean finalGatheringTriggered;
    private Set<characterNames> deadCharacters;
    private List<GameListener> listeners;

    //Constructor
    public NextStatementIsALie(Map<String, Scene> sceneRegistry) {
        this.sceneRegistry = Collections.unmodifiableMap(sceneRegistry);
        this.characters = buildCharacters();
        this.killer = randomizeKiller();
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


    public void startGame() {
        gameState = gameState.characterSelect;
        notifyListeners(gameEvent.gameStarted);
    }

    public void selectCharacter(playableCharacter chosen) {
        this.playableCharacter = chosen;
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

        loadScene(choice.getTargetSceneID());
    }

    //Scene effects
    public void applySceneEffects(Scene scene) {
        //This is for adding to the inventory
        for (String itemID : scene.getInventroyAdds()) {
            addToInventory(itemID);
        }

        //This is to add to the suspicion meter
        for (Map.Entry<characterNames, Integer> entry : scene.getSuspicionChanges().entrySet()) {
            adjustSuspicion(entry.getKey(), entry.getValue());
        }

        //This is to apply killer-conditional changes
        for (Scene.getKillerConditionalEffect effect : scene.getKillerConditionalEffects()) {
            if (killer.getName() == effect.getKillerName()) {
                adjustSuspicion(effect.getTargetCharacter(), effect.getSuspicion());
                dangerLevel = Math.min(maxDanger, dangerLevel + effect.getDanger());
            }
        }

        //Apply danger level changes
        dangerLevel = Math.min(maxDanger, Math.max(0, dangerLevel + scene.getDanger()));

        //Kill characters if the scene specifies it
        for (characterNames victim : scene.getCharacterDeaths()) {
            killCharacter(victim);
        }
    }

    //Suspicion System
    public void adjustSuspicion(characterNames name, int num) {
        Character c = characters.get(name);
        if (c == null) return;
        int newValue = Math.min(maxSuspicion, Math.max(0, c.getSuspicion() + num));
        c.setSuspicion(newValue);
        notifyListeners(gameEvent.suspicionChanged);
    }

    public int getSuspicion(characterNames name) {
        Character c = characters.get(name);
        return (c != null) ? c.getSuspicion() : 0;
    }

    public Map<characterNames, Integer> getAllSuspicionScores() {
        Map<characterNames, Integer> scores = new LinkedHashMap<>();
        for (Map.Entry<characterNames, Character> entry : characterNames.entrySet()) {
            scores.put(entry.getKey(), entry.getValue().getSuspicion);
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
        return Collections.unmodifiableMap(inventory);
    }

    //Returns how many clues actually implicate the killer
    public int getIncriminatingClueCount() {
        int count = 0;
        for (String itemID : inventory) {
            Clue clue = Clue.getByClueID(itemID);
            if (clue != null && clue.implicates(killer.getName())) {
                count++;
            }
        }
        return count;
        ;
    }

    //Guess - Accusation System
    public void processGuess(characterNames accused) {
        boolean correct = (accused == killer.getName());

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
        for (Map.Entry<characterNames, Character> entry : characters.entrySet()) {
            if (entry.getValue().isAlive()) {
                alive.add(entry.getKey());
            }
        }
        return Collections.unmodifiableList(alive);
    }

    private void checkEveryoneDead() {
        long aliveCount = characters.values().stream().filter(Character::isAlive).count();
        if (aliveCount <= 1) {
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
        if (dangerLevel >= dangerAmbushThreshold && gameState == gameState.exploring) {
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
        this.gameState = type == endingType.wrongGuess || type == endingType.correctGuessTooLate ? gameState.gameOverLose ? gameState.gameOverWin;
        notifyListeners(gameEvent.gameEnded);
    }

    private boolean isChoiceAvailable(Choice choice) {
        if (choice.getRequiredItem() != null && !hasItem(choice.getRequiredItem())) {
            return false;
        }
        if (choice.getRequiredCharacter() != null && !choice.getRequiredCharacte.toString().equals(playableCharacter.toString())) {
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
            return killer.getName();
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
        void onGameEvent(gameEvent event, Game game);
    }




    public static void main(String[] args) {

    }
}
