public class Character {
    private String name;
    private int suspicionLevel;
    private boolean isKiller;
    private boolean isAlive;

    public Character(String name, boolean isKiller){
        this.name = name;
        this.isKiller = isKiller;
        this.suspicionLevel = 0;
        this.isAlive = true;
    }

    public String getName(){
        return name;
    }
    public int getSuspicionLevel(){
        return suspicionLevel;
    }
    public void increaseSuspicionLevel(int amount){
        suspicionLevel += amount;
    }

    public boolean getIsKiller(){
        return isKiller;
    }
    public void setAlive(boolean alive){
        this.isAlive = alive;
    }
}
