package Logic;

import Data.DataManager;
import Data.GameConstants;
import Models.Cards.Card;
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

    public Game build(Player player1, Player player2) throws Exception {
        Game game = new Game();
        setCompetitor(game.getCompetitor(0), player1);
        setCompetitor(game.getCompetitor(1), player2);
        Game.Competitor first = game.getCompetitor(game.getTurn());
        first.setFullMana(GameConstants.getInstance().getInteger("manaForStart"));
        first.setLeftMana(GameConstants.getInstance().getInteger("manaForStart"));
        return game;
    }

    private void setCompetitor(Game.Competitor competitor, Player player) throws Exception {
        competitor.setDeck(player.getDeck(player.getCurrentDeckName()));
        for(Card card: player.getDeck(player.getCurrentDeckName()).getCards()){
            competitor.getInDeckCards().add(DataManager.getInstance().getObject(Card.class, card.getName()));
        }
        for(int i = 0; i < Math.min(competitor.getDeck().getCards().size(), GameConstants.getInstance().getInteger("HandsFirstCardsNumber")); i++){
            competitor.putCardFromDeckToHand();
        }
        competitor.setHero(
                DataManager.getInstance().getObject(Hero.class, player.getDeck(player.getCurrentDeckName()).getHero().getName())
        );
    }
}
