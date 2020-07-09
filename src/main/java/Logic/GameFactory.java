package Logic;

import Data.DataManager;
import Data.GameConstants;
import Models.Cards.Card;
import Models.Deck;
import Models.Hero;
import Models.Player;

public class GameFactory {
    private static GameFactory gameFactory = null;

    private GameFactory(){}

    public static GameFactory getInstance(){
        if(gameFactory == null){
            gameFactory = new GameFactory();
        }
        return gameFactory;
    }

    public Game build(Deck deck1, Deck deck2, boolean isWithBot) throws Exception {
        Game game = new Game(buildCompetitor(deck1), buildCompetitor(deck2), isWithBot);
        game.getCompetitor(0).setFullMana(GameConstants.getInstance().getInteger("manaForStart"));
        game.getCompetitor(0).setLeftMana(GameConstants.getInstance().getInteger("manaForStart"));
        game.getCompetitor(1).setFullMana(GameConstants.getInstance().getInteger("manaForStart") - 1);
        game.getCompetitor(1).setLeftMana(GameConstants.getInstance().getInteger("manaForStart") - 1);
        ActionRequest.setCurrentGame(game);
        return game;
    }

    private Competitor buildCompetitor(Deck deck) throws Exception {
        Competitor competitor = new Competitor();
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
}
