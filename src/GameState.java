import java.util.ArrayList;
import java.util.*;

public class GameState {
    private ArrayList<Character> characters;

    public GameState() {
    }

    public void assignRandomKiller(Character playerCharacter) {
        Random rand = new Random();
        Character killer;

        do {
            int index = rand.nextInt(characters.size());
            killer = characters.get(index);
        } while (killer == playerCharacter || killer.getName().equals("Little brother"));

        killer.setKiller(true);
        System.out.println("DEBUG: Killer is " + killer.getName());
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }
}
