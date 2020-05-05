package Graphics;

import Graphics.GraphicRender;
import Logic.PlayersManager;
import Models.Deck;
import Models.Passive;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DecksStatusGraphicsController {
    @FXML
    private Label name;
    @FXML
    private Label wins;
    @FXML
    private Label plays;
    @FXML
    private Label winsPercent;
    @FXML
    private Label hero;
    @FXML
    private Label manaAverage;
    @FXML
    private Label mostPlayedCard;
    @FXML
    private Label usingForBattle;
    @FXML
    private StackPane heroPlace;

    public void loadDeck(Deck deck) throws IOException {
        name.setText(deck.getName());
        wins.setText(Integer.toString(deck.getWinsNumber()));
        plays.setText(Integer.toString(deck.getPlaysNumber()));
        winsPercent.setText(Double.toString(deck.getWinsPercent()));
        hero.setText(deck.getHero().getName());
        manaAverage.setText(Double.toString(deck.getAverageMana()));
        mostPlayedCard.setText(deck.getMostPlayedCard());
        if (PlayersManager.getInstance().getCurrentPlayer().getCurrentDeckName().equals(deck.getName())){
            usingForBattle.setText("Yes");
        }
        else {
            usingForBattle.setText("No");
        }
        heroPlace.getChildren().add(GraphicRender.getInstance().buildHeroPlace(deck.getHero()));
    }

}
