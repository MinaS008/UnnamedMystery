import java.util.*;

public class Scene {
    private String sceneID;
    private String narrativeText;
    private List<Choice> choices;
    private List<String> inventoryAdds;

    private Map<NextStatementIsALie.characterNames, Integer> suspicionChanges;
    private List<Clue.KillerConditionalEffect> killerConditionalEffects; //Clue reference
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

        public Builder addKillerConditionalEffect(Clue.KillerConditionalEffect effect){
            killerConditionalEffects.add(effect);
            return this;
        }

        public Builder setDangerLevel(int danger){
            dangerLevel = danger;
            return this;
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

Scene openSceneFriend = new Scene.Builder("OpenSceneForFriend",
            "You end up in the living room. \n" +
            "You look around and notice something strange. \n" +
            "On the coffee table, beneath the wine glasses, is a folded napkin with faint red staining. \n" +
            "You pick it up. Inside is a short handwritten note:\n" +
            "'He knew the truth. Now he doesn’t breathe.'\n" +
            "You remember that the older sister was setting up entrees on the coffee table after the dinner. \n" +
            "You decide to leave the room to explore.")
            .addChoice(new Choice("Go to the Study", "GoToStudy"))
            .addChoice(new Choice("Go to the Attic", "GoToAttic"))
            .addChoice(new Choice("Go to the Bathroom", "GoToBathroom"))
            .addInventorySystem("handwrittenNote")
            .addSuspicionChange(NextStatementIsALie.characterNames.olderSister, 1)
            .setDangerLevel(1)
            .build();
   Scene goToStudy = new Scene.Builder("GoToStudy",
           "You enter the family’s study.\n" +
                   "The lights flicker.\n" +
                   "You see a couple of things:\n" +
                   "- An open will on the desk.\n" +
                   "- A shattered photo frame. \n" +
                   "- A laptop still unlocked \n" +
                   "Which one would give you a clue?\n")
           .addChoice(new Choice("Check the Will", "CheckTheWill"))
           .addChoice(new Choice("Pick Up the Photo Frame", "CheckFrame"))
           .addChoice(new Choice("Check the Laptop", "CheckLaptop"))
           .setDangerLevel(1)
           .build();


    Scene checkTheWill = new Scene.Builder("CheckTheWill",
            "You get close to the desk, pick up the will, and read it. \n" +
                    "Turns out it was the grandfather’s will. \n" +
                    "You freeze. \n" +
                    "On the slightly yellow pages, there is a list of names. At its bottom lies your name. You were added into the will. You were left money.\n" +
                    "Why would the grandfather leave you money?\n" +
                    "Next to the will is a small note.\n" +
                    "“Inheritance looks good on you. \n" +
                    "Shame it comes with blood.”\n" +
                    "The door of the Study flies open. \n" +
                    "he older sister marches into the room. She seems angry at you about the will.")
            .addSuspicionChange(NextStatementIsALie.characterNames.olderSister, 2)
            .addChoice(new Choice("Defend Yourself", "DefendYourselfOldSister"))
            .addChoice(new Choice("Don't Do Anything", "NoActionOldSister"))
            .addInventorySystem("grandfathersWill")
            .addInventorySystem("noteNextToWill")
            .setDangerLevel(2)
            .build();

    Scene defendYourselfOldSister = new Scene.Builder("DefendYourselfOldSister",
            "You grab the nearest object to defend yourself.\n" +
                    "You hit her unconscious and run out of the room.\n\n" +
                    "She is down. The house is silent.\n" +
                    "You have acted fast - but do you have enough evidence to back it up?\n" +
                    "Head to the final gathering and make your accusation.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 1, -2
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 3
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 3
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 3
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 3
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene noActionOldSister = new Scene.Builder("NoActionOldSister",
            "You decide not to act rashly.\n" +
                    "The older sister glares at you, then storms out.\n" +
                    "Now you have a moment to search the study more carefully.\n" +
                    "You notice:\n" +
                    "- A shattered photo frame of the grandfather with his family, partially torn.\n" +
                    "- The unlocked laptop, its screen showing a partially deleted email draft.")
            .addChoice(new Choice("Examine the photo frame", "CheckFrame"))
            .addChoice(new Choice("Examine the laptop", "CheckLaptop"))
            .build();

    Scene checkFrame = new Scene.Builder("CheckFrame",
            "You reconstruct the shattered photo frame.\n" +
                    "It's a normal family picture — but there is a black X over the cousin's face.\n\n" +
                    "If the cousin is the killer, this is a genuine rage indicator.\n" +
                    "If not, someone framed him.\n\n" +
                    "The cousin enters the room.")
            .addInventorySystem("markedFamilyPhoto")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 2)
            .addChoice(new Choice("Accuse him immediately", "AccuseCousin"))
            .addChoice(new Choice("Ask why he was crossed out", "AskCousin"))
            .build();

    Scene accuseCousin = new Scene.Builder("AccuseCousin",
            "You point at him and press the emergency button.\n\n" +
                    "The room goes quiet. Everyone stares.\n" +
                    "Was it really him? Head to the final gathering — the truth will come out.")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 2)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, -1
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 2
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 2
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 2
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 0, 2
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene askCousin = new Scene.Builder("AskCousin",
            "You ask him calmly why he was crossed out of the photo.\n" +
                    "He hesitates, then says:\n" +
                    "'He found out what I was doing.'\n\n" +
                    "New deduction unlocked: the victim was planning to expose someone.\n" +
                    "This connects to the will and the laptop.")
            .addInventorySystem("cousinAdmission")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 1)
            .addChoice(new Choice("Go examine the laptop", "CheckLaptop"))
            .build();

    Scene checkLaptop = new Scene.Builder("CheckLaptop",
            "You look at the screen.\n" +
                    "A drafted email reads:\n" +
                    "'Tonight, everything will be taken care of. The Family Friend won't get in the way.'\n\n" +
                    "If the killer wrote this, the metadata will confirm it.\n" +
                    "If not, it was staged.")
            .addInventorySystem("laptopDraftEmail")
            .addChoice(new Choice("Check logic history", "CheckLoginHistory"))
            .addChoice(new Choice("Accuse immediately", "AccuseFromLaptop"))
            .build();

    Scene checkLoginHistory = new Scene.Builder("CheckLoginHistory",
            "You check the login history.\n" +
                    "Last login: 8:42 PM.\n\n" +
                    "Cross-reference: who had an alibi at 8:42?\n" +
                    "This becomes critical evidence at the final gathering.")
            .addInventorySystem("loginRecord842PM")
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene accuseFromLaptop = new Scene.Builder("AccuseFromLaptop",
            "You press the emergency button without checking the metadata.\n\n" +
                    "You're acting on instinct. No login record, no verified author.\n" +
                    "The real killer knows you're rushing — your danger rises.\n" +
                    "Head to the final gathering. You'd better be right.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 3
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 3
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 3
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 3
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 0, 3
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    //Attic Branch

    Scene goToAttic = new Scene.Builder("GoToAttic",
            "You climb the narrow, creaking staircase to the attic.\n" +
                    "The air is thick with dust. Shadows stretch across the rafters.\n" +
                    "A single bulb swings slightly, casting eerie shapes.\n" +
                    "The door slams shut behind you — or was it the wind?\n\n" +
                    "You notice:\n" +
                    "- A locked chest in the corner.\n" +
                    "- A trunk with old family photos.\n" +
                    "- A pile of letters tied with black ribbon.\n\n" +
                    "A note on the floor reads: 'Some secrets are darker than the night. Look closely. - U'")
            .addSuspicionChange(NextStatementIsALie.characterNames.uncle, 1)
            .setDangerLevel(1)
            .addChoice(new Choice("Examine the locked chest", "CheckLockedChest"))
            .addChoice(new Choice("Examine the trunk with photos", "CheckTrunk"))
            .addChoice(new Choice("Examine the pile of letters", "CheckLetters"))
            .build();

    Scene checkLockedChest = new Scene.Builder("CheckLockedChest",
            "The chest is sturdy, with a rusted keyhole.\n" +
                    "You find a small key taped under the attic beam and unlock it.\n\n" +
                    "Inside:\n" +
                    "- A black hooded cloak smelling of floral perfume — a woman wore this.\n" +
                    "- A small vial of clear liquid, labeled 'For him'.\n" +
                    "- A handwritten confession: 'I can't let anyone take what is mine. Tonight, it ends.'")
            .addInventorySystem("blackCloak")
            .addInventorySystem("clearVial")
            .addInventorySystem("handwrittenConfession")
            .addChoice(new Choice("Look at the confession", "ReadConfession"))
            .addChoice(new Choice("Inspect the vial", "InspectVial"))
            .addChoice(new Choice("Examine the cloak", "ExamineCloak"))
            .build();

    Scene readConfession = new Scene.Builder("ReadConfession",
            "You look closer at the handwriting.\n" +
                    "It's shaky, forced — almost practiced.\n" +
                    "The signature doesn't match the aunt's handwriting from postcards she has sent you.\n" +
                    "It doesn't look like any of the other women either.\n\n" +
                    "Someone tried to copy the aunt's signature.\n" +
                    "Who would want to blame the murder on her?")
            .addInventorySystem("fakeSignatureObservation")
            .addChoice(new Choice("Suspect the Uncle framed his wife", "SuspectUncleFramed"))
            .addChoice(new Choice("Suspect the Father faked it", "SuspectFatherFaked"))
            .addChoice(new Choice("Suspect the Mother did it", "SuspectMotherConfession"))
            .build();

    Scene suspectUncleFramed = new Scene.Builder("SuspectUncleFramed",
            "You think the Uncle sabotaged his wife.\n" +
                    "You remember the older sister mentioning he cheated — and the aunt wants a divorce.\n" +
                    "The evidence points strongly toward him.\n\n" +
                    "Head to the final gathering and make your accusation.")
            .addSuspicionChange(NextStatementIsALie.characterNames.uncle, 3)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 2
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene suspectFatherFaked = new Scene.Builder("SuspectFatherFaked",
            "You think the Father faked the confession.\n" +
                    "He was eyeing the Aunt all night — maybe he hates her for something.\n" +
                    "The evidence feels thin but the suspicion is real.\n\n" +
                    "Head to the final gathering and make your accusation.")
            .addSuspicionChange(NextStatementIsALie.characterNames.father, 2)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 2
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene suspectMotherConfession = new Scene.Builder("SuspectMotherConfession",
            "The mother is very suspicious.\n" +
                    "She was yelling at the Father at dinner — furious he was looking at the Aunt, her sister.\n" +
                    "Jealousy. Rage. Motive.\n\n" +
                    "Head to the final gathering and make your accusation.")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 3)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 2
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene inspectVial = new Scene.Builder("InspectVial",
            "You hold the small vial up to the dim light.\n" +
                    "The liquid is dark and thick. You uncork it carefully.\n" +
                    "It smells of bitter almonds — but it's not poison.\n\n" +
                    "Scratched into the glass: 'Stage One.'\n" +
                    "That means there's a stage two. This wasn't the murder weapon.\n\n" +
                    "A tiny fingerprint smudge near the base — too large to belong to the Aunt or the Mother.\n" +
                    "Who would stage fake poison?")
            .addInventorySystem("stageOneVial")
            .addChoice(new Choice("Suspect the Cousin", "SuspectCousinVial"))
            .addChoice(new Choice("Suspect Yourself", "SuspectSelf"))
            .addChoice(new Choice("Suspect the Older Sister", "SuspectOlderSisterVial"))
            .build();

    Scene suspectCousinVial = new Scene.Builder("SuspectCousinVial",
            "The cousin has always loved drama.\n" +
                    "At dinner he said: 'Some people don't deserve second acts.'\n" +
                    "Stage One. Stage Two. That sounds theatrical.\n" +
                    "He also studied chemistry in college.\n\n" +
                    "Head to the final gathering and make your accusation.")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 3)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 2
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene suspectSelf = new Scene.Builder("SuspectSelf",
            "You don't remember how you got into the house tonight.\n" +
                    "The whole night feels like a fever dream.\n" +
                    "You were the first person to find the body. You were very calm.\n\n" +
                    "You can accuse yourself at the final gathering — but that never ends well.")
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene suspectOlderSisterVial = new Scene.Builder("SuspectOlderSisterVial",
            "The older sister loves control.\n" +
                    "She said: 'Everything in this house happens in phases.'\n" +
                    "Phases. Stages. She disappeared right before the body was found.\n" +
                    "The fingerprint — she has larger hands than the Aunt.\n\n" +
                    "Head to the final gathering and make your accusation.")
            .addSuspicionChange(NextStatementIsALie.characterNames.olderSister, 3)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 0, 2
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene examineCloak = new Scene.Builder("ExamineCloak",
            "You lift the black hooded cloak. It's heavy, high quality, recently worn.\n" +
                    "Inside the hood: a single long strand of dark hair. Not the Aunt's — her hair is short.\n" +
                    "Near the sleeve: a small tear, like it caught on something sharp.\n" +
                    "You remember hearing fabric rip earlier tonight.\n" +
                    "Who would need a disguise?")
            .addInventorySystem("blackCloakHairSample")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 2, 0
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 2, 0
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 2, 0
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 2, 0
            ))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 2, 0
            ))
            .addChoice(new Choice("Suspect the Mother", "SuspectMotherCloak"))
            .addChoice(new Choice("Suspect the Uncle", "SuspectUncle"))
            .build();

    Scene suspectMotherCloak = new Scene.Builder("SuspectMotherCloak",
            "The mother left the table suddenly before dessert, claiming she needed air.\n" +
                    "You later saw a small scratch on her wrist. That tear in the sleeve...\n\n" +
                    "Head to the final gathering and make your accusation.")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 2)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 2
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene suspectUncleCloak = new Scene.Builder("SuspectUncleCloak",
             "He once joked: 'Every good criminal needs a costume.'\n" +
            "The cloak fits his dramatic personality.\n" +
            "He had motive — the divorce, the inheritance, the humiliation.\n\n" +
            "Head to the final gathering and make your accusation.")
            .addSuspicionChange(NextStatementIsALie.characterNames.uncle, 2)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 2
            ))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene checkTrunk = new Scene.Builder("CheckTrunk",
            "You lift the lid of the trunk. Dust swirls in the dim light.\n" +
                    "Inside are old family photos. Most are mundane, but a few stand out:\n" +
                    "- A photo of the victim arguing with someone behind a closed door.\n" +
                    "- A photo of someone holding a strange object — weapon or staged prop?\n" +
                    "- A torn photo, only the bottom half remains.\n\n" +
                    "Written faintly on the back: 'Some memories die harder than people.'")
            .addInventorySystem("familyPhotoCollection")
            .addChoice(new Choice("Inspect the photos carefully", "InspectPhotos"))
            .addChoice(new Choice("Look at the arguing photo", "ArguePhoto"))
            .addChoice(new Choice("Leave the trunk alone", "LeaveTheTrunk"))
            .build();

    Scene inspectPhotos = new Scene.Builder("InspectPhotos",
            "You compare handwriting and objects across all the photos.\n" +
                    "If any object matches something found elsewhere, that character's suspicion rises.\n\n" +
                    "You carefully note everything and take the photos most relevant to your theory.")
            .addInventorySystem("photoEvidence")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 1)
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();
    Scene arguePhoto = new Scene.Builder("ArguePhoto",
            "The arguing photo is striking.\n" +
                    "The person the victim argued with — if they match another clue — becomes critical evidence.\n\n" +
                    "You may only press the emergency button if two other clues connect logically.")
            .addInventorySystem("arguingPhoto")
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene leaveTheTrunk = new Scene.Builder("LeaveTheTrunk",
            "You leave the photos untouched.\n" +
                    "The killer might return later to destroy or plant more evidence.\n" +
                    "Future events may become harder — you have lost potential deduction points.")
            .addChoice(new Choice("Continue to the letters", "CheckLetters"))
            .addChoice(new Choice("Go back downstairs", "OpenSceneForFriend"))
            .build();

    Scene checkLetters = new Scene.Builder("CheckLetters",
            "A neat pile of letters tied with black ribbon sits in the corner.\n" +
                    "Inside:\n" +
                    "- Threat letters addressed to the victim.\n" +
                    "- Handwritten notes signed with initials: M, F, C, U, O.\n" +
                    "- Postcards referencing 'tonight' and 'everything must end.'\n\n" +
                    "Only one letter will match the killer. Others are decoys.")
            .addChoice(new Choice("Read all the letters", "ReadAllLetters"))
            .addChoice(new Choice("Take only the most threatening letter", "TakeThreatLetter"))
            .addChoice(new Choice("Leave the letters untouched", "LeaveLetters"))
            .build();

    Scene readAllLetters = new Scene.Builder("ReadAllLetters",
            "You read every letter carefully.\n" +
                    "Each one reveals motive and pattern.\n" +
                    "When you connect the right letter to physical evidence, the killer's method and motive emerge.")
            .addInventorySystem("allLetters")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.father, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.uncle, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.olderSister, 1)
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene takeThreatLetter = new Scene.Builder("TakeThreatLetter",
            "The letter reads:\n" +
                    "'He saw too much. Tonight ends it all. - M'\n\n" +
                    "If M is the killer, this is primary evidence.\n" +
                    "If not, it was planted to mislead.")
            .addInventorySystem("threatLetterM")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 2)
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene leaveLetters = new Scene.Builder("LeaveLetters",
            "You leave the letters untouched.\n" +
                    "The killer may return later to remove or tamper with them.\n" +
                    "You miss a clue that could have linked method to motive.")
            .addChoice(new Choice("Go back downstairs", "OpenSceneForFriend"))
            .addChoice(new Choice("Check the chest instead", "CheckLockedChest"))
            .build();

    // Bathrooom Branch

    Scene goToBathroom = new Scene.Builder("GoToBathroom",
            "You step into the upstairs bathroom. The light hums faintly.\n" +
                    "The mirror is slightly fogged — even though no one has showered.\n" +
                    "The air smells faintly of something sweet.\n\n" +
                    "You notice:\n" +
                    "- A damp towel in the sink.\n" +
                    "- A cracked pill bottle near the trash.\n" +
                    "- A lipstick stain on the rim of a glass.\n\n" +
                    "The victim took heart medication every night before bed.")
            .setDangerLevel(1)
            .addChoice(new Choice("Examine the pill bottle", "ExaminePillBottle"))
            .addChoice(new Choice("Examine the towel", "ExamineTowel"))
            .addChoice(new Choice("Examine the stain", "ExamineStain"))
            .build();

    Scene examinePillBottle = new Scene.Builder("ExaminePillBottle",
            "You kneel beside the trash.\n" +
                    "The label reads: 'Beta blockers — prescribed to the victim.'\n" +
                    "But the pills inside are not the same color as before. They were white. These are pale pink.\n\n" +
                    "Someone switched them.\n\n" +
                    "Logical deduction: the murder may have been premeditated and quiet.\n\n" +
                    "You hear footsteps in the hallway.")
            .addInventorySystem("tamperedMedication")
            .addChoice(new Choice("Hide the bottle in your pocket", "HideBottle"))
            .addChoice(new Choice("Examine the towel", "ExamineTowel"))
            .addChoice(new Choice("Examine the stain", "ExamineStain"))
            .build();

    Scene hideBottle = new Scene.Builder("HideBottle",
            "You pocket the tampered medication.\n" +
                    "If the killer used poison, they will later notice it missing — your danger level increases.\n" +
                    "If they did not, the evidence weakens your credibility.\n\n" +
                    "You exit the bathroom quietly.")
            .addInventorySystem("tamperedMedicationHidden")
            .setDangerLevel(2)
            .addChoice(new Choice("Go to the Study", "GoToStudy"))
            .addChoice(new Choice("Go to the Attic", "GoToAttic"))
            .build();

    Scene leaveBottle = new Scene.Builder("LeaveBottle",
            "You carefully place everything back.\n" +
                    "If the killer returns to destroy evidence, you may later catch them.\n\n" +
                    "As you leave, you see someone entering the hallway from the stairs.")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 1)
            .addChoice(new Choice("Go back to catch them", "CatchHallwayPerson"))
            .addChoice(new Choice("Stay there until they leave", "StayHallway"))
            .addChoice(new Choice("Run away", "RunFromHallway"))
            .build();

    Scene catchHallwayPerson = new Scene.Builder("CatchHallwayPerson",
            "You slowly re-enter the bathroom.\n" +
                    "Someone is in here. Their suspicion meter rises.\n\n" +
                    "You don't know yet if this is the killer.\n" +
                    "Running gives you time. Confronting them is a risk.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 1, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 1, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 1, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 1, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 1, 2))
            .addChoice(new Choice("Run", "EscapeHallway"))
            .addChoice(new Choice("Confront them", "ConfrontHallwayPerson"))
            .build();

    Scene confrontHallwayPerson = new Scene.Builder("ConfrontHallwayPerson",
            "You step toward them.\n" +
                    "They freeze — then their expression shifts.\n\n" +
                    "If it is the killer, your presence here is very dangerous.\n" +
                    "If it is not, they look nervous but back away.\n\n" +
                    "Either way — suspicion rises. Head to the final gathering.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 1, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 1, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 1, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 1, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 1, 3))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene escapeHallway = new Scene.Builder("EscapeHallway",
            "You run. You barely make it out.\n" +
                    "The hallway is empty now. You are shaken but alive.")
            .addChoice(new Choice("Go to the Study", "GoToStudy"))
            .addChoice(new Choice("Go to the Attic", "GoToAttic"))
            .build();

    Scene stayHallway = new Scene.Builder("StayHallway",
            "You stand stiffly behind the door, trying to listen.\n" +
                    "They suddenly stop. The door flings open.\n\n" +
                    "You've been caught. If this is the killer — things are very bad.\n" +
                    "Your danger level is at maximum.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 0, 4))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene runFromHallway = new Scene.Builder("RunFromHallway",
            "You decide to run. You head toward safety.")
            .addChoice(new Choice("Go to the Attic", "GoToAttic"))
            .addChoice(new Choice("Go to the Study", "GoToStudy"))
            .build();

    Scene examineTowel = new Scene.Builder("ExamineTowel",
            "A white hand towel hangs near the sink — but it is not fully white.\n" +
                    "There is faint pink discoloration near the edge. Not blood. Not quite.\n" +
                    "When you touch it, it smells bitter. Almonds.\n\n" +
                    "Logical deduction: someone washed their hands after handling something toxic.\n" +
                    "The killer tried to remove physical traces.\n\n" +
                    "You flip the towel over. There's foundation smeared into the corner. Not your shade.\n\n" +
                    "You hear a floorboard creak outside.")
            .addInventorySystem("almondscentTowel")
            .addChoice(new Choice("Take the towel with you", "TakeTowel"))
            .addChoice(new Choice("Smell the towel more carefully", "SmellTowel"))
            .build();

    Scene takeTowel = new Scene.Builder("TakeTowel",
            "You pocket the towel.\n" +
                    "If the killer used poison, they will notice it missing — danger level rises.\n" +
                    "If not, you appear paranoid later during accusations.\n\n" +
                    "You step into the hallway. Someone is at the end of it.\n" +
                    "Whoever you see has their suspicion meter rise.")
            .addInventorySystem("towelEvidence")
            .setDangerLevel(2)
            .addSuspicionChange(NextStatementIsALie.characterNames.uncle, 1)
            .addChoice(new Choice("Confront them", "ConfrontHallwayPerson"))
            .addChoice(new Choice("Pretend you saw nothing", "IgnoreHallwayPerson"))
            .build();

    Scene ignoreHallwayPerson = new Scene.Builder("IgnoreHallwayPerson",
            "You walk past as if nothing happened.\n" +
                    "They don't stop you.\n" +
                    "You may continue your investigation.")
            .addChoice(new Choice("Go to the Study", "GoToStudy"))
            .addChoice(new Choice("Go to the Attic", "GoToAttic"))
            .build();

    Scene smellTowel = new Scene.Builder("SmellTowel",
            "You inhale deeply. Bad choice.\n" +
                    "Your vision swims slightly.\n" +
                    "If the killer used contact-transfer poison, your health begins declining.\n" +
                    "You will collapse later unless you deduce correctly.\n\n" +
                    "You hear whispering outside.")
            .setDangerLevel(2)
            .addChoice(new Choice("Open the door suddenly", "OpenDoorSuddenly"))
            .addChoice(new Choice("Listen carefully", "ListenAtDoor"))
            .build();

    Scene openDoorSuddenly = new Scene.Builder("OpenDoorSuddenly",
            "You catch two people talking — the Cousin and the Older Sister.\n" +
                    "They stop mid-sentence.\n" +
                    "'Did you move it?'\n" +
                    "'Not yet.'\n" +
                    "Both suspicion meters rise.")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.olderSister, 1)
            .addChoice(new Choice("Go to the Study", "GoToStudy"))
            .addChoice(new Choice("Go to the Attic", "GoToAttic"))
            .build();

    Scene listenAtDoor = new Scene.Builder("ListenAtDoor",
            "You only hear one set of footsteps.\n" +
                    "Which means someone was pretending to whisper.\n\n" +
                    "Psychological manipulation route unlocked.\n" +
                    "Someone is orchestrating what you hear.")
            .addInventorySystem("whisperObservation")
            .addChoice(new Choice("Go to the Study", "GoToStudy"))
            .addChoice(new Choice("Go to the Attic", "GoToAttic"))
            .build();

    Scene examineStain = new Scene.Builder("ExamineStain",
            "There's something near the base of the bathtub.\n" +
                    "Dark. Almost wiped. Not red. Brownish.\n" +
                    "You kneel closer. It's not blood — it's vomit residue.\n\n" +
                    "Logical deduction: if death was poisoning, there would be nausea.\n" +
                    "Someone cleaned up after the victim.\n\n" +
                    "You look closer. Scrape marks near the tile. The body may have been moved.\n\n" +
                    "You hear something metallic drop downstairs.")
            .addInventorySystem("vomitResidueObservation")
            .addChoice(new Choice("Touch the residue with tissue", "TouchResidue"))
            .addChoice(new Choice("Follow the scrape marks", "FollowScrapeMarks"))
            .build();

  Scene touchResidue = new Scene.Builder("TouchResidue",
            "You use a tissue. Inside it you find something metallic.\n" +
                    "A cufflink. Engraved initials.\n\n" +
                    "If the initials match the killer — suspicion rises sharply.\n" +
                    "If not — the cufflink was planted to frame someone else.")
            .addInventorySystem("cufflink")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 2, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 2, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 2, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 2, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 2, 0))
            .addChoice(new Choice("Keep the cufflink", "KeepCufflink"))
            .addChoice(new Choice("Leave it where it is", "LeaveCufflink"))
            .build();

    Scene keepCufflink = new Scene.Builder("KeepCufflink",
            "You pocket the cufflink.\n" +
                    "If it belongs to the real killer — they will panic later.\n" +
                    "If it was planted — the real killer may try to frame you.")
            .addInventorySystem("cufflinkKept")
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene leaveCufflink = new Scene.Builder("LeaveCufflink",
            "You leave the cufflink in place.\n" +
                    "You may later catch someone trying to retrieve it.")
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene followScrapeMarks = new Scene.Builder("FollowScrapeMarks",
            "The scrape marks lead toward the hallway — then stop abruptly.\n" +
                    "The carpet has been replaced. Fresh fibers.\n\n" +
                    "Logical deduction: the killer was calm. This was not impulsive.\n" +
                    "Only someone with full house access could do this.\n\n" +
                    "You hear footsteps descending the stairs.")
            .addInventorySystem("replacedCarpetObservation")
            .addChoice(new Choice("Hide in the linen closet", "HideInCloset"))
            .addChoice(new Choice("Stand your ground", "StandGround"))
            .build();

    Scene hideInCloset = new Scene.Builder("HideInCloset",
            "You slip into the linen closet and pull the door shut.\n" +
                    "You overhear:\n" +
                    "'If they find the amendment, we're finished.'\n\n" +
                    "The will branch and bathroom branch now connect.\n" +
                    "This is critical evidence.")
            .addInventorySystem("amendmentOverheard")
            .addSuspicionChange(NextStatementIsALie.characterNames.father, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene standGround = new Scene.Builder("StandGround",
            "You stand still as whoever descends the stairs sees you investigating.\n\n" +
                    "If it is the killer, your final ending becomes confrontation-based.\n" +
                    "You cannot win through guessing — only through accumulated logic.")
            .setDangerLevel(3)
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();
}
