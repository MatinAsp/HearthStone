package Graphics;

import Models.Cards.Card;
import Models.Player;

import java.io.IOException;
import java.util.ArrayList;

public class Filterer {
    static private Filterer filterer = null;
    private int manaFilter;
    private String searchFilter, currentHero;
    private boolean havingCard, notHavingCard;
    private Filterer() throws IOException {
        reset();
    }

    public void reset(){
        manaFilter = -1;
        currentHero = "Neutral";
        searchFilter = "";
        havingCard = true;
        notHavingCard = true;
    }

    public static Filterer getInstance() throws IOException {
        if(filterer == null){
            filterer = new Filterer();
        }
        return filterer;
    }

    public void setManaFilter(int manaFilter) {
        this.manaFilter = manaFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public void setHavingCard(boolean havingCard) {
        this.havingCard = havingCard;
    }

    public void setNotHavingCard(boolean notHavingCard) {
        this.notHavingCard = notHavingCard;
    }

    public ArrayList<Card> filterCards(ArrayList<Card> cards, Player player){
        ArrayList<Card> filteredCards = new ArrayList<>();
        for(Card card: cards){
            if(
                    (manaFilter == -1 || manaFilter == card.getMana()) &&
                    (card.getName().toLowerCase()).contains(searchFilter.toLowerCase()) &&
                    (currentHero.equals("All") || currentHero.equals(card.getHeroClass()))
            ){
                if(player.haveCard(card.getName()) && havingCard){
                    filteredCards.add(card);
                }
                if(!player.haveCard(card.getName()) && notHavingCard) {
                    filteredCards.add(card);
                }
            }
        }
        return filteredCards;
    }

    public void setCurrentHero(String currentHero) {
        this.currentHero = currentHero;
    }
}
