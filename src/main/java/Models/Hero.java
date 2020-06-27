package Models;

import Interfaces.Cloneable;
import Models.Cards.Card;

public class Hero extends Character{
    private int hp,deckMax;
    private Card heroPower;

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

    public Card getHeroPower() {
        return heroPower;
    }

    @Override
    public Hero newOne() {
        return new Hero(this);
    }
}
