package Models;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Passive extends Character{
    @Column
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

    @Override
    public String toString() {
        return super.toString() + " Passive{" +
                "description='" + description + '\'' +
                '}';
    }
}
