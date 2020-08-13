package Models;

import Interfaces.Cloneable;

import java.security.SecureRandom;

public class Character implements Cloneable {
    private String name;
    private int id = new SecureRandom().nextInt();

    public Character(){ }

    public Character (String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId(){
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Character newOne() {
        return new Character(this.name);
    }
}
