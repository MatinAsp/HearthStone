package Logic;

import Data.DataManager;
import Data.GameConstants;
import Models.Cards.Card;
import Models.Deck;
import Models.Hero;
import Models.Player;
import com.google.gson.Gson;

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
        Game game = new Game(buildCompetitor(deck1, username1), buildCompetitor(deck2, username2), isWithBot);
        game.getCompetitor(0).setFullMana(GameConstants.getInstance().getInteger("manaForStart"));
        game.getCompetitor(0).setLeftMana(GameConstants.getInstance().getInteger("manaForStart"));
        game.getCompetitor(1).setFullMana(GameConstants.getInstance().getInteger("manaForStart") - 1);
        game.getCompetitor(1).setLeftMana(GameConstants.getInstance().getInteger("manaForStart") - 1);
        ActionRequest.setCurrentGame(game);
        return game;
    }

    private Competitor buildCompetitor(Deck deck, String username) throws Exception {
        Competitor competitor = new Competitor(username);
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

    public Game getPrivateGame(Competitor competitor1, Competitor competitor2){
        Gson gson = new Gson();
        competitor1 = gson.fromJson(gson.toJson(competitor1),Competitor.class);
        competitor2 = gson.fromJson(gson.toJson(competitor2),Competitor.class);
        competitor2.setDeck(null);
        makeCardsPrivate(competitor2.getInDeckCards());
        makeCardsPrivate(competitor2.getInHandCards());
        return new Game(competitor1, competitor2, true);
    }



    private void makeCardsPrivate(ArrayList<Card> cards){
        int cnt = cards.size();
        cards.clear();
        while (cnt-- > 0){
            cards.add(new Card());
        }
    }
}
