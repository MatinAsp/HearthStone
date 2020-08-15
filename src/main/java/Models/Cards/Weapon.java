package Models.Cards;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Weapon extends Card {
    @Column
    private int durability;
    @Column
    private int attack;
    @Column
    private boolean charge;
    public Weapon(){ }

    private Weapon(Weapon weapon) {
        super(weapon);
        durability = weapon.getDurability();
        attack = weapon.getAttack();
        charge = weapon.isCharge();
    }

//    @Override
//    public String toString() {
//        return super.toString()+" \u26CADurability:"+durability+" \u26CFAttack:"+attack+"\n";
//    }


    @Override
    public String toString() {
        return super.toString() + " Weapon{" +
                "durability=" + durability +
                ", attack=" + attack +
                ", charge=" + charge +
                '}';
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getAttack() {
        return attack;
    }

    @Override
    public Weapon newOne() {
        return new Weapon(this);
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }
}
