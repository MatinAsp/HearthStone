package Models.Cards;

public class Weapon extends Card {
    private int durability,attack;
    public Weapon(){ }

    @Override
    public String toString() {
        return super.toString()+" \u26CADurability:"+durability+" \u26CFAttack:"+attack+"\n";
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
}
