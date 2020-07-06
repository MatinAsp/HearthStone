package Models.Cards;

import Interfaces.Cloneable;

public class Spell extends Card {
    public Spell(){}

    private Spell(Spell spell) {
        super(spell);
    }

    @Override
    public String toString() {
        return super.toString()+"\n";
    }

    @Override
    public Spell newOne() {
        return new Spell(this);
    }
}
