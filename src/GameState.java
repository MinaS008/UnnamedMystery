import java.util.ArrayList;

public class GameState {
    private ArrayList<Character> characters;
    private Character playableCharacter;
    private Character killer;
    private Random random;

    public GameState(){
        characters = new ArrayList<>();
        random = new Random();
        
        Character mother = new Character("Mother", false);
        Character father = new Character("Father", false);
        Character olderSister = new Character("Older sister", false);
        Character littleBrother = new Character("Little brother", true);
        Character uncle = new Character("Uncle", false);
        Character cousin = new Character("Cousin", false);
        Character familyFriend = new Character("Family friend", false);

        characters.add(mother);
        characters.add(father);
        characters.add(uncle);
        characters.add(olderSister);
        characters.add(littleBrother);
        characters.add(cousin);
        characters.add(familyFriend);
    }

    public void setPlayableCharacter(Character player) {
        this.playableCharacter = player;
    }

    public void chooseKiller() {
        ArrayList<Character> possibleKillers = new ArrayList<>();
        for(Character c: characters) {
            if(!c.isChild() && c != playableCharacter) {
                possibleKillers.add(c);
            }
        }
        killer = possibleKillers.get(random.nextInt(possibleKillers.size()));
        killer.setKiller(true);

        System.out.println("DEBUG: Killer is: " + killer.getName());
    } 
}
