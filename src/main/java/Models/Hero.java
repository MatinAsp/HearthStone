package Models;

import Exceptions.GameOverException;
import Interfaces.Cloneable;
import Models.Cards.Card;
import Models.Cards.HeroPower;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Hero extends Character{
    @Column
    private int hp;
    @Column
    private int deckMax;
    @OneToOne
  //  @Cascade(CascadeType.ALL)
    private HeroPower heroPower;
    @Column
    private boolean divineShield;

    public Hero(){ }

    private Hero(Hero hero) {
        super(hero.getName());
        hp = hero.getHp();
        deckMax = hero.getDeckMax();
        heroPower = hero.getHeroPower().newOne();
    }

    public boolean isForHero(Card card){
        if(card.getHeroClass().equals("Neutral") || card.getHeroClass().equals(super.getName())){
            return true;
        }
        return false;
    }

    public int getDeckMax() {
        return deckMax;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp){
        this.hp = hp;
    }

    public HeroPower getHeroPower() {
        return heroPower;
    }

    @Override
    public Hero newOne() {
        return new Hero(this);
    }

    public boolean isDivineShield() {
        return divineShield;
    }

    public void setDivineShield(boolean divineShield) {
        this.divineShield = divineShield;
    }

    public void getDamage(int damage) throws GameOverException {
        if(divineShield){
            divineShield = false;
        }
        else {
            hp -= damage;
        }
        if(hp <= 0) throw new GameOverException();
    }

    public void setDeckMax(int deckMax) {
        this.deckMax = deckMax;
    }

    public void setHeroPower(HeroPower heroPower) {
        this.heroPower = heroPower;
    }

    @Override
    public String toString() {
        return super.toString() + " Hero{" +
                "hp=" + hp +
                ", deckMax=" + deckMax +
                ", heroPower=" + heroPower +
                ", divineShield=" + divineShield +
                '}';
    }
}
