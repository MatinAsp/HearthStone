package Models;

import Exceptions.GameOverException;
import Interfaces.Cloneable;
import Models.Cards.Card;
import Models.Cards.HeroPower;

public class Hero extends Character{
    private int hp,deckMax;
    private HeroPower heroPower;
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
}
