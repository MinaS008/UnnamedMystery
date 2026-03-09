import java.util.*;

public class Clue {
  private static Map<String, Clue> registry = new LinkedHashMap<>();
  static {}

  private static void register(String clueID, String description, NextStatementIsALie.characterNames implicateCharacter, int suspicionLevel) {
    registry.put(clueID, new Clue(clueID, description, implicatedCharacter, suspicionLevel));
  }

  private String clueID;
  private String description;
  private NextStatementIsALie.characterNames implicatedCharacter;
  private int suspicionLevel;

  private Clue(String clueID, String description, NextStatementIsALie.characterNames implicateCharacter, int suspicionLevel) {
    this.clueID = clueID;
    this.description = description;
    this.implicatedCharacter = implicatedCharacter;
    this.suspicionLevel = suspicionLevel;
  }

  public static Clue getByClueID(String clueID) {
    return registry.get(clueID);
  }

  public boolean implicates(NextStatementIsALie.characterNames character) {
    return implicatedCharacter == character;
  }

  public int getSuspicionLevel(NextStatementIsALie.characterNames character) {
    return implicates(character) ? suspicionLevel : 0;
  }

  public String getClueID() {
    return clueID;
  }

  public String getDescription() {
    return description;
  }

  public NextStatementIsALie.characterNames getImplicatedCharacter() {
    return implicatedCharacter;
  }

  public int getSuspicionLevel() {
    return suspicionLevel;
  }

  @Override
  public String toString() {
    return "[Clue: " + clueID + "] implicates=" + implicatedCharacter + " +" + suspicionLevel; 
  }
}
