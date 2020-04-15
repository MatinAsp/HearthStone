package Logic.Models;

import Logic.Models.Cards.Card;

import java.util.ArrayList;

public class Hero {
    private String name;
    private int hp,deckMax;
    private ArrayList<Card> cards;

    public Hero(){ }

    public String getName(){
        return name;
    }

    public ArrayList<Card> getCards(){
        return cards;
    }

    public boolean isForHero(Card card){
        if(card.getHeroClass().equals("Neutral") || card.getHeroClass().equals(name)){
            return true;
        }
        return false;
    }

    public boolean haveCard(String card){
        for(Card heroCard: cards){
            if(heroCard.getName().equals(card)){
                return true;
            }
        }
        return false;
    }

    public void removeCard(String card){
        for(Card heroCard: cards){
            if(heroCard.getName().equals(card)){
                cards.remove(heroCard);
                break;
            }
        }
    }

    public boolean fullDeck(){
        if(cards.size() == deckMax){
            return true;
        }
        return false;
    }

    public int cardNum(String card){
        int sum = 0;
        for(Card heroCard: cards){
            if(heroCard.getName().equals(card)){
                sum++;
            }
        }
        return sum;
    }

    public void addCard(Card card){
        cards.add(card);
    }

}
