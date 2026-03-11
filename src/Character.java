public class Character {
    private NextStatementIsALie.characterNames name;
    private String displayName;
    private int suspicionLevel;
    private boolean alive;
    private boolean isKiller;

    public Character(NextStatementIsALie.characterNames name, String displayName, int startingSuspicion){
        this.name = name;
        this.displayName = displayName;
        this.suspicionLevel = startingSuspicion;
        this.alive = true;
        this.isKiller = false;
    }

    public int getSuspicionLevel(){
        return suspicionLevel;
    }

    public void increaseSuspicionLevel(int amount){
        suspicionLevel += amount;
    }

    public void decreaseSuspicionLevel(int amount){
        suspicionLevel = Math.max(0, suspicionLevel - amount);
    }

    public void setSuspicionLevel(int value){
        suspicionLevel = Math.max(0, value);
    }

    public boolean isAlive(){
        return alive;
    }

    public void setAlive(boolean alive){
        this.alive = alive;
    }

    public boolean isKiller(){
        return isKiller;
    }

    public void setKiller(boolean isKiller){
        this.isKiller = isKiller;
    }

    public NextStatementIsALie.characterNames getName(){
        return name;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String toString(){
        return displayName + " | Suspicion: " + suspicionLevel + " | Alive: " + alive + (isKiller ? " | *** KILLER *** |" : "");
    }
}
