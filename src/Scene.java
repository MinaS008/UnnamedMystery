import java.util.*;

public class Scene {
    private String sceneID;
    private String narrativeText;
    private List<Choice> choices;
    private List<String> inventoryAdds;

    private Map<NextStatementIsALie.characterNames, Integer> suspicionChanges;
    private List<KillerConditionalEffect> killerConditionalEffects; //Clue reference
    private int dangerLevel;

    private List<NextStatementIsALie.characterNames> characterDeaths;
    private boolean triggersFinalGathering;
    private boolean isExitScene;

    private Scene(Builder builder){
        this.sceneID = builder.sceneID;
        this.narrativeText = builder.narrativeText;
        this.choices = Collections.unmodifiableList(new ArrayList<>(builder.choices));
        this.inventoryAdds = Collections.unmodifiableList(new ArrayList<>(builder.inventoryAdds));
        this.suspicionChanges = Collections.unmodifiableMap(new LinkedHashMap<>(builder.suspicionChanges));
        this.killerConditionalEffects = Collections.unmodifiableList(new ArrayList<>(builder.killerConditionalEffects));
        this.dangerLevel = builder.dangerLevel;
        this.characterDeaths = Collections.unmodifiableList(new ArrayList<>(builder.characterDeaths));
        this.triggersFinalGathering = builder.triggersFinalGathering;
        this.isExitScene = builder.isExitScene;
    }

    public String getSceneID() {
        return sceneID;
    }
    public String getNarrativeText(){
        return narrativeText;
    }
    public List<Choice> getChoices(){
        return choices;
    }
    public List<String> getInventoryAdds(){
        return inventoryAdds;
    }
    public int getDangerLevel(){
        return dangerLevel;
    }
    public boolean getTriggersFinalGathering(){
        return triggersFinalGathering;
    }
    public boolean getIsExitScene(){
        return isExitScene;
    }

    public Map<NextStatementIsALie.characterNames, Integer> getSuspicionChanges(){
        return suspicionChanges;
    }
    public List<Clue.KillerConditionalEffect> getKillerConditionalEffects(){
        return killerConditionalEffects;
    }
    public List<NextStatementIsALie.characterNames> getCharacterDeaths(){
        return characterDeaths;
    }

    public static class killerConditionalEffect{
        private NextStatementIsALie.characterNames killerName;
        private NextStatementIsALie.characterNames targetCharacter;
        private int suspicionLevel;
        private int dangerLevel;

        public killerConditionalEffect(NextStatementIsALie.characterNames killerName, NextStatementIsALie.characterNames targetCharacter, int suspicionLevel, int dangerLevel){
            this.killerName = killerName;
            this.targetCharacter = targetCharacter;
            this.suspicionLevel = suspicionLevel;
            this.dangerLevel = dangerLevel;
        }

        public NextStatementIsALie.characterNames getKillerName(){return killerName;}
        public NextStatementIsALie.characterNames getTargetCharacter(){return targetCharacter;}
        public int getSuspicionLevel(){return suspicionLevel;}
        public int getDangerLevel(){return dangerLevel;}
    }

    public static class Builder{
        private String sceneID;
        private String narrativeText;
        private List<Choice> choices = new ArrayList<>();
        private List<String> inventoryAdds = new ArrayList<>();

        private Map<NextStatementIsALie.characterNames, Integer> suspicionChanges = new LinkedHashMap<>();
        private List<Clue.KillerConditionalEffect> killerConditionalEffects = new ArrayList<>(); //Clue reference
        private int dangerLevel = 0;

        private List<NextStatementIsALie.characterNames> characterDeaths = new ArrayList<>();
        private boolean triggersFinalGathering = false;
        private boolean isExitScene = false;

        public Builder(String sceneId, String narrativeText){
            this.sceneID = sceneID;
            this.narrativeText = narrativeText;
        }

        public Builder addChoice(Choice choice){
            choices.add(choice);
            return this;
        }

        public Builder addInventorySystem(String itemID){
            inventoryAdds.add(itemID);
            return this;
        }

        public Builder addSuspicionChange(NextStatementIsALie.characterNames character, int value){
            suspicionChanges.put(character, value);
            return this;
        }

        public Builder addKillerConditionalEffect(KillerConditionalEffect effect){
            killerConditionalEffects.add(effect);
            return this;
        }

        public int setDangerLevel(int danger){
            dangerLevel = danger;
            return dangerLevel;
        }

        public Builder addCharacterDeath(NextStatementIsALie.characterNames name){
            characterDeaths.add(name);
            return this;
        }

        public Builder setIsExitScene(boolean value){
            isExitScene = value;
            return this;
        }

        public Scene build(){
            if(sceneID == null || sceneID.isBlank()){
                throw new IllegalStateException("Scene must have a valid sceneID (non-empty).");
            }
            if (narrativeText == null || narrativeText.isBlank()){
                throw new IllegalStateException("Scene " + sceneID + " must have a narrative text.");
            }
            return new Scene(this);
        }
    }

    public String toString(){
        return "Scene[" + sceneID + "] choices = " + choices.size() + " items =" + inventoryAdds.size() + " danger= " + (dangerLevel >= 0 ? "+" : "");
    }
}
