package Logic;

import Models.Cards.Card;
import Models.Player;

import java.io.IOException;

public class Store {
    private static Store instanceStore = null;
    private Store() throws IOException { }

    static public Store getInstance() throws IOException {
        if(instanceStore == null){
            instanceStore = new Store();
        }
        return instanceStore;
    }

    public void buyCard(Card card) throws Exception {
        Player player =  PlayersManager.getInstance().getCurrentPlayer();
        if (player.getWallet() < card.getPrice()) throw new Exception("Don't have enough coin.");
        player.setWallet(player.getWallet()-card.getPrice());
        player.addToCards(card);
    }

    public void sellCard(Card card) throws IOException {
        Player player =  PlayersManager.getInstance().getCurrentPlayer();
        player.setWallet(player.getWallet()+card.getPrice());
        player.removeCard(card);
    }
}
