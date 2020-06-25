package Models;

public abstract class Character {
    private String name;

    public Character(){ }

    public Character (String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
