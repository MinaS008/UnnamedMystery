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

        public Builder(String sceneID, String narrativeText){
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

        public Builder setTriggersFinalGathering(boolean value) {
            triggersFinalGathering = value;
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

    public String toString() {
        return "Scene[" + sceneID + "] choices=" + choices.size() +
                " items=" + inventoryAdds.size() +
                " danger=" + dangerLevel;
    }

    //OPENING GATHERING
    Scene openingGathering = new Scene.Builder("OpeningGathering",
            "The countryside estate is silent except for the rain against the windows.\n\n" +
                    "You are gathered in the living room — all of you.\n" +
                    "The fire is low. The drinks have gone untouched.\n\n" +
                    "Upstairs, your grandfather lies dead.\n\n" +
                    "No one has called anyone yet. No one has moved toward the door.\n" +
                    "The storm outside sees to that.\n\n" +
                    "You look around the room:\n\n" +
                    "Your MOTHER sits closest to the fire, hands folded, eyes down.\n" +
                    "Your FATHER stands by the window, watching the rain.\n" +
                    "Your OLDER SISTER has not sat down since they found him.\n" +
                    "Your LITTLE BROTHER does not seem to understand what is happening.\n" +
                    "Your UNCLE pours himself a drink he has not touched.\n" +
                    "Your COUSIN is looking at his phone — or pretending to.\n" +
                    "The FAMILY FRIEND hovers near the door, as if deciding something.\n\n" +
                    "Someone in this room did this.\n\n" +
                    "The question is who — and whether you will find out before they\n" +
                    "decide that you know too much.\n\n" +
                    "Choose your perspective wisely.")
            .build();

    //FAMILY FRIEND
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
            .build();

    Scene suspectSelf = new Scene.Builder("SuspectSelf",
            "You don't remember how you got into the house tonight.\n" +
                    "The whole night feels like a fever dream.\n" +
                    "You were the first person to find the body. You were very calm.\n\n" +
                    "You can accuse yourself at the final gathering — but that never ends well.")
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
            .build();
    Scene arguePhoto = new Scene.Builder("ArguePhoto",
            "The arguing photo is striking.\n" +
                    "The person the victim argued with — if they match another clue — becomes critical evidence.\n\n" +
                    "You may only press the emergency button if two other clues connect logically.")
            .addInventorySystem("arguingPhoto")
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
            .build();

    Scene takeThreatLetter = new Scene.Builder("TakeThreatLetter",
            "The letter reads:\n" +
                    "'He saw too much. Tonight ends it all. - M'\n\n" +
                    "If M is the killer, this is primary evidence.\n" +
                    "If not, it was planted to mislead.")
            .addInventorySystem("threatLetterM")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 2)
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
            .build();

    Scene leaveCufflink = new Scene.Builder("LeaveCufflink",
            "You leave the cufflink in place.\n" +
                    "You may later catch someone trying to retrieve it.")
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .setTriggersFinalGathering(true)
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
            .setTriggersFinalGathering(true)
            .build();

    Scene standGround = new Scene.Builder("StandGround",
            "You stand still as whoever descends the stairs sees you investigating.\n\n" +
                    "If it is the killer, your final ending becomes confrontation-based.\n" +
                    "You cannot win through guessing — only through accumulated logic.")
            .setDangerLevel(3)
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .setTriggersFinalGathering(true)
            .build();

    //OLDER SISTER
    Scene openSceneSister = new Scene.Builder("OpenSceneForSister",
            "The kitchen is eerily silent.\n" +
                    "The dinner plates have been cleared, but the lingering smell of herbs and something sharper hangs in the air.\n" +
                    "On the counter, a kitchen knife rests beside the cutting board. It is clean. Too clean.\n" +
                    "You run your finger along the blade. Someone wiped this.\n" +
                    "You look around and notice:\n" +
                    "- The knife on the counter, recently wiped.\n" +
                    "- A crumpled receipt near the bin.\n" +
                    "- The pantry door slightly ajar.\n" +
                    "You decide to investigate before the others return.")
            .addChoice(new Choice("Go to the Pantry", "GoToPantry"))
            .addChoice(new Choice("Go to the Wine Cellar", "GoToCellar"))
            .addChoice(new Choice("Go to the Study", "GoToStudySister"))
            .addInventorySystem("kitchenKnife")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .setDangerLevel(1)
            .build();

    Scene goToPantry = new Scene.Builder("GoToPantry",
            "Between the bread loaves on the shelf, you spot a crumpled note.\n" +
                    "You smooth it out under the dim light.\n\n" +
                    "\"You searched for steel and crimson thread,\n" +
                    "But found warm crumbs and broken bread.\n" +
                    "No wound was carved, no blade was shown —\n" +
                    "The silent killer made it rise alone.\"\n\n" +
                    "A riddle. Someone left this deliberately.\n" +
                    "Suddenly, voices rise upstairs — someone is arguing.")
            .addInventorySystem("pantryRiddleNote")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .addChoice(new Choice("Go to investigate", "InvestigateArguing"))
            .addChoice(new Choice("Run in the other direction", "RunFromArguing"))
            .build();

    Scene investigateArguing = new Scene.Builder("InvestigateArguing",
            "You creep upstairs and press your ear to the door.\n" +
                    "Inside, your parents argue in hushed, furious tones.\n\n" +
                    "\"If they find the amendment, we look guilty. Both of us.\"\n" +
                    "\"That was never supposed to happen tonight.\"\n\n" +
                    "Both sound desperate. Both sound afraid.")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.father, 1)
            .addChoice(new Choice("Try to record them", "RecordParents"))
            .addChoice(new Choice("Confront them", "ConfrontParents"))
            .build();

    Scene recordParents = new Scene.Builder("RecordParents",
            "You pull out your phone and begin recording through the gap in the door.\n" +
                    "But your hand brushes the frame. A creak. Silence from inside.\n" +
                    "The door swings open. They have seen you.\n" +
                    "Now they know you were listening.\n\n" +
                    "If either parent is the killer, your danger level rises and the Final Gathering is triggered.\n" +
                    "If neither is the killer, your suspicion rises among all characters — you are now a suspect.")
            .addInventorySystem("partialRecording")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 2))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .setTriggersFinalGathering(true)
            .build();

    Scene confrontParents = new Scene.Builder("ConfrontParents",
            "You push the door open. They freeze mid-sentence.\n\n" +
                    "\"What did you hear?\"\n\n" +
                    "You press them. Your father looks at your mother. Your mother looks at the floor.\n\n" +
                    "\"Grandfather planned to expose someone tonight. Before he could — he was gone.\"\n\n" +
                    "New Deduction Unlocked: The murder may have been preventative.")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.father, 1)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 2))
            .addChoice(new Choice("Go to the Wine Cellar", "GoToCellar"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene runFromArguing = new Scene.Builder("RunFromArguing",
            "You turn and move quickly down the hallway.\n" +
                    "The voices fade behind you.\n" +
                    "The corridor splits ahead — one direction leads to the back stairwell, the other toward the exit.")
            .addChoice(new Choice("Go to the exit", "SisterExitOption"))
            .addChoice(new Choice("Go to the Nursery", "GoToNursery"))
            .build();

    Scene sisterExitOption = new Scene.Builder("SisterExitOption",
            "The back door stands before you. Cool night air bleeds through the gap at the bottom.\n" +
                    "Freedom. Safety.\n\n" +
                    "But the killer is still in this house.\n\n" +
                    "Do you leave and let it go unsolved? Or do you turn back?")
            .setIsExitScene(true)
            .addChoice(new Choice("Exit the house", "EscapeUnsolved"))
            .addChoice(new Choice("Go back and investigate", "GoToNursery"))
            .build();

    Scene goToNursery = new Scene.Builder("GoToNursery",
            "You ease open the nursery door.\n" +
                    "The room smells of talcum powder and something faintly chemical beneath it.\n" +
                    "A single lamp casts long shadows over the crib and the old wardrobe in the corner.\n\n" +
                    "You know this room well. You used to play here.\n\n" +
                    "You notice:\n" +
                    "- The crib, recently disturbed — sheets slightly pulled.\n" +
                    "- The wardrobe, one door slightly open.\n" +
                    "- A stack of old photographs on the side table.")
            .addChoice(new Choice("Look behind the crib", "CheckCrib"))
            .addChoice(new Choice("Look inside the wardrobe", "CheckWardrobe"))
            .addChoice(new Choice("Look at the photographs", "CheckNurseryPhotos"))
            .build();

    Scene checkCrib = new Scene.Builder("CheckCrib", "You reach behind the crib and feel something stiff and cold.\n" +
            "A hidden bundle of cash — bound with a rubber band.\n\n" +
            "You remember the Family Friend leaning over the crib earlier during the house tour.\n" +
            "Laughing. Pretending to admire it.\n\n" +
            "Was he looking for this?\n\n" +
            "The wardrobe door creaks behind you. You hold your breath.")
            .addInventorySystem("Hidden cash bundle")
            .addSuspicionChange(NextStatementIsALie.characterNames.familyFriend, 1)
            .addChoice(new Choice("Open the wardrobe", "OpenWardrobeWithPresence"))
            .build();

    Scene openWardrobeWithPresence = new Scene.Builder("OpenWardrobeWithPresence", "You pull the wardrobe door open.\n\n" +
            "Your cousin stands inside, clutching a glass vial.\n\n" +
            "His eyes go wide. So do yours.\n\n" +
            "For a moment neither of you move.\n\n" +
            "Then he lunges — not for you, but for the exit.\n" +
            "He shoves past you and runs.")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 3)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 0))
            .addInventorySystem("droppedVialNote")
            .addChoice(new Choice("Press the emergency button and accuse him", "Final Gathering"))
            .addChoice(new Choice("Investigate further first", "GoToStudySister"))
            .build();

    Scene checkWardrobe = new Scene.Builder("CheckWardrobe",         "You open the wardrobe carefully.\n" +
            "Clothes hang neatly — but tucked behind a winter coat is a glass vial.\n" +
            "Dark liquid inside. A faint chemical scent.\n\n" +
            "You pick it up. Scratched into the glass: 'Stage One.'\n\n" +
            "That means there is a stage two.\n\n" +
            "Footsteps approach in the hallway. Your cousin walks in.")
            .addInventorySystem("stageOneVial")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 3)
            .addChoice(new Choice("Hide the vial quickly", "HideVialFromCousin"))
            .addChoice(new Choice("Accuse him immediately", "Final Gathering"))
            .build();

    Scene hideVialFromCousin = new Scene.Builder("HideVialFromCousin",
            "You slip the vial into your pocket just as he enters.\n\n" +
                    "He looks at you. Then at the wardrobe. His eyes narrow.\n\n" +
                    "\"You're in here alone?\"\n\n" +
                    "You hold his gaze. \"I was just looking around.\"\n\n" +"\n" +
                    "He stares for a long beat — then leaves without another word.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 0))
            .addChoice(new Choice("Go to the Study", "GoToStudySister"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene checkNurseryPhotos = new Scene.Builder("CheckNurseryPhotos",
            "You flip through the photographs on the side table.\n" +
            "Most are old family portraits — but two stand out:\n" +
            "- A photo of the victim mid-argument behind a closed door.\n" +
            "- A torn photo with only the bottom half remaining.\n\n" +
            "Written faintly on the back of the torn photo:\n" +
            "'Some memories die harder than people.'")
            .addInventorySystem("nurseryPhotoCollection")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 1)
            .addChoice(new Choice("Go to the Study", "GoToStudySister"))
            .addChoice(new Choice("Go to the Wine Cellar", "GoToCellar"))
            .build();

    Scene goToCellar = new Scene.Builder("GoToCellar",
            "You descend the creaking staircase to the cellar.\n" +
            "A single bulb swings above you — as if someone was here moments ago.\n" +
            "The smell of damp stone and aged wine is undercut by something faintly chemical.\n\n" +
            "You notice three things immediately:\n" +
            "- A broken wine glass on the floor.\n" +
            "- A muddy footprint near the back wall.\n" +
            "- One wine rack slightly pulled forward.\n\n" +
            "Your uncle prides himself on this cellar. He never lets anyone touch the collection.")
            .addInventorySystem("brokenWineGlass")
            .addInventorySystem("footprintPhotograph")
            .addSuspicionChange(NextStatementIsALie.characterNames.uncle, 1)
            .setDangerLevel(1)
            .addChoice(new Choice("Examine the wine rack", "ExamineCellarRack"))
            .addChoice(new Choice("Follow the muddy footprint", "FollowCellarFootprint"))
            .addChoice(new Choice("Inspect the broken wine glass", "InspectBrokenGlass"))
            .build();

    Scene examineCellarRack = new Scene.Builder("ExamineCellarRack",
            "You pull the rack further from the wall.\n" +
                    "Behind it: a small metal box, already forced open.\n" +
                    "Inside — nothing. Except a faint smell.\n" +
                    "Bitter and chemical. Almonds.\n\n" +
                    "Suddenly the cellar door slams shut. The light flickers.\n" +
                    "You hear footsteps upstairs.")
            .setDangerLevel(1)
            .addChoice(new Choice("Bang on he door and call for help", "BangCellarDoor"))
            .addChoice(new Choice("stay silent and look for another exit", "FindCellarPassage"))
            .build();

    Scene bangCellarDoor = new Scene.Builder("BangCellarDoor",
            "After several seconds, the door opens.\n" +
                    "It is your father. He looks annoyed — not concerned.\n\n" +
                    "\"Why are you snooping alone down here?\"\n\n" +
                    "You choose your next words carefully.")
            .addSuspicionChange(NextStatementIsALie.characterNames.father, 1)
            .addChoice(new Choice("Accuse him directly", "AccuseFatherCellar"))
            .addChoice(new Choice("Apologise and lie", "LieFatherCellar"))
            .build();

    Scene accuseFatherDirectly = new Scene.Builder("AccuseFatherCellar",
            "You say it plainly: \"You locked me in here.\"\n\n" +
                    "He goes very still.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 1, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 1, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 1, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 1, 1))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .setTriggersFinalGathering(true)
            .build();

    Scene lieFatherCellar = new Scene.Builder("LieFatherCellar",
            "\"I was looking for wine,\" you say. \"The door must have caught.\"\n\n" +
                    "He studies you for a beat too long. Then he leaves.\n\n" +
                    "You may continue exploring.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 0))
            .addChoice(new Choice("Follow the footprint", "FollowCellarFootprint"))
            .addChoice(new Choice("Inspect the broken glass", "InspectBrokenGlass"))
            .build();

    Scene findCellarPassage = new Scene.Builder("FindCellarPassage",
            "You find a servant passage behind the barrels. It leads upward.\n" +
            "Two voices whisper somewhere close.\n\n" +
            "You recognise your mother — and the Family Friend.\n\n" +
            "Fragments only:\n" +
            "\"...should have waited...\"\n" +
            "\"...wasn't part of the plan...\"\n" +
            "\"Too late now...\"")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.familyFriend, 1)
            .addChoice(new Choice("Reveal yourself", "RevealCellarPassage"))
            .addChoice(new Choice("Record the conversation secretly", "RecordCellarPassage"))
            .build();

    Scene revealCellarPassage = new  Scene.Builder("RevealCellarPassage",
            "They stop the moment they see you.\n\n" +
                    "If either is the killer, they attempt to push you down the stairs.\n" +
                    "You must guess which one.\n\n" +
                    "If correct — you shove them and they fall unconscious. The Final Gathering is triggered.\n" +
                    "If wrong — you fall. Game over.\n\n" +
                    "If neither is the killer, they accuse you of spying and the Final Gathering triggers regardless.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 2, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 2, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 1))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .setTriggersFinalGathering(true)
            .build();

    Scene recordCellarPassage = new Scene.Builder("RecordCellarPassage",
            "You stay hidden and record everything you can.\n\n" +
                    "If the killer is among them, this recording can be used at the Final Gathering for automatic confirmation.\n" +
                    "Their own words seal it.")
            .addInventorySystem("partialCellarRecording")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.familyFriend, 1)
            .addChoice(new Choice("Go to the Study", "GoToStudySister"))
            .addChoice(new Choice("Go the Final Gathering", "Final Gathering"))
            .build();

    Scene followCellarFootprint = new Scene.Builder("FollowCellarFootprint",
            "The footprint leads to a section of the cellar rarely used.\n" +
                    "There is dirt on the floor — but it has not rained outside tonight.\n\n" +
                    "You find a hidden trapdoor under an old rug.\n\n" +
                    "New Deduction Unlocked: The killer may have staged external entry or used a false alibi.")
            .addInventorySystem("inconsistencyNote")
            .addChoice(new Choice("Hide in the tunnel", "HideInTunnel"))
            .addChoice(new Choice("Wait and confront whoever enters", "WaitInCellar"))
            .build();

    Scene hideInTunnel = new Builder("HideInTunnel",
            "Someone enters the cellar. You see only their shoes through the gap.\n\n" +
                    "You must guess who it is.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 3, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 3, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 3, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 3))
            .addChoice(new Choice("Guess it is the Uncle", "Final Gathering"))
            .addChoice(new Choice("Guess it is the Cousin", "Final Gathering"))
            .addChoice(new Choice("Guess it is the Family Friend", "Final Gathering"))
            .setTriggersFinalGathering(true)
            .build();

    Scene waitInCellar = new Scene.Builder("WaitInCellar",
            "You step back and stand your ground.\n\n" +
                    "The door opens. It is your cousin.\n" +
                    "He looks terrified — not guilty.\n" +
                    "He says he followed someone down here earlier.")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 1)
            .addChoice(new Choice("Trust him", "TrustCousin"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene trustCousin = new Scene.Builder("TrustCousin",
            "You decide to believe him — for now.\n\n" +
                    "He tells you he saw someone enter the cellar carrying something wrapped in cloth.\n" +
                    "He couldn't see their face.\n\n" +
                    "He agrees to watch your back at the Final Gathering.\n\n" +
                    "Note: he may still be the killer.")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 1)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 0))
            .addChoice(new Choice("Go to the Study", "GoToStudySister"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene inspectBrokenGlass = new Scene.Builder("InspectBrokenGlass",
            "You kneel beside the shattered glass.\n" +
            "When you lean close, you catch it — bitter almonds.\n\n" +
            "Your grandfather was allergic to almonds.\n\n" +
            "This confirms the poison theory.\n\n" +
            "Suddenly the cellar light goes out. Darkness.\n" +
            "You feel someone behind you.\n\n" +
            "You must choose instantly.")
            .addInventorySystem("almondScentObservation")
            .setDangerLevel(2)
            .addChoice(new Choice("Turn around", "CellarDarkTurnAround"))
            .addChoice(new Choice("Run for the stairs", "CellarDarkRun"))
            .addChoice(new Choice("Stay perfectly still", "CellarDarkStill"))
            .build();

    Scene cellarDarkTurnAround = new Scene.Builder("CellarDarkTurnAround",
            "You spin around and face the darkness.\n\n" +
                    "A hand reaches for you — then stops.\n\n" +
                    "You react instinctively and narrowly avoid being shoved.\n" +
                    "The shadow retreats. You hear hurried footsteps up the stairs.\n\n" +
                    "Someone knows you found the poison.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 2, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 2, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 4))
            .addChoice(new Choice("Go to the Study", "GoToStudySister"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene cellarDarkRun = new Scene.Builder("CellarDarkRun",
            "You bolt for the stairs.\n\n" +
                    "Your feet find the first step. You scramble up and burst into the hallway.\n" +
                    "Behind you — silence.\n\n" +
                    "Someone knows you found the poison.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 2, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 2, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 4))
            .addChoice(new Choice("Go to the Study", "GoToStudySister"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene cellarDarkStill = new Scene.Builder("CellarDarkStill",
            "You press yourself against the wall and go completely still.\n\n" +
                    "Footsteps move past you in the dark.\n" +
                    "A pause. Then they continue toward the stairs and up.\n\n" +
                    "You wait until the sound disappears entirely.\n\n" +
                    "Someone knows you found the poison.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 2, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 4))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 4))
            .addChoice(new Choice("Go to the Study", "GoToStudySister"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene goToStudySister = new Scene.Builder("GoToStudySister",
            "You step into the study. The imposing desk dominates the room.\n" +
            "It is half-open, as if someone left in a hurry.\n\n" +
            "You see three things inside:\n" +
            "- A revised will — dated yesterday.\n" +
            "- A torn envelope.\n" +
            "- A fountain pen, still uncapped.")
            .addInventorySystem("revisedWill")
            .addInventorySystem("tornEnvelope")
            .addInventorySystem("A fountain pen, still uncapped")
            .setDangerLevel(1)
            .addChoice(new Choice("Read the will carefully", "ReadWillSister"))
            .addChoice(new Choice("Force open the locked drawer", "ForceDrawer"))
            .addChoice(new Choice("Hide the will and observe", "HideWillSister"))
            .build();

    Scene readWillSister = new Scene.Builder("ReadWillSister",
            "You scan the names. It is the grandfather's will.\n\n" +
                    "A handwritten amendment — added this morning — changes the inheritance distribution.\n\n" +
                    "Whoever this harms most had the strongest motive.\n\n" +
                    "You hear footsteps behind you. It is your mother.")
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
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 1, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 2, 0))
            .addChoice(new Choice("Confront her about the amendment", "ConfrontMotherWill"))
            .addChoice(new Choice("Hide the page before she sees it", "HideWillFromMother"))
            .build();

    Scene confrontMotherWill = new Scene.Builder("ConfrontMotherWill",
            "You hold up the amendment page.\n\n" +
                    "She does not panic. Instead, she begins to cry — slowly, deliberately.\n" +
                    "She twists the story. She tries to make you doubt yourself.\n\n" +
                    "If she is not the killer, she says quietly:\n" +
                    "\"Your grandfather planned to expose someone tonight.\n" +
                    "Before he could — he was dead.\"\n\n" +
                    "New Deduction Unlocked: The murder may have been preventative.")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 0))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .addChoice(new Choice("Go to the Wine Cellar", "GoToCellar"))
            .build();

    Scene hideWillFromMother = new Scene.Builder("HideWillFromMother",
            "You fold the amendment page and slip it into your pocket before she can see it.\n\n" +
                    "She enters the room and pauses. Her eyes sweep the desk.\n" +
                    "Something is missing and she knows it.\n\n" +
                    "She says nothing. She leaves.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 1))
            .addChoice(new Choice("Go to the Wine Cellar", "GoToCellar"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene forceDrawer = new Scene.Builder("ForceDrawer",
            "You force the locked drawer open.\n\n" +
                    "Inside:\n" +
                    "- An old revolver — unloaded.\n" +
                    "- Insurance paperwork.\n" +
                    "- A medical allergy report.\n\n" +
                    "The allergy report confirms the almond allergy theory officially.\n\n" +
                    "Suddenly you hear glass shatter upstairs.")
            .addInventorySystem("allergyConfirmation")
            .addChoice(new Choice("Go upstairs immediately", "GoUpstairsGlass"))
            .addChoice(new Choice("Stay and secure the revolver", "TakeRevolver"))
            .addChoice(new Choice("Call out from below", "CallOutFromStudy"))
            .build();

    Scene goUpstairsGlass = new Scene.Builder("GoUpstairsGlass",   "You sprint upstairs and throw open the door.\n\n" +
            "Your father stands over your uncle.\n" +
            "Your uncle is unconscious — but breathing.\n\n" +
            "Your father turns. His expression is unreadable.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 3, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 2))
            .build();

    Scene takeRevolver = new Scene.Builder("TakeRevolver",
            "You pick up the revolver. You don't know it is unloaded.\n\n" +
                    "You go upstairs.\n" +
                    "The cousin and the Family Friend stand facing each other.\n" +
                    "Both go silent when they see you.\n\n" +
                    "One of them is guilty. The other is not.\n\n" +
                    "If you guess correctly and aim the gun, they freeze.\n" +
                    "Then they rush you when they realise it is unloaded.\n" +
                    "If you evade them, the Final Gathering triggers.\n" +
                    "If not — game over.")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 0, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 0, 3))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 0, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 0, 2))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 0, 2))
            .addChoice(new Choice("Accuse the Cousin", "Final Gathering"))
            .addChoice(new Choice("Accuse the Family Friend", "Final Gathering"))
            .setTriggersFinalGathering(true)
            .build();

    Scene callOutFromStudy = new Scene.Builder("CallOutFromStudy",
            "You call upstairs. The sounds go quiet. Footsteps.\n" +
                    "Your mother appears at the top of the stairs, looking down.\n\n" +
                    "\"There is nothing up here,\" she says.\n" +
                    "\"Come up and see for yourself.\"")
            .addSuspicionChange(NextStatementIsALie.characterNames.mother, 1)
            .addChoice(new Choice("Go upstairs", "ConfrontMotherWill"))
            .addChoice(new Choice("Stay put", "GoToCellar"))
            .build();

    Scene hideWillSister = new Scene.Builder("HideWillSister",
            "You hide the will — not just a copy. The original goes into your pocket.\n\n" +
            "You step back and wait.\n\n" +
            "If the killer has an inheritance motive, they return to the study within the next scene\n" +
            "and notice the will is missing.\n" +
            "They accuse you publicly at the Final Gathering. Your suspicion rises.")
            .addInventorySystem("grandfathersWillOriginal")
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.olderSister, 2, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.olderSister, 2, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.olderSister, 2, 1))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.olderSister, 1, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.olderSister, 2, 1))
            .addChoice(new Choice("Go to the Wine Cellar", "GoToCellar"))
            .addChoice(new Choice("Go to the Fireplace", "GoToFireplace"))
            .build();

    Scene goToFireplace = new Scene.Builder("GoToFireplace", "You examine the dying embers in the sitting room fireplace.\n" +
            "No one has passed through here recently — you are alone.\n\n" +
            "You find:\n" +
            "- A burned document fragment.\n" +
            "- A partially melted gold button.\n" +
            "- Ashes not fully cold.")
            .addInventorySystem("burnedDocumentFragment")
            .addChoice(new Choice("Piece the fragment together", "PieceDocument"))
            .addChoice(new Choice("Search the chimney", "SearchChimney"))
            .build();

    Scene pieceDocument = new Scene.Builder("PieceDocument",
            "You carefully assemble the burned fragments.\n\n" +
                    "When complete, the text reads:\n" +
                    "\"Beneficiary changed to —\"\n\n" +
                    "The name is burned away.\n\n" +
                    "If you already have the will in your inventory, the pieces connect.\n" +
                    "Suspicion rises sharply for the financially harmed character.")
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
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 1, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 2, 0))
            .addChoice(new Choice("Go to the Wine Cellar", "GoToCellar"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene searchChimney = new Scene.Builder("SearchChimney",
            "You reach up inside the chimney flue and feel a small velvet pouch wedged against the wall.\n\n" +
                    "Inside: almond shavings.\n\n" +
                    "This directly implicates poison staging.\n\n" +
                    "If the killer used poison, this is primary evidence.\n" +
                    "If not, this was placed here to mislead.")
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
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 2, 0))
            .addKillerConditionalEffect(new Clue.KillerConditionalEffect(
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 2, 0))
            .addChoice(new Choice("Go to the Wine Cellar", "GoToCellar"))
            .addChoice(new Choice("Go to the Final Gathering", "Final Gathering"))
            .build();

    Scene stayInKitchen = new Scene.Builder("StayInKitchen",
            "You move deeper into the kitchen, staying out of sight.\n\n" +
            "The door opens. Your cousin and the Family Friend enter, talking in urgent, low voices.\n\n" +
            "\"Someone is going to figure it out...\"\n" +
            "\"What are you going to do?\"\n\n" +
            "You hold your breath behind the counter.\n\n" +
            "They leave. You slip safely out of the kitchen.")
            .addSuspicionChange(NextStatementIsALie.characterNames.cousin, 1)
            .addSuspicionChange(NextStatementIsALie.characterNames.familyFriend, 1)
            .addChoice(new Choice("Go to the Study", "GoToStudySister"))
            .addChoice(new Choice("Go to the Fireplace", "GoToFireplace"))
            .addChoice(new Choice("Go to Wine Cellar", "GoToCellar"))
            .build();

    //FINAL GATHERING

    Scene finalGathering = new Scene.Builder("Final Gathering",
            "The remaining members of the family gather in the dimly lit sitting room.\n" +
                    "No one speaks at first. The fire has died down to embers.\n" +
                    "Every face is a mask — grief, fear, or something else entirely.\n\n" +
                    "You look around the room. You have been through the house.\n" +
                    "You have seen what others have not.\n\n" +
                    "Now it comes down to this.\n\n" +
                    "You may accuse someone — but choose carefully.\n" +
                    "A wrong accusation ends everything.\n" +
                    "If you are not ready, you can attempt to leave.")
            .addChoice(new Choice(
                    "Accuse the Mother",
                    "AccuseOutcomeMother",
                    null,
                    null,
                    NextStatementIsALie.characterNames.mother,
                    NextStatementIsALie.characterNames.mother, 3))
            .addChoice(new Choice(
                    "Accuse the Father",
                    "AccuseOutcomeFather",
                    null,
                    null,
                    NextStatementIsALie.characterNames.father,
                    NextStatementIsALie.characterNames.father, 3))
            .addChoice(new Choice(
                    "Accuse the Older Sister",
                    "AccuseOutcomeOlderSister",
                    null,
                    NextStatementIsALie.characterNames.familyFriend,  // only appears if playing as Family Friend,
                    NextStatementIsALie.characterNames.olderSister,
                    NextStatementIsALie.characterNames.olderSister, 3))
            .addChoice(new Choice(
                    "Accuse the Uncle",
                    "AccuseOutcomeUncle",
                    null,
                    null,
                    NextStatementIsALie.characterNames.uncle,
                    NextStatementIsALie.characterNames.uncle, 3))
            .addChoice(new Choice(
                    "Accuse the Cousin",
                    "AccuseOutcomeCousin",
                    null,
                    null,
                    NextStatementIsALie.characterNames.cousin,
                    NextStatementIsALie.characterNames.cousin, 3))
            .addChoice(new Choice(
                    "Accuse the Family Friend",
                    "AccuseOutcomeFamilyFriend",
                    null,
                    NextStatementIsALie.characterNames.olderSister,  // only appears if playing as OlderSister
                    NextStatementIsALie.characterNames.familyFriend,
                    NextStatementIsALie.characterNames.familyFriend, 3))
            .addChoice(new Choice(
                    "Attempt to leave without accusing anyone",
                    "EscapeUnsolved"))
            .build();

//Accusation outcomes

    Scene accuseOutcomeMother = new Scene.Builder("AccuseOutcomeMother",
            "You step forward. All eyes turn to you.\n\n" +
                    "\"It was you,\" you say, looking directly at the Mother.\n\n" +
                    "The room goes utterly still.\n\n" +
                    "She holds your gaze for a long moment.\n" +
                    "Then something shifts in her expression.")
            .build();

    Scene accuseOutcomeFather = new Scene.Builder("AccuseOutcomeFather",
            "You step forward. All eyes turn to you.\n\n" +
                    "\"It was you,\" you say, looking directly at the Father.\n\n" +
                    "The room goes utterly still.\n\n" +
                    "He holds your gaze for a long moment.\n" +
                    "Then something shifts in his expression.")
            .build();

    Scene accuseOutcomeOlderSister = new Scene.Builder("AccuseOutcomeOlderSister",
            "You step forward. All eyes turn to you.\n\n" +
                    "\"It was you,\" you say, looking directly at the Older Sister.\n\n" +
                    "The room goes utterly still.\n\n" +
                    "She holds your gaze for a long moment.\n" +
                    "Then something shifts in her expression.")
            .build();

    Scene accuseOutcomeUncle = new Scene.Builder("AccuseOutcomeUncle",
            "You step forward. All eyes turn to you.\n\n" +
                    "\"It was you,\" you say, looking directly at the Uncle.\n\n" +
                    "The room goes utterly still.\n\n" +
                    "He holds your gaze for a long moment.\n" +
                    "Then something shifts in his expression.")
            .build();

    Scene accuseOutcomeCousin = new Scene.Builder("AccuseOutcomeCousin",
            "You step forward. All eyes turn to you.\n\n" +
                    "\"It was you,\" you say, looking directly at the Cousin.\n\n" +
                    "The room goes utterly still.\n\n" +
                    "He holds your gaze for a long moment.\n" +
                    "Then something shifts in his expression.")
            .build();

    Scene accuseOutcomeFamilyFriend = new Scene.Builder("AccuseOutcomeFamilyFriend",
            "You step forward. All eyes turn to you.\n\n" +
                    "\"It was you,\" you say, looking directly at the Family Friend.\n\n" +
                    "The room goes utterly still.\n\n" +
                    "They hold your gaze for a long moment.\n" +
                    "Then something shifts in their expression.")
            .build();

// These are loaded by the GUI after processGuess() resolves,
// based on endingType. One scene per endingType.

    Scene endingCorrectGuessEscape = new Scene.Builder("EndingCorrectGuessEscape",
            "The colour drains from their face.\n\n" +
                    "For a moment it looks like they might deny it.\n" +
                    "Then the evidence you have gathered closes around them\n" +
                    "like a trap they built themselves.\n\n" +
                    "Someone calls for help. Someone else is already at the door.\n\n" +
                    "You walk out into the cold night air.\n" +
                    "The house disappears behind you.\n\n" +
                    "The killer was: " + "[ revealed by GUI from getKillerReveal() ]\n\n" +
                    "You survived. You solved it.\n" +
                    "The truth cost something — but you made it out.")
            .build();

    Scene endingCorrectGuessTooLate = new Scene.Builder("EndingCorrectGuessTooLate",
            "You are right.\n\n" +
                    "You can see it in their eyes the moment you say it.\n" +
                    "But you said it too late.\n\n" +
                    "They are already moving.\n" +
                    "The others are too far away.\n" +
                    "You had the answer — you just ran out of time.\n\n" +
                    "The killer was: " + "[ revealed by GUI from getKillerReveal() ]\n\n" +
                    "You solved it. But it was not enough.")
            .build();

    Scene endingWrongGuess = new Scene.Builder("EndingWrongGuess",
            "The room erupts.\n\n" +
                    "The person you accused stares at you in disbelief — then in fury.\n" +
                    "The real killer watches from across the room, expression unreadable.\n\n" +
                    "In the chaos that follows, no one is watching the right person.\n\n" +
                    "The killer was: " + "[ revealed by GUI from getKillerReveal() ]\n\n" +
                    "You were wrong. The night does not end well.")
            .build();

    Scene endingEscapedUnsolved = new Scene.Builder("EndingEscapedUnsolved",
            "You slip out the back door.\n\n" +
                    "The cold air hits you and you do not look back.\n" +
                    "Somewhere behind you, in that house, the killer is still there.\n" +
                    "Still among them.\n\n" +
                    "You are alive. That is something.\n\n" +
                    "But the truth stays buried in that house tonight.")
            .build();

    Scene endingEveryoneDead = new Scene.Builder("EndingEveryoneDead",
            "The sitting room is silent.\n\n" +
                    "You look around at what remains.\n" +
                    "One by one, the night took them.\n\n" +
                    "You are the only one left standing.\n\n" +
                    "The killer was: " + "[ revealed by GUI from getKillerReveal() ]\n\n" +
                    "There is no one left to accuse.\n" +
                    "There is no one left to save.\n" +
                    "You survived — but there is nothing left to survive for.")
            .build();
}