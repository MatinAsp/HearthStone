package Logic;

import Data.DataManager;
import Data.GameConstants;
import Models.Cards.Card;
import Models.Hero;
import Models.Player;

import java.util.ArrayList;
import java.util.Random;

public class PlayerFactory {
    static private PlayerFactory playerFactory = null;

    private PlayerFactory(){}

    static public PlayerFactory getInstance(){
        if(playerFactory == null) {
            playerFactory = new PlayerFactory();
        }
        return playerFactory;
    }

    public Player build(String username, String password) throws Exception {
        int id = (new Random()).nextInt(Integer.MAX_VALUE);
        int wallet = GameConstants.getInstance().getInteger("wallet");
        password = Integer.toString(password.hashCode());
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Card> defaultCards = dataManager.getDefaultCards();
        ArrayList<Hero> defaultHeroes = dataManager.getDefaultHeroes();
        Player player = new Player(username, password, id, wallet, defaultCards, defaultHeroes);
        player.createDeck("MageDeck", "Mage");
        player.setCurrentDeck("MageDeck");
        for(Card card: defaultCards){
            if(player.getDeck(player.getCurrentDeckName()).getHero().isForHero(card)){
                player.getDeck(player.getCurrentDeckName()).addCard(card);
            }
        }
        return player;
    }
}
