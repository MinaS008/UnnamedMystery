import java.util.ArrayList;

public class GameState {
    private ArrayList<Character> characters;

    public GameState(){
        characters = new ArrayList<>();
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
        characters.add(uncle);
        characters.add(cousin);
        characters.add(familyFriend);
    }
}
