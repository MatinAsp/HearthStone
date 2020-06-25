package Models.Cards;

public class Minion extends Card {
    private int hp,attack;
    public Minion(){ }

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

}
