package Models.Cards;

import Interfaces.ActionHandler;
import Interfaces.Cloneable;
import Models.Character;

import java.util.ArrayList;

public class Minion extends Card {
    private int hp,attack;
    private boolean rush, charge, divineShield, taunt, stealth;
    private ArrayList<ActionHandler> actionHandlers = new ArrayList<>();
    public Minion(){ }

    private Minion(Minion minion) {
        super(minion);
        stealth = minion.isStealth();
        hp = minion.getHp();
        attack = minion.getAttack();
        rush = minion.isRush();
        charge = minion.isCharge();
        divineShield = minion.isDivineShield();
        taunt = minion.isTaunt();
    }

    public boolean isStealth() {
        return stealth;
    }

    public void setStealth(boolean stealth) {
        this.stealth = stealth;
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

    public boolean isRush() {
        return rush;
    }

    public void setRush(boolean rush) {
        this.rush = rush;
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public boolean isDivineShield() {
        return divineShield;
    }

    public void setDivineShield(boolean divineShield) {
        this.divineShield = divineShield;
    }

    public boolean isTaunt() {
        return taunt;
    }

    public void setTaunt(boolean taunt) {
        this.taunt = taunt;
    }

    public void getDamage(int damage){
        if(divineShield){
            divineShield = false;
        }
        else {
            hp -= damage;
        }
        runActions();
    }

    public void runActions(){
        for (ActionHandler actionHandler: actionHandlers){
            try {
                actionHandler.runAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addActionForDamage(ActionHandler actionHandler){
        actionHandlers.add(actionHandler);
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

}
