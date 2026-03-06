public class Character {
    private String name;
    private int suspicionLevel;
    private boolean isKiller;
    private boolean isAlive;
    private boolean isChild;

    public Character(String name, boolean isChild){
        this.name = name;
        this.isChild = isChild;
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
    public boolean getIsChild(){
        return isChild;
}
