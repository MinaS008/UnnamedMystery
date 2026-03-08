public class Choice {
  private String text;
  private String targetSceneID;

  private String requiredItem;
  private NextStatementIsALie.characterNames requiredCharacter;
  private NextStatementIsALie.characterNames requiredAliveCharacter;
  private NextStatementIsALie.characterNames minSuspicionTarget;
  private int minSuspicionValue;

  public Choice (String text, String targetSceneID) {
    this.text = text;
    this.targetSceneID = targetSceneID;
    this.requiredItem = null;
    this.requiredCharacter = null;
    this.requiredAliveCharacter = null;
    this.minSuspicionTarget = null;
    this.minSuspicionValue = 0;
  }

  public Choice(String text, String targetSceneID, String requiredItem,
                  NextStatementIsALie.characterNames requiredCharacter,
                  NextStatementIsALie.characterNames requiredAliveCharacter,
                  NextStatementIsALie.characterNames minSuspicionTarget,
                  int minSuspicionValue) {
    this.text = text;
    this.targetSceneID = targetSceneID;
    this.requiredItem = requiredItem;
    this.requiredCharacter = requiredCharacter;
    this.requiredAliveCharacter = requiredAliveCharacter;
    this.minSuspicionTarget = minSuspicionTarget;
    this.minSuspicionValue = minSuspicionValue;
  }

  //Getters
  public String getText() {
    return text;
  }
  public String getTargetSceneId() {
    return targetSceneID;
  }
  public String getRequiredItem() {
    return requiredItem;
  }
  public NextStatementIsALie.characterNames getRequiredCharacter() { 
    return requiredCharacter; 
  }
  public NextStatementIsALie.characterNames getRequiredAliveCharacter() { 
    return requiredAliveCharacter; 
  }
  public NextStatementIsALie.characterNames getMinSuspicionTarget() {
    return minSuspicionTarget; 
  }
  public int getMinSuspicionValue() { 
    return minSuspicionValue; 
  }

  @Override
  public String toString() {
    return "> " + text + " -> [" + targetSceneID + "]";
  }
}
