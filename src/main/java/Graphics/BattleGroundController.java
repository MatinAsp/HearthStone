package Graphics;

import Data.*;
import Exceptions.GameOverException;
import Graphics.GraphicRender;
import Log.LogCenter;
import Logic.Game;
import Models.Cards.Card;
import Models.Cards.Minion;
import Logic.PlayersManager;
import Models.Passive;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

public class BattleGroundController implements Initializable {
    @FXML
    private StackPane root;
    @FXML
    private Pane rootPane;
    @FXML
    private HBox battleGround1;
    @FXML
    private HBox battleGround2;
    @FXML
    private Label manaText1;
    @FXML
    private Label manaText2;
    @FXML
    private HBox manaBar;
    @FXML
    private Pane hand1;
    @FXML
    private Pane hand2;
    @FXML
    private Label cardsNumberLabel1;
    @FXML
    private Label cardsNumberLabel2;
    @FXML
    private StackPane heroPowerPlace1;
    @FXML
    private StackPane heroPowerPlace2;
    @FXML
    private ImageView arena;
    @FXML
    private GridPane gameLogGridPane;

    private Pane hero1,hero2;

    private Game game = null;

    @FXML
    private void exit(){
        try {
            PlayersManager.getInstance().getCurrentPlayer().saveData();
            LogCenter.getInstance().getLogger().info("exit");
        } catch (Exception ex) {
            System.out.println("exit on login page.");;
        }
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            renderPassives();
            arena.setImage(AssetManager.getInstance().getBattleGround(GameSettings.getInstance().getBattleGroundArena()));
        } catch (IOException e) {
            LogCenter.getInstance().getLogger().error(e);
            e.printStackTrace();
        }
    }

    public void setGame(Game game){
        this.game = game;
    }

    public void gameRender() throws IOException {
        rootPane.getChildren().removeAll(hero1, hero2);
        GraphicRender graphicRender = GraphicRender.getInstance();
        Game.Competitor competitor1 = game.getCompetitor(0), competitor2 = game.getCompetitor(1);
        hero1 = graphicRender.buildHeroPlace(competitor1.getHero());
        hero1.setLayoutX(GameConstants.getInstance().getInteger("player1HeroPlaceX"));
        hero1.setLayoutY(GameConstants.getInstance().getInteger("player1HeroPlaceY"));
        rootPane.getChildren().add(hero1);
        hero1.toBack();
        hero2 = graphicRender.buildHeroPlace(competitor2.getHero());
        hero2.setLayoutX(GameConstants.getInstance().getInteger("player2HeroPlaceX"));
        hero2.setLayoutY(GameConstants.getInstance().getInteger("player2HeroPlaceY"));
        rootPane.getChildren().add(hero2);
        hero2.toBack();
        renderHand(competitor1.getInHandCards(), hand1, true);
        renderHand(competitor2.getInHandCards(), hand2, false);
        renderBattleGround(competitor1.getOnBoardCards(), battleGround1);
        renderBattleGround(competitor2.getOnBoardCards(), battleGround2);
        manaText1.setText(competitor1.getLeftMana()+"/"+competitor1.getFullMana());
        manaText2.setText(competitor2.getLeftMana()+"/"+competitor2.getFullMana());
        renderManaBar(competitor1.getLeftMana(), competitor1.getFullMana());
        cardsNumberLabel1.setText(getCardsNumberString(competitor1));
        cardsNumberLabel2.setText(getCardsNumberString(competitor2));
        heroPowerPlace1.getChildren().clear();
        heroPowerPlace2.getChildren().clear();
        heroPowerPlace1.getChildren().add(
                GraphicRender.getInstance().buildHeroPower(competitor1.getHero().getHeroPower())
        );
        heroPowerPlace2.getChildren().add(
                GraphicRender.getInstance().buildHeroPower(competitor2.getHero().getHeroPower())
        );
    }

    private String getCardsNumberString(Game.Competitor competitor){
        return competitor.getInDeckCards().size() + "\n/\n" + competitor.getHero().getDeckMax();
    }

    private void renderHand(ArrayList<Card> cards, Pane hand, boolean isForOwn) throws IOException {
        hand.getChildren().clear();
        int counter = 0;
        for(Card card: cards){
            Pane graphicCard = GraphicRender.getInstance().buildCard(card, false, false, !isForOwn);
            if(isForOwn){
                handCardSetAction(graphicCard, card);
            }
            if(cards.size() == 1) graphicCard.setLayoutX(0);
            else graphicCard.setLayoutX(counter*((hand.getPrefWidth()-graphicCard.getPrefWidth())/(cards.size()-1)));
            hand.getChildren().add(graphicCard);
            counter++;
        }
    }

    private void handCardSetAction(Parent graphicCard, Card card){
        graphicCard.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    graphicCard.setLayoutY(
                            graphicCard.getLayoutY()-GameConstants.getInstance().getInteger("liftUpCardInHad")
                    );
                } catch (IOException e) {
                    LogCenter.getInstance().getLogger().error(e);
                    e.printStackTrace();
                }
                graphicCard.toFront();
            }
        });
        graphicCard.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    gameRender();
                } catch (IOException e) {
                    LogCenter.getInstance().getLogger().error(e);
                    e.printStackTrace();
                }
            }
        });
        final double[] mousePosition = {0,0};
        final double[] mouseFirstPosition = {0,0};
        graphicCard.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                LogCenter.getInstance().getLogger().info("drag_detected");
                mousePosition[0] = event.getSceneX();
                mousePosition[1] = event.getSceneY();
                mouseFirstPosition[0] = event.getSceneX();
                mouseFirstPosition[1] = event.getSceneY();
            }
        });
        graphicCard.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                graphicCard.setLayoutX(graphicCard.getLayoutX() + event.getSceneX() - mousePosition[0]);
                graphicCard.setLayoutY(graphicCard.getLayoutY() + event.getSceneY() - mousePosition[1]);
                mousePosition[0] = event.getSceneX();
                mousePosition[1] = event.getSceneY();
            }
        });
        graphicCard.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                LogCenter.getInstance().getLogger().info("drag_ended");
                boolean check = false;
                try {
                    int maxYForPlayCard = GameConstants.getInstance().getInteger("maxYForPlayCard");
                    if (graphicCard.getParent().getLayoutY() + graphicCard.getLayoutY() < maxYForPlayCard){
                        game.playCard(card);
                        LogCenter.getInstance().getLogger().info("play_"+card.getType());
                        addGameLog("play_"+card.getType());
                    }
                } catch (Exception e) {
                    LogCenter.getInstance().getLogger().error(e);
                    check = true;
                    e.printStackTrace();
                }
                try {
                    if(!check) cardPlaySound(card);
                } catch (IOException e) {
                    LogCenter.getInstance().getLogger().error(e);
                    e.printStackTrace();
                }
                graphicCard.setLayoutX(graphicCard.getLayoutX() + event.getSceneX() - mouseFirstPosition[0]);
                graphicCard.setLayoutY(graphicCard.getLayoutY() + event.getSceneY() - mouseFirstPosition[1]);
            }
        });

    }

    private void cardPlaySound(Card card) throws IOException {
        MediaManager mediaManager = MediaManager.getInstance();
        if (card instanceof Minion){
            mediaManager.playMedia(GameConstants.getInstance().getString("minionPlacingSoundTrack"), 1);
        }
        else {
            mediaManager.playMedia(GameConstants.getInstance().getString("putCardFromHandSoundTrack"), 1);
        }
    }

    private void renderBattleGround(ArrayList<Card> cards, HBox battleGround) throws IOException {
        battleGround.getChildren().clear();
        for(Card card: cards){
            battleGround.getChildren().add(GraphicRender.getInstance().buildBattleGroundMinion((Minion) card));
        }
    }

    private void renderManaBar(int leftMana, int fullMana) throws IOException {
        manaBar.getChildren().clear();
        for(int i = 0; i < leftMana; i++){
            ImageView imageView = new ImageView(AssetManager.getInstance().getImage("mana"));
            imageView.setFitWidth(manaBar.getPrefHeight());
            imageView.setFitHeight(manaBar.getPrefHeight());
            manaBar.getChildren().add(imageView);
        }
        for(int i = 0; i < fullMana - leftMana; i++){
            ImageView imageView = new ImageView(AssetManager.getInstance().getImage("darkMana"));
            imageView.setFitWidth(manaBar.getHeight());
            imageView.setFitHeight(manaBar.getHeight());
            manaBar.getChildren().add(imageView);
        }
    }

    @FXML
    private StackPane alertBox;
    @FXML
    private Label alertMessage;
    @FXML
    private Button endTurnButton;
    @FXML
    private void endTurn() throws IOException {
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("endTurnSoundTrack"), 1);
        changeTurn();
        changeTurn();
    }

    private void changeTurn() throws IOException {
        LogCenter.getInstance().getLogger().info("end_turn_player_"+(game.getTurn()+1));
        addGameLog("end_turn_player_"+(game.getTurn()+1));
        try {
            game.changeTurn();
            int turn = game.getTurn();
            Game.Competitor competitor = game.getCompetitor(turn);
            boolean isForOwn = (turn == 0)? true:false;
            putCardToHandAnimation(competitor.getInHandCards().get(competitor.getInHandCards().size()-1), isForOwn);
        } catch (GameOverException e) {
            LogCenter.getInstance().getLogger().info("game_over");
            addGameLog("game_over");
            alertBox.setVisible(true);
            if(game.getWinner() == 0) alertMessage.setText("You Win.");
            else alertMessage.setText("You Lose.");
        }catch (InterruptedException e){
            LogCenter.getInstance().getLogger().error(e);
        }
        if (game.getTurn() == 0) endTurnButton.setDisable(false);
        else endTurnButton.setDisable(true);
    }

    private void putCardToHandAnimation(Card card, boolean isForOwn) throws IOException, InterruptedException {
        Pane cardPane = GraphicRender.getInstance().buildCard(card, false, false, !isForOwn);
        rootPane.getChildren().add(cardPane);
        int duration = 2;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(duration), cardPane);
        double fromX, fromY, toX, toY;
        Pane hand ;
        if(isForOwn){
            fromX = cardsNumberLabel1.getLayoutX();
            fromY = cardsNumberLabel1.getLayoutY();
            toX = hand1.getLayoutX();
            toY = hand1.getLayoutY();
            hand = hand1;
        }
        else {
            fromX = cardsNumberLabel2.getLayoutX();
            fromY = cardsNumberLabel2.getLayoutY();
            toX = hand2.getLayoutX();
            toY = hand2.getLayoutY();
            hand = hand2;
        }
        hand.setDisable(true);
        translateTransition.setFromX(fromX);
        translateTransition.setFromY(fromY);
        translateTransition.setToX(toX);
        translateTransition.setToY(toY);
        translateTransition.setCycleCount(1);
        translateTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                rootPane.getChildren().remove(translateTransition.getNode());
                try {
                    gameRender();
                    hand.setDisable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        translateTransition.play();
    }

    @FXML
    private void backToMenu() throws IOException {
        LogCenter.getInstance().getLogger().info("back_to_menu");
        MediaManager.getInstance().stopMedia(GameConstants.getInstance().getString("battleGroundSoundTrack"));
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("menuSoundTrack"), -1);
        root.setVisible(false);
    }

    @FXML
    private StackPane passiveSelectionPane;
    @FXML
    private HBox passiveSelectionPlace;

    public void renderPassives() throws IOException {
        passiveSelectionPane.setVisible(true);
        ArrayList<Passive> passives = DataManager.getInstance().getAllPassives();
        int passivesNumber = GameConstants.getInstance().getInteger("passivesOnGamesStar");
        while (passivesNumber-- > 0){
            Random random = new Random();
            Passive passive = passives.get(random.nextInt(passives.size()));
            Pane passiveGraphics = GraphicRender.getInstance().buildPassive(passive);
            passiveGraphics.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    LogCenter.getInstance().getLogger().info("passive_selected");
                    addGameLog("passive_selected");
                    passiveSelectionPane.setVisible(false);
                }
            });
            passiveSelectionPlace.getChildren().add(passiveGraphics);
            passives.remove(passive);
        }
    }

    private void addGameLog(String log){
        for (Node node: gameLogGridPane.getChildren()){
            GridPane.setConstraints(node, 0, GridPane.getRowIndex(node)+1);
        }
        Label logLabel = new Label(log);
        logLabel.setTextFill(Color.WHITE);
        GridPane.setConstraints(logLabel, 0, 0);
        gameLogGridPane.getChildren().add(logLabel);
    }
}
