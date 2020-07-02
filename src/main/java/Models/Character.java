package Models;

import Interfaces.Cloneable;

public abstract class Character implements Cloneable {
    private String name;

    public Character(){ }

    public Character (String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
