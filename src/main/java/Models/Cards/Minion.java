package Models.Cards;

import Interfaces.ActionHandler;
import Interfaces.Cloneable;
import Models.Character;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;

@Entity
@JsonIgnoreProperties(value = {"actionHandlers"})
public class Minion extends Card {
    @Column
    private int hp;
    @Column
    private int attack;
    @Column
    private boolean rush;
    @Column
    private boolean charge;
    @Column
    private boolean divineShield;
    @Column
    private boolean taunt;
    @Column
    private boolean stealth;
    @Transient
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

//    @Override
//    public String toString() {
//        return super.toString()+" \u2665HP:"+hp+" \u26CFAttack:"+attack+"\n";
//    }


    @Override
    public String toString() {
        return super.toString()+" Minion{" +
                "hp=" + hp +
                ", attack=" + attack +
                ", rush=" + rush +
                ", charge=" + charge +
                ", divineShield=" + divineShield +
                ", taunt=" + taunt +
                ", stealth=" + stealth +
                ", actionHandlers=" + actionHandlers +
                '}';
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
