import java.util.*;

public class NextStatementIsALie {
    //Enums
    public enum characterNames{
        mother, father, olderSister, littleBrother, uncle, cousin, familyFriend
    }

    public enum playableCharacter{
        olderSister, familyFriend
    }

    public enum gameState{
        notStarted, characterSelect, exploring, finalGathering, gameOverWin, gameOverLose
    }

    public enum endingType{
        correctGuessEscape,correctGuessTooLate, wrongGuess, escapedUnsolved, everyoneDead
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
    public NextStatementIsALie(Map<String, Scene> sceneRegistry){
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

    public static void main(String[] args) {


    }
}
