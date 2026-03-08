public class Choice {
  private String text;
  private String targetSceneID;

  private String requiredItem;
  private NextStatementIsALie.characterNames requiredCharacter;
  private NextStatementIsALie.characterNames requiredAliveCharacter;
  private NextStatementIsALie.characterNames minSuspiciontarget;
  private int minSuspicionValue;

  private Choise(Builder b) {
    this.text = b.text;
    this.targetSceneID = b.targetSceneID;
    this.requiredItem = b.requiredItem;
    this.requiredCharacter = b.requiredCharacter;
    this.requiredAliveCharacter = b.requiredAliveCharacter;
    this.minSuspicionTarget = b.minSuspicionTarget;
    this.minSUspicionValue = b.minSuspicionValue;
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

  public static class Builder {
    private String text;
    private String targetSceneID;
    private String requiredItem = null;
    private NextStatementIsALie.characterNames requiredCharacter = null;
    private NextStatementIsALie.characterNames requiredAliveCharacter = null;
    private NextStatementIsALie.characterNames minSuspicionTarget = null;
    private int minSuspicionValue = 0;

  }

  public Builder(String text, String targetSceneID) {
    this.text = text;
    this.targetSceneID = targetSceneID;
  }

  public Builder requireItem(String itemID) {
    this.requiredItem = itemID;
    return this;
  }

  public Builder requireCharacter(NextStatementIsALie.characterNames character) {
            this.requiredCharacter = character;
            return this;
  }

  public Builder requireAlive(NextStatementIsALie.characterNames character) {
            this.requiredAliveCharacter = character;
            return this;
  }

  public Builder requireSuspicion(NextStatementIsALie.characterNames target, int minimum) {
            this.minSuspicionTarget = target;
            this.minSuspicionValue  = minimum;
            return this;
  }

  public Choice build() {
            return new Choice(this);
  }

}
