package Models;

import Interfaces.Cloneable;

public class Passive extends Character{
    private String description;

    private Passive(Passive passive){
        super(passive.getName());
        description = passive.getDescription();
    }

    public Passive() {}

    @Override
    public Passive newOne() {
        return new Passive(this);
    }

    public String getDescription() {
        return description;
    }
}
