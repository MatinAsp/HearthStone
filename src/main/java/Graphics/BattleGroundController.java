package Graphics;

import Data.*;
import Exceptions.EmptyDeckException;
import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Exceptions.SelectionNeededException;
import Interfaces.ActionHandler;
import Log.LogCenter;
import Logic.ActionRequest;
import Logic.Game;
import Models.Cards.Card;
import Models.Cards.Minion;
import Logic.PlayersManager;
import Logic.Competitor;
import Models.Character;
import Models.InfoPack;
import Models.Passive;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    private HBox[] battleGround = new HBox[2];
    @FXML
    private StackPane showCard1;
    @FXML
    private StackPane showCard2;
    private StackPane[] showCard = new StackPane[2];
    @FXML
    private Label manaText1;
    @FXML
    private Label manaText2;
    private Label[] manaText = new Label[2];
    @FXML
    private HBox manaBar;
    @FXML
    private Pane hand1;
    @FXML
    private Pane hand2;
    private Pane[] hand = new Pane[2];
    @FXML
    private Label cardsNumberLabel1;
    @FXML
    private Label cardsNumberLabel2;
    private Label[] cardsNumberLabel = new Label[2];
    @FXML
    private StackPane heroPowerPlace1;
    @FXML
    private StackPane heroPowerPlace2;
    private StackPane[] heroPowerPlace = new StackPane[2];
    @FXML
    private StackPane heroWeapon1;
    @FXML
    private StackPane heroWeapon2;
    private StackPane[] heroWeapon = new StackPane[2];
    @FXML
    private ImageView arena;
    @FXML
    private GridPane gameLogGridPane;
    private Parent selected = null;
    private Pane[] hero = new Pane[2];
    private Game game = null;
    private ArrayList<InfoPack> infoPacks = new ArrayList<>();
    Object lock = new Object();

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
        renderPassives(0);
        battleGround[0] = battleGround1;
        battleGround[1] = battleGround2;
        showCard[0] = showCard1;
        showCard[1] = showCard2;
        hand[0] = hand1;
        hand[1] = hand2;
        manaText[0] = manaText1;
        manaText[1] = manaText2;
        cardsNumberLabel[0] = cardsNumberLabel1;
        cardsNumberLabel[1] = cardsNumberLabel2;
        heroPowerPlace[0] = heroPowerPlace1;
        heroPowerPlace[1] = heroPowerPlace2;
        heroWeapon[0] = heroWeapon1;
        heroWeapon[1] = heroWeapon2;
        arena.setImage(AssetManager.getInstance().getImage(GameSettings.getInstance().getBattleGroundArena()));
    }

    public void setGame(Game game){
        this.game = game;
    }

    public synchronized void gameRender() {
        GraphicRender graphicRender = GraphicRender.getInstance();
        for(int i = 0; i < 2; i++){
            rootPane.getChildren().removeAll(hero[i]);
            Competitor competitor = game.getCompetitor(i);
            hero[i] = graphicRender.buildHeroPlace(competitor.getHero());
            hero[i].setLayoutX(GameConstants.getInstance().getInteger("player"+(i+1)+"HeroPlaceX"));
            hero[i].setLayoutY(GameConstants.getInstance().getInteger("player"+(i+1)+"HeroPlaceY"));
            rootPane.getChildren().add(hero[i]);
            hero[i].toBack();
            setForPerformAction(competitor.getHero(), i, true, hero[i]);
            renderHand(competitor.getInHandCards(), hand[i], ((i == 0) ? true : false));
            renderBattleGround(competitor.getOnBoardCards(), battleGround[i], i);
            manaText[i].setText(competitor.getLeftMana()+"/"+competitor.getFullMana());
            if(i == 0)
                renderManaBar(competitor.getLeftMana(), competitor.getFullMana());
            cardsNumberLabel[i].setText(getCardsNumberString(competitor));
            renderHeroPower(competitor, i);
            renderHeroWeapon(competitor, i);
        }
    }

    private void renderHeroWeapon(Competitor competitor, int side) {
        heroWeapon[side].getChildren().clear();
        if(competitor.getHeroWeapon() != null){
            Parent parent =  GraphicRender.getInstance().buildHeroWeapon(competitor.getHeroWeapon());
            setForPerformAction(competitor.getHeroWeapon(),side, true, parent);
            heroWeapon[side].getChildren().add(parent);
        }
    }

    private void renderHeroPower(Competitor competitor, int side) {
        heroPowerPlace[side].getChildren().clear();
        Parent parent =  GraphicRender.getInstance().buildHeroPower(competitor.getHero().getHeroPower());
        setForPerformAction(competitor.getHero().getHeroPower(),side, true, parent);
        heroPowerPlace[side].getChildren().add(parent);
    }

    private String getCardsNumberString(Competitor competitor){
        return competitor.getInDeckCards().size() + "\n/\n" + competitor.getHero().getDeckMax();
    }

    private void renderHand(ArrayList<Card> cards, Pane hand, boolean isForOwn) {
        hand.getChildren().clear();
        int counter = 0;
        for(Card card: cards){
            Pane graphicCard = GraphicRender.getInstance().buildCard(card, false, false, (!isForOwn && game.isWithBot()));
            hand.getChildren().add(graphicCard);
            if(isForOwn || !game.isWithBot()){
                handCardSetAction(graphicCard, card, isForOwn ? 0 : 1);
            }
            if(cards.size() == 1) graphicCard.setLayoutX(0);
            else graphicCard.setLayoutX(counter*((hand.getPrefWidth()-graphicCard.getPrefWidth())/(cards.size()-1)));
            counter++;
        }
    }

    private void handCardSetAction(Parent graphicCard, Card card, int side){
        if(side == 1) graphicCard.setRotate(180);
        graphicCard.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                graphicCard.setRotate(0);
                graphicCard.setLayoutY(
                        graphicCard.getLayoutY()+GameConstants.getInstance().getInteger("liftUpCardInHand"+side)
                );
                graphicCard.toFront();
            }
        });
        int cnt = hand[side].getChildren().indexOf(graphicCard);
        graphicCard.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                synchronized (lock){
                    if(hand[side].getChildren().contains(graphicCard)){
                        int tmp = hand[side].getChildren().size() - cnt - 1 ;
                        while (tmp-- > 0){
                            hand[side].getChildren().get(cnt).toFront();
                        }
                        graphicCard.setLayoutY(
                                graphicCard.getLayoutY() - GameConstants.getInstance().getInteger("liftUpCardInHand"+side)
                        );
                        if (side == 1) graphicCard.setRotate(180);
                    }
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
                synchronized (lock){
                    LogCenter.getInstance().getLogger().info("drag_ended");
                    double y = graphicCard.getLayoutY();
                    double x = graphicCard.getLayoutX();
                    graphicCard.setLayoutX(graphicCard.getLayoutX() - event.getSceneX() + mouseFirstPosition[0]);
                    graphicCard.setLayoutY(graphicCard.getLayoutY() - event.getSceneY() + mouseFirstPosition[1]);
                    try {
                        int maxYForPlayCard = GameConstants.getInstance().getInteger("maxYForPlayCard");
                        int minYForPlayCard = GameConstants.getInstance().getInteger("minYForPlayCard");
                        if ((side == 0 && graphicCard.getParent().getLayoutY() + y < maxYForPlayCard) ||
                                (side == 1 && graphicCard.getParent().getLayoutY() + y + ((Pane) graphicCard).getHeight() > minYForPlayCard)){
                            x += graphicCard.getParent().getLayoutX();
                            int cnt = -1;
                            double groundX = battleGround[side].getLayoutX();
                            int i = 0;
                            for(Node node: battleGround[side].getChildren()){
                                if(x < node.getLayoutX() + groundX){
                                    cnt = i;
                                    break;
                                }
                                i++;
                            }
                            performAction(card, side, false,graphicCard, cnt);
                            LogCenter.getInstance().getLogger().info("play_"+card.getType());
                            addGameLog("play_"+card.getType());
                        }
                    } catch (Exception e) {
                        LogCenter.getInstance().getLogger().error(e);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void renderBattleGround(ArrayList<Minion> cards, HBox battleGround, int side) {
        battleGround.getChildren().clear();
        for(Card card: cards){
            Parent parent = GraphicRender.getInstance().buildBattleGroundMinion((Minion) card);
            setForPerformAction(card, side, true, parent);
            parent.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    showCard[side].getChildren().add(GraphicRender.getInstance().buildCard(card, false, false, false));
                }
            });
            parent.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    showCard[side].getChildren().clear();
                }
            });
            battleGround.getChildren().add(parent);
        }
    }

    private void renderManaBar(int leftMana, int fullMana) {
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
    private Label timeText;

    Thread thread = null;

    @FXML
    private void endTurn() {
        infoPacks.clear();
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("endTurnSound"), 1);
        changeTurn();
        thread.interrupt();
        thread = new Timer();
        thread.start();
    }

    class Timer extends Thread{
        @Override
        public void run() {
            int time = GameConstants.getInstance().getInteger("timeToPlay");
            Platform.runLater(()->timeText.setText(""+time));
            for(int i = 0; i < time && thread == this; i++){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                if(isInterrupted()){
                    return;
                }
                int finalI = i;
                if(thread == this) Platform.runLater(()->timeText.setText(""+(time - finalI -1)));
            }
            if (thread == this) Platform.runLater(()->endTurn());
        }
    }

    private void changeTurn() {
        LogCenter.getInstance().getLogger().info("end_turn_player_"+(game.getTurn()+1));
        addGameLog("end_turn_player_"+(game.getTurn()+1));
        try {
            ActionRequest.END_TURN.execute();
            renderActions();
        } catch (GameOverException e) {
            endGame();
        }
        if (game.getTurn() == 0 || !game.isWithBot()) endTurnButton.setDisable(false);
        else endTurnButton.setDisable(true);
    }

    private void endGame(){
        LogCenter.getInstance().getLogger().info("game_over");
        addGameLog("game_over");
        alertBox.setVisible(true);
        thread.interrupt();
        thread = null;
        if(game.getWinner() == 0) alertMessage.setText("You Win.");
        else alertMessage.setText("You Lose.");
    }

    public TranslateTransition putCardToHandAnimation(Pane cardPane, boolean isForOwn) {
        int side = isForOwn? 0:1;
        rootPane.getChildren().add(cardPane);
        cardPane.setVisible(false);
        int duration = 2;
        double fromX, fromY, toX, toY;
        fromX = cardsNumberLabel[side].getLayoutX();
        fromY = cardsNumberLabel[side].getLayoutY();
        toX = hand[side].getLayoutX();
        toY = hand[side].getLayoutY();
        return buildTranslateTransition(cardPane, fromX, fromY, toX, toY, duration);
    }

    @FXML
    private void backToMenu() {
        LogCenter.getInstance().getLogger().info("back_to_menu");
        MediaManager.getInstance().stopMedia(GameConstants.getInstance().getString("battleGroundSound"));
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("menuSound"), -1);
        root.setVisible(false);
    }

    @FXML
    private StackPane passiveSelectionPane;
    @FXML
    private HBox passiveSelectionPlace;

    public void renderPassives(int side) {
        passiveSelectionPlace.getChildren().clear();
        passiveSelectionPane.setVisible(true);
        cardSelectionButton.setVisible(false);
        passiveText.setText("Player "+(side+1));
        ArrayList<Passive> passives = DataManager.getInstance().getAllCharacter(Passive.class);
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
                    performAction(passive, side, false, passiveGraphics, -1);
                    passiveSelectionPane.setVisible(false);
                    if(side == 0 && !game.isWithBot()) renderPassives(1);
                    else {
                        renderCardSelection(0);
                    }
                }
            });
            passiveSelectionPlace.getChildren().add(passiveGraphics);
            passives.remove(passive);
        }
    }

    @FXML
    private Label passiveText;
    @FXML
    private Button cardSelectionButton;
    private ArrayList<Card> cardsSelected = new ArrayList<>();
    private int selectionSide;
    public void renderCardSelection(int side) {
        selectionSide = side;
        passiveSelectionPlace.getChildren().clear();
        passiveSelectionPane.setVisible(true);
        cardSelectionButton.setVisible(true);
        passiveText.setText("Player "+(side+1));
        cardsSelected.clear();
        ArrayList<Card> cards = game.getCompetitor(side).getInHandCards();
        for (Card card: cards){
            Pane cardGraphics = GraphicRender.getInstance().buildCard(card, false, false, false);
            cardGraphics.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Node node = cardGraphics.getChildren().get(cardGraphics.getChildren().size() - 1);
                    if(!node.isVisible()) cardsSelected.add(card);
                    else cardsSelected.remove(card);
                    node.setVisible(!node.isVisible());
                }
            });
            passiveSelectionPlace.getChildren().add(cardGraphics);
        }
    }

    @FXML
    public void cardSelecting(){
        try {
            ActionRequest.selectCard(cardsSelected);
        } catch (GameOverException e) {
            e.printStackTrace();
        }
        renderActions();
        if(selectionSide == 0 && !game.isWithBot()){
            game.setTurn(1);
            renderCardSelection(1);
        }
        else {
            game.setTurn(0);
            passiveSelectionPane.setVisible(false);
            thread = new Timer();
            thread.start();
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
    private synchronized void performAction(Character character, int side, boolean isOnGround, Parent parent, int summonPlace){
        Logger logger = LogCenter.getInstance().getLogger();
        infoPacks.add(new InfoPack(character, side, isOnGround, parent, summonPlace));
        InfoPack[] parameters = new InfoPack[infoPacks.size()];
        for(int i = 0; i < infoPacks.size(); i++){
             parameters[i] = infoPacks.get(i);
        }
        try {
            ActionRequest.PERFORM_ACTION.execute(parameters);
            renderActions();
            infoPacks.clear();
        } catch (Exception e) {
            logger.error(e);
            try {
                throw e;
            } catch (SelectionNeededException selectionNeededException) {
                selectionNeededException.printStackTrace();
                //kjslkfdjs
            } catch (InvalidChoiceException invalidChoiceException) {
                invalidChoiceException.printStackTrace();
                infoPacks.clear();
            } catch (GameOverException gameOverException) {
                gameOverException.printStackTrace();
                endGame();
            }
        }
    }
    private ArrayList<Transition> transitions = new ArrayList<>();
    private ArrayList<ActionHandler> afterAction = new ArrayList<>(), beforeAction = new ArrayList<>();
    private void renderActions() {
        transitions.clear();
        afterAction.clear();
        beforeAction.clear();
        setPlayCardTransition();
        setAttackTransition();
        setDrawTransition();
        if(transitions.size() > 0){
            rootPane.setDisable(true);
            bindTransitions();
            try {
                if (transitions.size() > 0) beforeAction.get(0).runAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else gameRender();
    }

    private void bindTransitions() {
        for(int i = 0; i < transitions.size() - 1; i++){
            int finalI = i;
            transitions.get(i).setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        afterAction.get(finalI).runAction();
                        beforeAction.get(finalI + 1).runAction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        transitions.get(transitions.size() - 1).setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    afterAction.get(transitions.size() - 1).runAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(ActionRequest.readSummoned()){
                    MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("minionPlacingSound"), 1);
                }
                rootPane.setDisable(false);
                gameRender();
            }
        });
    }

    private void setDrawTransition() {
        int drawNumber = ActionRequest.readDrawNumber();
        while (drawNumber > 0 ){
            Card card = game.getCompetitor(game.getTurn()).getInHandCards().get(game.getCompetitor(game.getTurn()).getInHandCards().size() - drawNumber--);
            Pane cardPane = GraphicRender.getInstance().buildCard(card, false, false, (game.getTurn() == 0 || !game.isWithBot())? false:true);
            Transition transition = putCardToHandAnimation(cardPane, (game.getTurn() == 0)? true:false);
            transitions.add(transition);
            beforeAction.add(new ActionHandler() {
                @Override
                public void runAction() throws Exception {
                    cardPane.setVisible(true);
                    transition.play();
                }
            });
            afterAction.add(new ActionHandler() {
                @Override
                public void runAction() throws Exception {
                    rootPane.getChildren().remove(cardPane);
                }
            });
        }
    }

    private void setAttackTransition() {
        ArrayList<InfoPack> attackList = ActionRequest.readAttackingList();
        if(attackList.size() > 0){
            Transition go = goAttackAnimation(attackList.get(0).getParent(), attackList.get(1).getParent());
            Transition back = backAttackAnimation(attackList.get(0).getParent(), attackList.get(1).getParent());
            transitions.add(go);
            transitions.add(back);
            beforeAction.add(new ActionHandler() {
                @Override
                public void runAction() throws Exception {
                    go.play();
                }
            });
            afterAction.add(new ActionHandler() {
                @Override
                public void runAction() throws Exception {
                    MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("attackSound"),1);
                }
            });
            beforeAction.add(new ActionHandler() {
                @Override
                public void runAction() throws Exception {
                    back.play();
                }
            });
            afterAction.add(new ActionHandler() {
                @Override
                public void runAction() throws Exception { }
            });
        }
    }

    private Transition backAttackAnimation(Parent parent1, Parent parent2) {
        double x = parent2.getParent().getLayoutX() - parent1.getParent().getLayoutX() + parent2.getLayoutX() - parent1.getLayoutX();
        double y = parent2.getParent().getLayoutY() - parent1.getParent().getLayoutY() + parent2.getLayoutY() - parent1.getLayoutY();
        return buildTranslateTransition(parent1, x, y, 0, 0, 0.5);
    }

    private Transition goAttackAnimation(Parent parent1, Parent parent2) {
        double x = parent2.getParent().getLayoutX() - parent1.getParent().getLayoutX() + parent2.getLayoutX() - parent1.getLayoutX();
        double y = parent2.getParent().getLayoutY() - parent1.getParent().getLayoutY() + parent2.getLayoutY() - parent1.getLayoutY();
        return buildTranslateTransition(parent1, 0, 0, x, y, 0.2);
    }

    private void setPlayCardTransition() {
        InfoPack played = ActionRequest.readPlayed();
        if(played != null){
            if(played.getSide() == 1){
                ((Pane) played.getParent()).getChildren().get(((Pane) played.getParent()).getChildren().size() - 2).setVisible(false);
            }
            Transition transition = playCardTransition(played.getParent());
            transitions.add(transition);
            beforeAction.add(new ActionHandler() {
                @Override
                public void runAction() throws Exception {
                    MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("playCardSound"), 1);
                    transition.play();
                }
            });
            afterAction.add(new ActionHandler() {
                @Override
                public void runAction() throws Exception {
                    hand[played.getSide()].getChildren().remove(played.getParent());
                }
            });
        }
    }

    private TranslateTransition playCardTransition(Parent parent) {
        double fromX = parent.getLayoutX();
        double fromY = parent.getLayoutY();
        parent.setLayoutX(0);
        parent.setLayoutY(0);
        double toX = ((GameConstants.getInstance().getInteger("screenWidth")/2 - parent.getParent().getLayoutX() - (((Pane)parent).getWidth()/2)))- parent.getLayoutX();
        double toY = ((GameConstants.getInstance().getInteger("screenHeight")/2 - parent.getParent().getLayoutY() - (((Pane)parent).getHeight()/2)));
        return buildTranslateTransition(parent, fromX, fromY, toX, toY, 1);
    }

    private TranslateTransition buildTranslateTransition(Parent parent, double fromX, double fromY, double toX, double toY, double second){
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(second), parent);
        translateTransition.setFromX(fromX);
        translateTransition.setFromY(fromY);
        translateTransition.setToX(toX);
        translateTransition.setToY(toY);
        translateTransition.setCycleCount(1);
        return translateTransition;
    }

    private void setForPerformAction(Character character, int side, boolean isOnGround, Parent parent){
        parent.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                performAction(character, side, isOnGround, parent, -1);
            }
        });
    }
}