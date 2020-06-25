package Models;

public class Passive extends Character {
    private String description;

    public Passive() {}

    @Override
    public Passive clone() {
        return new Passive(this);
    }
     private Passive(Passive passive){
        description = passive.getDescription();
     }

    public String getDescription() {
        return description;
    }
}
