package Graphics;

import Data.*;
import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Exceptions.SelectionNeededException;
import Interfaces.ActionHandler;
import Interfaces.QuestActionHandler;
import Logic.ActionRequest;
import Logic.Game;
import Models.Cards.Card;
import Models.Cards.HeroPower;
import Models.Cards.Minion;
import Logic.Competitor;
import Models.Cards.Quest;
import Models.Character;
import Models.InfoPack;
import Models.Passive;
import Network.Client.Client;
import javafx.animation.ScaleTransition;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class BattleGroundController implements Initializable {
    private Client client;
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
    private Button questButton1;
    @FXML
    private Button questButton2;
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
            client.logInfo("exit");
        } catch (Exception ex) {
            System.out.println("exit on login page.");;
        }
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        renderPassives(0);
        questButton1.setOnAction(e->renderQuests(0));
        questButton2.setOnAction(e->renderQuests(1));
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
        allInfoPack.clear();
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
            if(!logScroll.isVisible() && lastQuestRender == i){
                renderQuests(i);
            }
            if(game.getTurn() == i && !passiveSelectionPane.isVisible()){
                battleGround[i].toFront();
                heroWeapon[i].toFront();
                hand[i].toFront();
            }
        }
    }

    private void renderHeroWeapon(Competitor competitor, int side) {
        heroWeapon[side].getChildren().clear();
        if(competitor.getHeroWeapon() != null){
            Parent parent =  GraphicRender.getInstance().buildHeroWeapon(competitor.getHeroWeapon());
            showCardOnBoard(parent, side, competitor.getHeroWeapon());
            setForPerformAction(competitor.getHeroWeapon(),side, true, parent);
            heroWeapon[side].getChildren().add(parent);
        }
    }

    private void renderHeroPower(Competitor competitor, int side) {
        heroPowerPlace[side].getChildren().clear();
        Parent parent =  GraphicRender.getInstance().buildHeroPower(competitor.getHero().getHeroPower());
        showCardOnBoard(parent, side, competitor.getHero().getHeroPower());
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
            InfoPack infoPack = new InfoPack(card, isForOwn ? 0 : 1, false, graphicCard, -1);
            allInfoPack.add(infoPack);
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
                client.logInfo("drag_detected");
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
                    client.logInfo("drag_ended");
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
                        }
                    } catch (Exception e) {
                        client.logError(e);
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
            showCardOnBoard(parent, side, card);
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
    private Thread thread = null;
    @FXML
    private Label turnShowingText;

    @FXML
    private void endTurn() {
        clearSelections();
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("endTurnSound"), 1);
        changeTurn();
        turnShowingText.setText("Player " + (game.getTurn() + 1) + " Turn");
        thread.interrupt();
        thread = new Timer();
        thread.start();
    }

    public void makeGameOk() {
        ArrayList<InfoPack> infoPacks = new ArrayList<>();
        for(InfoPack infoPack: game.getActionRequest().getAttackList()){
            infoPacks.add(getInfoPack(infoPack.getCharacter().getId()));
        }
        game.getActionRequest().getAttackList().clear();
        game.getActionRequest().getAttackList().addAll(infoPacks);
        if(game.isWithBot()){
            boolean check = false;
            for(Card card: game.getCompetitor(0).getInHandCards()){
                if(card.getId() == game.getActionRequest().getPlayed().getCharacter().getId()){
                    check = true;
                    break;
                }
            }
            if(!check){
                ArrayList<Card> cards = game.getCompetitor(1).getInHandCards();
                cards.remove(cards.size() - 1);
                cards.add((Card) game.getActionRequest().getPlayed().getCharacter());
                Platform.runLater(() -> renderHand(cards, hand[1], false));
            }
        }
    }

    private InfoPack getInfoPack(int id){
        for(InfoPack infoPack: allInfoPack){
            if(id == infoPack.getCharacter().getId()){
                return infoPack;
            }
        }
        return null;
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
        client.sendEndTurn();
//        try {
//            ActionRequest.END_TURN.execute();
//            Platform.runLater(() -> renderActions());
//        } catch (GameOverException e) {
//            endGame();
//        }
//        if (game.getTurn() == 0 || !game.isWithBot()) endTurnButton.setDisable(false);
//        else endTurnButton.setDisable(true);
    }

    private void endGame(){
        client.logInfo("game over");
        addGameLog("game over");
        isGameEnded = true;
        Platform.runLater(() -> renderActions());
        thread.interrupt();
        thread = null;
    }

    private void setAlertBox(){
        alertBox.setVisible(true);
        if(game.getWinner() == 0) alertMessage.setText("You Win.");
        else alertMessage.setText("You Lose.");
    }

    public TranslateTransition putCardToHandAnimation(Pane cardPane, boolean isForOwn) {
        int side = isForOwn? 0:1;
        rootPane.getChildren().add(cardPane);
        cardPane.setVisible(false);
        int duration = 2;
        cardPane.setLayoutX(cardsNumberLabel[side].getLayoutX());
        cardPane.setLayoutY(cardsNumberLabel[side].getLayoutY());
        double toX, toY;
        toX = hand[side].getLayoutX() - cardPane.getLayoutX();
        toY = hand[side].getLayoutY() - cardPane.getLayoutY();
        return buildTranslateTransition(cardPane, 0, 0, toX, toY, duration);
    }

    @FXML
    private StackPane protector;

    @FXML
    private void backToMenu() {
        client.logInfo("back_to_menu");
        thread = null;
        MediaManager.getInstance().stopMedia(GameConstants.getInstance().getString("battleGroundSound"));
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("menuSound"), -1);
        root.setVisible(false);
        ((Pane) root.getParent()).getChildren().remove(root);
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
                    client.logInfo("passive selected");
                    addGameLog("passive selected");
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
            passiveSelectionPane.toBack();
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

    @FXML
    private GridPane questsStatus;
    @FXML
    private ScrollPane logScroll;
    @FXML
    private ScrollPane questScroll;

    @FXML
    private void showGameLog(){
        logScroll.setVisible(true);
        questScroll.setVisible(false);
    }

    int lastQuestRender;

    public void renderQuests(int side){
        lastQuestRender = side;
        logScroll.setVisible(false);
        questsStatus.getChildren().clear();
        questScroll.setVisible(true);
        HashMap<Quest, QuestActionHandler> map = game.getCompetitor(side).getQuestsInProgress();
        int cnt = 0;
        for(Quest quest: map.keySet()){
            Parent parent = GraphicRender.getInstance().buildQuestStatus(quest, map.get(quest));
            showCardOnBoard(parent, side, quest);
            GridPane.setConstraints(parent, 0, cnt++);
            questsStatus.getChildren().add(parent);
        }
    }

    private void showCardOnBoard(Parent parent, int side, Card card) {
        if(card instanceof HeroPower){
            parent.setOnMouseEntered(event -> showCard[side].getChildren().add(GraphicRender.getInstance().buildHeroPowerShow((HeroPower) card)));
        }
        else{
            Card finalCard = DataManager.getInstance().getObject(Card.class, card.getName());
            parent.setOnMouseEntered(event -> showCard[side].getChildren().add(GraphicRender.getInstance().buildCard(finalCard, false, false, false)));
        }
        parent.setOnMouseExited(event -> showCard[side].getChildren().clear());
    }

    @FXML
    private StackPane targetSelectionPane;
    private void clearSelections(){
        targetSelectionPane.setVisible(false);
        infoPacks.clear();
    }

    private ArrayList<InfoPack> allInfoPack = new ArrayList<>();
    private synchronized void performAction(Character character, int side, boolean isOnGround, Parent parent, int summonPlace){
        InfoPack infoPack = new InfoPack(character, side, isOnGround, parent, summonPlace);
        allInfoPack.add(infoPack);
        performAction(infoPack);
    }

    private synchronized void performAction(InfoPack infoPack){
        infoPacks.add(infoPack);
        InfoPack[] parameters = new InfoPack[infoPacks.size()];
        for(int i = 0; i < infoPacks.size(); i++){
             parameters[i] = infoPacks.get(i);
        }
        client.sendPerformAction(parameters);
//        try {
//            ActionRequest.PERFORM_ACTION.execute(parameters);
//            renderActions();
//        } catch (Exception e) {
//            client.logError(e);
//            try {
//                throw e;
//            } catch (SelectionNeededException selectionNeededException) {
//                client.logError(selectionNeededException);
//                targetSelectionPane.setVisible(true);
//            } catch (InvalidChoiceException invalidChoiceException) {
//                client.logError(invalidChoiceException);
//                clearSelections();
//            } catch (GameOverException gameOverException) {
//                client.logError(gameOverException);
//                endGame();
//            }
//        }
    }
    private ArrayList<Transition> transitions = new ArrayList<>();
    private ArrayList<ActionHandler> afterAction = new ArrayList<>(), beforeAction = new ArrayList<>();
    synchronized void renderActions() {
        clearSelections();
        transitions.clear();
        afterAction.clear();
        beforeAction.clear();
        logActions();
        setHeroPowerTransition();
        setPlayCardTransition();
        setAttackTransition();
        setDrawTransition();
        if(transitions.size() > 0){
            protector.setVisible(true);
            bindTransitions();
            try {
                beforeAction.get(0).runAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            gameRender();
            if(isGameEnded){
                alertBox.setVisible(true);
                if(game.getWinner() == 0) alertMessage.setText("You Win.");
                else alertMessage.setText("You Lose.");
            }
            else botCheck();
        }
    }

    private void setHeroPowerTransition() {
        if (game.getActionRequest().readUseHeroPower()){
            ScaleTransition transition = new ScaleTransition(Duration.seconds(1), heroPowerPlace[game.getTurn()].getChildren().get(0));
            transition.setByX(0.5);
            transition.setByY(0.5);
            transition.setCycleCount(2);
            transition.setAutoReverse(true);
            transitions.add(transition);
            beforeAction.add(() -> transition.play());
            afterAction.add(() -> {});
        }
    }

    private void botCheck(){
        if(game.getTurn() == 1 && game.isWithBot() && !isGameEnded){
            try {
                if(!ActionRequest.BOT_MOVE.execute(allInfoPack)) Platform.runLater(()->endTurn());
                else Platform.runLater(()->renderActions());
            } catch (GameOverException e) {
                endGame();
            }
        }
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
                if(game.getActionRequest().readSummoned()){
                    MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("minionPlacingSound"), 1);
                }
                protector.setVisible(false);
                gameRender();
                if(isGameEnded) setAlertBox();
                else botCheck();
            }
        });
    }

    private boolean isGameEnded = false;

    private void setDrawTransition() {
        int drawNumber = game.getActionRequest().readDrawNumber();
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
            afterAction.add(() -> rootPane.getChildren().remove(cardPane));
        }
    }

    private void setAttackTransition() {
        ArrayList<InfoPack> attackList = game.getActionRequest().readAttackingList();
        if(attackList.size() > 0){
            Transition go = goAttackAnimation(attackList.get(0).getParent(), attackList.get(1).getParent());
            Transition back = backAttackAnimation(attackList.get(0).getParent(), attackList.get(1).getParent());
            transitions.add(go);
            transitions.add(back);
            beforeAction.add(() -> go.play());
            afterAction.add(() -> MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("attackSound"),1));
            beforeAction.add(() -> back.play());
            afterAction.add(() -> {});
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

    private void loggingForGame(String log){
        client.logInfo(log);
        addGameLog(log);
    }

    private void logActions(){
        if(game.getActionRequest().readUseHeroPower()){
            loggingForGame("hero power used");
        }
        if(game.getActionRequest().readTurnEnded()){
            loggingForGame("end turn player "+(((game.getTurn()+1)%2)+1));
        }
        InfoPack played = game.getActionRequest().readPlayed();
        if(played != null){
            loggingForGame("play "+played.getCharacter().getName());
        }
        ArrayList<InfoPack> infoPacks = game.getActionRequest().readAttackingList();
        if(infoPacks.size() > 0){
            loggingForGame(infoPacks.get(0).getCharacter().getName() + " attacked " + infoPacks.get(1).getCharacter().getName());
        }
        for(int i = 0; i < game.getActionRequest().readDrawNumber(); i++){
            loggingForGame("draw card");
        }
        if(game.getActionRequest().readSummoned()){
            loggingForGame("card summoned");
        }

    }


    private void setPlayCardTransition() {
        InfoPack played = game.getActionRequest().readPlayed();
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
            afterAction.add(() -> hand[played.getSide()].getChildren().remove(played.getParent()));
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
        InfoPack infoPack = new InfoPack(character, side, isOnGround, parent, -1);
        allInfoPack.add(infoPack);
        parent.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                performAction(infoPack);
            }
        });
    }

    public void setClient(Client client){
        this.client = client;
    }
}