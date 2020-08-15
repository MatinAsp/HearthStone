package Models.Cards;

import javax.persistence.Entity;

@Entity
public class Spell extends Card {
    public Spell(){}

    private Spell(Spell spell) {
        super(spell);
    }

    @Override
    public String toString() {
        return super.toString()+" ";
    }

    @Override
    public Spell newOne() {
        return new Spell(this);
    }
}
