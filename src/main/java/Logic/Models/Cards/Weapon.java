package Logic.Models.Cards;

public class Weapon extends Card {
    private int durability,attack;
    public Weapon(){ }

    @Override
    public String toString() {
        return super.toString()+" \u26CADurability:"+durability+" \u26CFAttack:"+attack+"\n";
    }
}
