import java.util.*;

public class Clue {
  public static class KillerConditionalEffect {
    private NextStatementIsALie.characterNames killerName;
    private NextStatementIsALie.characterNames targetCharacter;
    private int suspicionLevel;
    private int dangerLevel;

    public KillerConditionalEffect(NextStatementIsALie.characterNames killerName, NextStatementIsALie.characterNames targetCharacter, int suspicionLevel, int dangerLevel) {
      this.killerName = killerName;
      this.targetCharacter = targetCharacter;
      this.suspicionLevel = suspicionLevel;
      this.dangerLevel = dangerLevel;
    }

    public NextStatementIsALie.characterNames getKillerName() {
      return killerName;
    }

    public NextStatementIsALie.characterNames getTargetCharacter() {
      return targetCharacter;
    }

    public int getSuspicionLevel() {
      return suspicionLevel;
    }

    public int getDangerLevel() {
      return dangerLevel;
    }
  }
  private static Map<String, Clue> registry = new LinkedHashMap<>();
    static {
        //Older Sister clues
              register("bloodyKnife",
                "A kitchen knife smeared with dark residue. Someone was in here.",
                NextStatementIsALie.characterNames.mother, 2);
        register("pantryNote",
                "A crumpled note: 'No wound was carved -the silent killer made it rise alone.' Posion.",
                NextStatementIsALie.characterNames.father, 2);
        register("brokenWineGlass",
                "A shattered wine glass in the cellar. The uncle never lets anyone touch his collection.",
                NextStatementIsALie.characterNames.uncle, 2);
        register("almondScent",
                "A bitter almond smell from a forced-open box. Granfather was allergic to almonds.",
                NextStatementIsALie.characterNames.father, 2);
        register("footprintPhoto",
                "A muddy footprint near a hidden tunnel. SOmeone used a secret exit - or entrance.",
                NextStatementIsALie.characterNames.uncle, 2);
        register("inconsistencyNote",
                "The dirt on the floor is new. But it hasn't rained recently, so there is no dirt oustide.",
                NextStatementIsALie.characterNames.uncle, 2);
        register("revisedWIll",
                "The handwritten amendment was added this morning. Someone changed it... It is very suspiciouss",
                NextStatementIsALie.characterNames.father, 3);
        register("tornEnvelope",
                "A torn envelope on the grandfather's desk. Something was sent - or received - in secret.",
                NextStatementIsALie.characterNames.cousin, 1);
        register("allergyReport",
                "AN official allergy report confirms a deathly reaction to almnonds.",
                NextStatementIsALie.characterNames.mother, 1);
        register("hiddenCash",
                "A hidden stack of money behind the nursery crib. The family friend was near here earlier.",
                NextStatementIsALie.characterNames.familyFriend, 2);
        register("wardrobePoison",
                "Poison hidden inside the nursery wardrobe. The cousin was seen opening it earlier.",
                NextStatementIsALie.characterNames.cousin, 3);
        register("burnedDocument",
                "Piece up the document together and see that the name is bruned away",
                NextStatementIsALie.characterNames.father, 1);
        register("almondResidue",
                "A velvet pouch hidden in the chimney containing almond shavings. Poison was staged here.",
                NextStatementIsALie.characterNames.mother, 3);
      
        //Family Friend clues
        register("handwrittenNote",
                "A folded napkin note: 'He knew the truth. Now he doesn't breath.' Red-stained",
                NextStatementIsALie.characterNames.olderSister, 2);

        register("grandfathersWill",
                "The grandfather's will - your name has been added as a beneficiary. Why?",
                NextStatementIsALie.characterNames.father, 2);

        register("noteNextToWill",
                "'Inheritance looks good on you. Shame it comes with blood.'",
                NextStatementIsALie.characterNames.olderSister, 1);

        register("markedFamilyPhoto",
                "The cousin said: 'He found out what I was doing.' The victim was planning to expose someone.",
                NextStatementIsALie.characterNames.cousin,2);

        register("laptopDraftEmail",
                "A drafted email: 'The Family Friend won't get in the way.' Never sent - or staged?",
                NextStatementIsALie.characterNames.uncle, 2);

        register("loginRecord842PM",
                "Laptop login at 8:42 PM. Cross-reference with alibis - this places someone at the desk",
                NextStatementIsALie.characterNames.uncle, 2);

        register("handwrittenCOnfession",
                "A confession letter with the aunt's signature - but the handwriting doesn't entierly match",
                NextStatementIsALie.characterNames.uncle, 2);

        register("fakeSignatureObservation",
                "The signature on the confession is practiced, forced. Someone tried to copy the aunt's hand.",
                NextStatementIsALie.characterNames.uncle, 2);

        register("stageOneVial",
                "A vial labeled 'Stage One' - almond-scented but not the weapon. This was a two-part plan.",
                NextStatementIsALie.characterNames.cousin, 3);

        register("blackCloakHairSample",
                "A black cloak with a strand of dark hair and a torn sleeve. Someone wore this as a disguise.",
                NextStatementIsALie.characterNames.olderSister, 2);

        register("arguingPhoto",
                "A photo of the vitim mid-argument behind a closed door. The other person is partially visible.",
                NextStatementIsALie.characterNames.father, 2);

        register("allLetters",
                "A bundle of threat letters to the victim, signed with initials M, F, C, U, O.",
                NextStatementIsALie.characterNames.father, 1);

        register("threatLetterM",
                "'He saw too much. Tonight ends it all. - M' Could be the Mother - pr a planted decoy.",
                NextStatementIsALie.characterNames.mother, 2);

        register("tamperedMedication",
                "The victim's beta blockers have been swapped. The pills are the wrong colour.",
                NextStatementIsALie.characterNames.mother, 3);

        register("tamperedMedicationHidden",
                "The tampered medication — pills switched. You've pocketed the evidence.",
                NextStatementIsALie.characterNames.mother, 3);

        register("almondscentTowel",
                "A towel smelling of bitter almonds with foundation smeared in the corner. Not your shade.",
                NextStatementIsALie.characterNames.mother, 2);

        register("towelEvidence",
                "The almond-scented towel — someone washed their hands after handling something toxic.",
                NextStatementIsALie.characterNames.mother, 2);

        register("whisperObservation",
                "You heard only one set of footsteps — but two voices. Someone was performing.",
                NextStatementIsALie.characterNames.olderSister, 1);

        register("vomitResidueObservation",
                "Vomit residue near the tub, almost wiped. The victim was moved after the poisoning.",
                NextStatementIsALie.characterNames.father, 2);

        register("cufflink",
                "A cufflink with engraved initials found in the bathroom residue.",
                NextStatementIsALie.characterNames.father, 2);

        register("cufflinkKept",
                "The cufflink — engraved initials. If it belongs to the killer, they will notice it missing.",
                NextStatementIsALie.characterNames.father, 2);

        register("replacedCarpetObservation",
                "The hallway carpet has been recently replaced. Fresh fibres over scrape marks. Premeditated.",
                NextStatementIsALie.characterNames.uncle, 2);

        register("amendmentOverheard",
                "Overheard: 'If they find the amendment, we're finished.' The will and bathroom now connect.",
                NextStatementIsALie.characterNames.father, 3);
    }
  private static void register(String clueID, String description, NextStatementIsALie.characterNames implicatedCharacter, int suspicionLevel) {
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
