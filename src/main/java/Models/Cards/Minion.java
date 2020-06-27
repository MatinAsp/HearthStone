package Models.Cards;

import Interfaces.Cloneable;
import Models.Character;

public class Minion extends Card {
    private int hp,attack;
    public Minion(){ }

    private Minion(Minion minion) {
        super(minion);
        hp = minion.getHp();
        attack = minion.getAttack();
    }

    @Override
    public String toString() {
        return super.toString()+" \u2665HP:"+hp+" \u26CFAttack:"+attack+"\n";
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    @Override
    public Minion newOne() {
        return new Minion(this);
    }
}
