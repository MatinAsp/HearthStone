package Logic;

import Data.DataManager;
import Data.GameConstants;
import Data.JacksonMapper;
import Data.JarLoader;
import Models.Cards.Card;
import Models.Deck;
import Models.Hero;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class GameFactory {
    private static GameFactory gameFactory = null;

    private GameFactory(){}

    public static GameFactory getInstance(){
        if(gameFactory == null){
            gameFactory = new GameFactory();
        }
        return gameFactory;
    }

    public Game build(String username1, String username2, Deck deck1, Deck deck2, boolean isWithBot) throws Exception {
        Game game = null;
        if(GameConstants.getInstance().getBoolean("isReflectionOn")){
            Constructor constructor = JarLoader.loadClass("ReflectedGame").getConstructor(Competitor.class, Competitor.class, boolean.class);
            game = (Game) constructor.newInstance(buildCompetitor(deck1, username1), buildCompetitor(deck2, username2), isWithBot);
        }
        else {
            game = new Game(buildCompetitor(deck1, username1), buildCompetitor(deck2, username2), isWithBot);
        }
        game.getCompetitor(0).setFullMana(GameConstants.getInstance().getInteger("manaForStart"));
        game.getCompetitor(0).setLeftMana(GameConstants.getInstance().getInteger("manaForStart"));
        game.getCompetitor(1).setFullMana(GameConstants.getInstance().getInteger("manaForStart") - 1);
        game.getCompetitor(1).setLeftMana(GameConstants.getInstance().getInteger("manaForStart") - 1);
        game.getCompetitor(0).setTime(GameConstants.getInstance().getInteger("timeToPlay"));
        game.getCompetitor(1).setTime(GameConstants.getInstance().getInteger("timeToPlay"));
        game.initialize();
        return game;
    }

    private Competitor buildCompetitor(Deck deck, String username) throws Exception {
        Competitor competitor = null;
        if(GameConstants.getInstance().getBoolean("isReflectionOn")){
            Constructor constructor = JarLoader.loadClass("ReflectedCompetitor").getConstructor(String.class);
            competitor = (Competitor) constructor.newInstance(username);
        }
        else {
            competitor = new Competitor(username);
        }
        competitor.setDeck(deck);
        for(Card card: deck.getCards()){
            competitor.getInDeckCards().add(DataManager.getInstance().getObject(Card.class, card.getName()));
        }
        for(int i = 0; i < Math.min(competitor.getDeck().getCards().size(), GameConstants.getInstance().getInteger("HandsFirstCardsNumber")); i++){
            competitor.drawCard();
        }
        competitor.setHero(
                DataManager.getInstance().getObject(Hero.class, deck.getHero().getName())
        );
        return competitor;
    }

    public Game getPrivateGame(String username, Game game){
        try {
            game = getSafeGame(game);
            game = JacksonMapper.getNetworkMapper().readValue(JacksonMapper.getNetworkMapper().writeValueAsString(game), Game.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        game.setWithBot(true);
        if(game.getCompetitorIndex(username) != 0) game.changeSide();
        makeCardsPrivate(game.getCompetitor(1).getInDeckCards());
        makeCardsPrivate(game.getCompetitor(1).getInHandCards());
        return game;
    }

    private void makeCardsPrivate(ArrayList<Card> cards){
        int cnt = cards.size();
        cards.clear();
        while (cnt-- > 0){
            cards.add(new Card());
        }
    }

    public Game getSafeGame(Game game) {
        return new Game(game);
    }
}
