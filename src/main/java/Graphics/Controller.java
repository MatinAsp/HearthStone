package Graphics;

import Data.DataManager;
import Data.GameConstants;
import Data.GameSettings;
import Data.MediaManager;
import Exceptions.InvalidChoiceException;
import Exceptions.SelectionNeededException;
import Interfaces.ActionHandler;
import Logic.*;
import Models.Cards.Card;
import Models.Deck;
import Models.Hero;
import Models.Player;
import Network.Client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private StackPane root;
    @FXML
    private StackPane logInPage;
    @FXML
    private StackPane menu;
    @FXML
    private StackPane collectionsPage;
    @FXML
    private StackPane rankingPage;
    @FXML
    private StackPane storePage;
    @FXML
    private StackPane alertBox;
    @FXML
    private Label alertMessage;
    @FXML
    private Label playerCoin;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private StackPane status;
    @FXML
    private StackPane waitPane;
    @FXML
    private StackPane connectionPane;

    public Controller(){
    }

    @FXML
    private void logInAction(){
        client.sendLogInRequest(usernameField.getText(), passwordField.getText());
    }

    public void logInActionUpdate(){
        usernameField.clear();
        passwordField.clear();
        navigateFromLogInToMenu();
    }

    @FXML
    private void signInAction(ActionEvent e) {
        client.sendSignInRequest(usernameField.getText(), passwordField.getText());
    }

    @FXML
    private void exit(ActionEvent e){
        try {
            client.exitClient();
            client.logInfo("exit");
        } catch (Exception ex) {
            System.out.println("exit on login page.");;
        }
        System.exit(0);
    }

    @FXML
    private void okAlert(){
        alertBox.setVisible(false);
        client.logInfo("alert_dismissed");
    }

    @FXML
    private void navigateFromLogInToMenu() {
        client.logInfo("navigate_from_log_in_to_menu");
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("menuSound"), -1);
        navigate(logInPage, menu);
    }

    @FXML
    private void navigateFromMenuToRanking() {
        client.logInfo("navigate_from_menu_to_ranking");
        client.sendRankingRequest();
        waitPane.setVisible(true);
    }

    @FXML
    private GridPane rankingBoard;

    public void loadRanking(ArrayList<String> usernames, ArrayList<String> cups, int ownRank){
        waitPane.setVisible(false);
        ArrayList<Node> nodes = new ArrayList<>();
        for(int i = usernames.size() - 1; i >= 0; i--){
            nodes.add(GraphicRender.getInstance().buildRank(usernames.get(i), usernames.size() - i, Integer.parseInt(cups.get(i))));
        }
        nodes.add(GraphicRender.getInstance().buildRank(client.getPlayer().getUsername(), ownRank, client.getPlayer().getCup()));
        gridPaneRender(rankingBoard, nodes);
        navigate(menu, rankingPage);
    }

    @FXML
    private void navigateFromRankingToMenu() {
        client.logInfo("navigate_from_ranking_to_menu");
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("menuSound"), -1);
        navigate(rankingPage, menu);
    }

    @FXML
    private void navigateFromMenuToLogIn() {
        try {
            client.logInfo("log_out");
        }catch (NullPointerException e){

        }
        MediaManager.getInstance().stopMedia(GameConstants.getInstance().getString("menuSound"));
        navigate(menu, logInPage);
    }

    @FXML
    private void navigateFromMenuToStore() throws IOException {
        client.logInfo("navigate_from_menu_to_store");
        loadStore();
        navigate(menu, storePage);
    }

    @FXML
    private void navigateFromMenuToStatus() {
        client.logInfo("navigate_from_menu_to_status");
        loadStatus();
        navigate(menu, status);
    }

    @FXML
    private StackPane settingsPane;

    @FXML
    private void navigateFromSettingsToMenu(){
        navigate(settingsPane, menu);
        try{
            client.logInfo("navigate_from_settings_to_menu");
        }catch (Exception e){}
    }

    @FXML
    private void navigateFromMenuToSettings() throws IOException {
        client.logInfo("navigate_from_menu_to_settings");
        loadSettings();
        navigate(menu, settingsPane);
    }

    @FXML
    private Slider volumeSlider;
    @FXML
    private ChoiceBox<String> cardsBackChoiceBox;
    @FXML
    private ChoiceBox<String> arenaChoiceBox;

    private void loadSettings() throws IOException {
        GameSettings gameSettings = GameSettings.getInstance();
        volumeSlider.setValue(gameSettings.getVolume() * volumeSlider.getMax());
        cardsBackChoiceBox.getItems().clear();
        cardsBackChoiceBox.getItems().addAll(gameSettings.getAllCardBack());
        cardsBackChoiceBox.getSelectionModel().select(gameSettings.getCardBack());
        arenaChoiceBox.getItems().clear();
        arenaChoiceBox.getItems().addAll(gameSettings.getAllArenas());
        arenaChoiceBox.getSelectionModel().select(gameSettings.getBattleGroundArena());
    }

    @FXML
    private void applySettings() throws IOException {
        GameSettings gameSettings = GameSettings.getInstance();
        gameSettings.setVolume(volumeSlider.getValue()/volumeSlider.getMax());
        gameSettings.setCardBack(cardsBackChoiceBox.getSelectionModel().getSelectedItem());
        gameSettings.setBattleGroundArena(arenaChoiceBox.getSelectionModel().getSelectedItem());
        gameSettings.applySettings();
        navigateFromSettingsToMenu();
        client.logInfo("settings_applied");
    }

    @FXML
    private GridPane decksStatusBoard;
    private void loadStatus(){
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Deck> decks = client.getPlayer().getAllDecks();
        for(int i = decks.size() - 1; i>=0; i--){
            Deck deck = decks.get(i);
            boolean isUsing = (client.getPlayer().getCurrentDeckName().equals(deck.getName())) ? true : false;
            Pane decksStatusGraphics = GraphicRender.getInstance().buildDecksStatus(deck, isUsing);
            nodes.add(decksStatusGraphics);
        }
        gridPaneRender(decksStatusBoard, nodes);
    }

    @FXML
    private void navigateFromStatusToMenu(){
        client.logInfo("navigate_from_status_to_menu");
        navigate(status, menu);
    }

    @FXML
    private void navigateFromCollectionsToStore() throws IOException {
        client.logInfo("navigate_from_collections_to_store");
        loadStore();
        navigate(collectionsPage, storePage);
    }

    @FXML
    private void navigateFromStoreToMenu(){
        client.logInfo("navigate_from_store_to_menu");
        navigate(storePage, menu);
    }

    @FXML
    private void navigateFromMenuToCollections(){
        client.logInfo("navigate_from_menu_to_collections");
        loadCollections();
        navigate(menu, collectionsPage);
    }

    private void loadCollections() {
        setDisableDecksButtons(true);
        currentCollectionsDeck = null;
        collectionsFilterer.reset();
        collectionsSearchBox.setText(null);
        SingleSelectionModel<Tab> selectionModel = heroTabPane.getSelectionModel();
        selectionModel.select(0);
        selectionModel = manaTabPane.getSelectionModel();
        selectionModel.select(0);
        selectionModel = collectionsLockingTabPane.getSelectionModel();
        selectionModel.select(0);
        collectionsCardsRender();
        collectionsDecksRender();
    }

    private void loadStore() throws IOException {
        storeFilterer.reset();
        storeFilterer.setCurrentHero("All");
        SingleSelectionModel<Tab> selectionModel = storeLockingTabPane.getSelectionModel();
        selectionModel.select(0);
        storeSearchBox.setText(null);
        storeCardsRender();
    }

    @FXML
    private void navigateFromCollectionsToMenu(){
        client.logInfo("navigate_from_collections_to_menu");
        navigate(collectionsPage, menu);
    }

    private void navigate(Parent start, Parent end){
        start.setVisible(false);
        end.setVisible(true);
    }

    private Filterer collectionsFilterer = Filterer.getInstance();
    private Filterer storeFilterer = Filterer.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //PlayersManager.getInstance().logIn("ali", "ali");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //startGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeHeroSelection();
    }

    @FXML
    private StackPane heroSelectionPane;
    @FXML
    private GridPane heroSelectionGridPane;

    private void initializeHeroSelection(){
        ArrayList<Node> nodes = new ArrayList<>();
        for(Hero hero: DataManager.getInstance().getAllCharacter(Hero.class)){
            Pane heroPane = GraphicRender.getInstance().buildHeroPlace(hero);
            heroPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                try {
                    client.getPlayer().changeDeckHero(
                            currentCollectionsDeck.getName(), hero.getName()
                    );
                    client.sendUpdateRequest("null");
                    client.logInfo("change_deck's_hero");
                    heroSelectionPane.setVisible(false);
                } catch (Exception e) {
                    client.logError(e);
                    setAlert(e.getMessage());
                }
            });
            nodes.add(heroPane);
        }
        gridPaneRender(heroSelectionGridPane, nodes);
    }

    private void gridPaneRender(GridPane gridPane, List<Node> list){
        gridPane.getChildren().clear();
        int counter = 0;
        for(Node node: list){
            GridPane.setConstraints(
                    node,
                    counter%gridPane.getColumnConstraints().size(),
                    counter/gridPane.getColumnConstraints().size()
            );
            gridPane.getChildren().add(node);
            counter++;
        }
    }

    @FXML
    private void changeHero(){
        client.logInfo("hero_selection_window_opened");
        heroSelectionPane.setVisible(true);
    }

    @FXML
    private GridPane storeCardsBoard;

    @FXML
    private GridPane collectionsCardsBoard;

    private void collectionsCardsRender(){
        collectionsCardsBoard.getChildren().clear();
        Player player =client.getPlayer();
        ArrayList<Card> filteredCards =collectionsFilterer.filterCards(DataManager.getInstance().getAllCharacter(Card.class), player);
        ArrayList<Node> nodes = new ArrayList<>();
        for(Card card: filteredCards){
            Pane cardGraphic =
                    GraphicRender.getInstance().buildCard(card, !player.haveCard(card.getName()), false, false);
            collectionCardSetAction(card, cardGraphic,!player.haveCard(card.getName()));
            nodes.add(cardGraphic);
        }
        gridPaneRender(collectionsCardsBoard, nodes);
    }

    private void collectionCardSetAction(Card card, Parent cardGraphic, boolean lock){
        if(lock) {
            cardGraphic.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                client.logInfo("click_card");
                ActionHandler cardAdder = new ActionHandler() {
                    @Override
                    public void runAction() throws Exception {
                        navigateFromCollectionsToStore();
                    }
                };
                setConfirmation(
                        cardAdder,
                        "This card costs "+card.getPrice()+" coins.\n" +
                                "Do you want to transfer to the store to buy this card?",
                        false,
                        true
                );
            });
        }
        else {
            cardGraphic.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                client.logInfo("click_card");
                if (currentCollectionsDeck == null){
                    setAlert("Please select your deck.");
                }
                else {
                    try {
                        currentCollectionsDeck.addCard(card);
                        client.logInfo("add_card_to_deck");
                        client.sendUpdateRequest("showCurrentCollectionsDecksCards");
                    } catch (Exception ex) {
                        client.logError(ex);
                        setAlert(ex.getMessage());
                    }
                }
            });
        }
    }

    public void storeCardsRender(){
        storeCardsBoard.getChildren().clear();
        Player player =client.getPlayer();
        playerCoin.setText(Integer.toString(player.getWallet()));
        ArrayList<Card> filteredCards =storeFilterer.filterCards(DataManager.getInstance().getAllCharacter(Card.class), player);
        ArrayList<Node> nodes = new ArrayList<>();
        for(Card card: filteredCards){
            Pane cardGraphic =
                    GraphicRender.getInstance().buildCard(card, !player.haveCard(card.getName()), true, false);
            storeCardSetAction(card, cardGraphic, !player.haveCard(card.getName()));
            nodes.add(cardGraphic);
        }
        gridPaneRender(storeCardsBoard, nodes);
    }

    private void storeCardSetAction(Card card, Parent cardGraphic, boolean lock){
        if(lock) {
            cardGraphic.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                client.logInfo("click_card");
                ActionHandler cardAdder = new ActionHandler() {
                    @Override
                    public void runAction() throws Exception {
                        client.buyRequest(card.getName());
                        client.logInfo("buy_card");
                    }
                };
                setConfirmation(
                        cardAdder,
                        "You have to pay "+card.getPrice()+" coins.\nAre you sure to buy this card?",
                        false,
                        true
                );
            });
        }
        else {
            cardGraphic.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                client.logInfo("click_card");
                ActionHandler cardRemover = new ActionHandler() {
                    @Override
                    public void runAction() throws Exception {
                        client.sellRequest(card.getName());
                        client.logInfo("sell_card");
                    }
                };
                setConfirmation(
                        cardRemover,
                        "You'll get "+card.getPrice()+" coins.\nAre you sure to sell this card?",
                        false,
                        true
                );
            });
        }
    }

    @FXML
    private StackPane confirmBox;
    @FXML
    private Label confirmMessage;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField getTextField;

    private void  setConfirmation(ActionHandler actionHandler, String message, boolean gettingText, boolean closeByException){
        client.logInfo("set_confirmation");
        confirmBox.setVisible(true);
        getTextField.setVisible(gettingText);
        getTextField.setText(null);
        confirmMessage.setText(message);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                client.logInfo("confirmed");
                try {
                    confirmBox.setVisible(false);
                    actionHandler.runAction();
                } catch (Exception e) {
                    client.logError(e);
                    setAlert(e.getMessage());
                    confirmBox.setVisible(!closeByException);
                    if(closeByException) client.logInfo("confirm_dismissed");
                }
            }
        });
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                client.logInfo("confirm_dismissed");
                confirmBox.setVisible(false);
            }
        });
    }

    @FXML
    private TextField collectionsSearchBox;

    @FXML
    private void collectionsSearchFilter(ActionEvent e) {
        collectionsFilterer.setSearchFilter(collectionsSearchBox.getText());
        client.logInfo("collections_search_card");
        collectionsCardsRender();
    }

    @FXML
    private TextField storeSearchBox;

    @FXML
    private void storeSearchFilter(ActionEvent e) {
        storeFilterer.setSearchFilter(storeSearchBox.getText());
        client.logInfo("store_search_card");
        storeCardsRender();
    }

    private synchronized void setAlert(String message) {
        client.logInfo("set_alert");
        alertMessage.setText(message);
        alertBox.setVisible(true);
    }

    @FXML
    private TabPane collectionsLockingTabPane;

    @FXML
    private TabPane storeLockingTabPane;

    @FXML
    private void collectionsLockFilter() throws IOException {
        lockFilter(collectionsFilterer, collectionsLockingTabPane);
        client.logInfo("collections_lock_filter_changed");
        collectionsCardsRender();
    }

    @FXML
    private void storeLockFilter() throws IOException {
        lockFilter(storeFilterer, storeLockingTabPane);
        client.logInfo("store_lock_filter_changed");
        storeCardsRender();
    }

    private void lockFilter(Filterer filterer, TabPane tabPane){
        String selected="";
        for (Tab tab: tabPane.getTabs()){
            if (tab.isSelected()){
                selected = tab.getText();
            }
        }
        switch (selected){
            case "Locked":
                filterer.setHavingCard(false);
                filterer.setNotHavingCard(true);
                break;
            case "Unlocked":
                filterer.setHavingCard(true);
                filterer.setNotHavingCard(false);
                break;
            default:
                filterer.setHavingCard(true);
                filterer.setNotHavingCard(true);
        }
    }

    @FXML
    private TabPane heroTabPane;

    @FXML
    private void heroFilter() throws IOException {
        for (Tab tab: heroTabPane.getTabs()){
            if (tab.isSelected()){
                collectionsFilterer.setCurrentHero(tab.getText());
            }
        }
        client.logInfo("hero_filter_changed");
        collectionsCardsRender();
    }

    @FXML
    private TabPane manaTabPane;

    @FXML
    private void manaFilter() throws IOException {
        String selected="";
        for (Tab tab: manaTabPane.getTabs()){
            if (tab.isSelected()){
                selected = tab.getText();
            }
        }
        if (selected.equals("All") || selected.equals("")){
            collectionsFilterer.setManaFilter(-1);
        }
        else {
            collectionsFilterer.setManaFilter(Integer.parseInt(selected));
        }
        client.logInfo("mana_filter_changed");
        collectionsCardsRender();
    }

    @FXML
    private GridPane collectionsDecksBoard;

    @FXML
    public void collectionsDecksRender(){
        collectionsDecksBoard.getChildren().clear();
        Player player =client.getPlayer();
        ArrayList<Node> nodes = new ArrayList<>();
        for(Deck deck: player.getAllDecks()){
            Pane deckGraphic =
                    GraphicRender.getInstance().buildCollectionsDeck(deck);
            setCollectionsDeckAction(deckGraphic, deck);
            nodes.add(deckGraphic);
        }
        gridPaneRender(collectionsDecksBoard, nodes);
    }

    private void setCollectionsDeckAction(Parent deckPane, Deck deck){
        deckPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            currentCollectionsDeck = deck;
            client.logInfo("set_current_collections_deck");
            showCurrentCollectionsDecksCards();
        });
    }

    public void showCurrentCollectionsDecksCards() {
        setDisableDecksButtons(false);
        collectionsDecksBoard.getChildren().clear();
        ArrayList<Node> nodes = new ArrayList<>();
        for (Card card: currentCollectionsDeck.getCards()){
            Pane decksCard = GraphicRender.getInstance().buildCollectionsDecksCard(card.getName());
            decksCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                currentCollectionsDeck.removeCard(card.getName());
                client.logInfo("remove_card_from_deck");
                client.sendUpdateRequest("showCurrentCollectionsDecksCards");
            });
            nodes.add(decksCard);
        }
        gridPaneRender(collectionsDecksBoard, nodes);
    }

    private Deck currentCollectionsDeck = null;

    @FXML
    private void selectDeckForPlay(){
        client.getPlayer().setCurrentDeck(currentCollectionsDeck.getName());
        client.sendUpdateRequest("null");
        client.logInfo("set_player_current_deck");
    }

    @FXML
    private void removeDeck(){
        ActionHandler actionHandler = new ActionHandler() {
            @Override
            public void runAction() throws Exception {
                Player player = client.getPlayer();
                if(player.getCurrentDeckName().equals(currentCollectionsDeck.getName())){
                    player.setCurrentDeck(null);
                    client.logInfo("set_player_current_deck_null");
                }
                client.getPlayer().removeDeck(currentCollectionsDeck.getName());
                setDisableDecksButtons(true);
                client.sendUpdateRequest("collectionsDecksRender");
                client.logInfo("remove_deck");
            }
        };
        setConfirmation(
                actionHandler,
                "Are you sure to remove this deck?",
                false,
                true
        );
    }

    @FXML
    private HBox decksButtons;
    @FXML
    private Label decksLabel;
    @FXML
    private Button decksBackButton;

    @FXML
    private Label decksCardsNumber;

    private void setDisableDecksButtons(boolean value){
        decksButtons.setDisable(value);
        decksBackButton.setDisable(value);
        decksCardsNumber.setDisable(value);
        if(value){
            decksCardsNumber.setText("");
            currentCollectionsDeck = null;
            decksLabel.setText("My Decks");
        }
        else{
            decksCardsNumber.setText(
                    currentCollectionsDeck.getCards().size()+"/"+currentCollectionsDeck.getHero().getDeckMax()
            );
            decksLabel.setText(currentCollectionsDeck.getName());
        }
    }

    @FXML
    private void decksBackButtonAction() {
        client.logInfo("set_collections_current_deck_null");
        setDisableDecksButtons(true);
        collectionsDecksRender();
    }

    @FXML
    private void makeNewDeck(){
        ActionHandler actionHandler = new ActionHandler() {
            @Override
            public void runAction() throws Exception {
                Player player = client.getPlayer();
                player.createDeck(getTextField.getText(), player.getAllHeroes().get(0).getName());
                client.logInfo("new_deck_created");
                currentCollectionsDeck = player.getDeck(getTextField.getText());
                client.logInfo("set_collections_current_deck");
                client.sendUpdateRequest("showHeroSelection");
            }
        };
        setConfirmation(actionHandler, "Enter Your Deck's Name.", true, false);
    }

    public void showHeroSelection(){
        heroSelectionPane.setVisible(true);
    }

    @FXML
    private void changeDeckName(){
        ActionHandler actionHandler = new ActionHandler() {
            @Override
            public void runAction() throws Exception {
                currentCollectionsDeck.setName(getTextField.getText());
                client.logInfo("change_deck's_name");
                client.sendUpdateRequest("showCurrentCollectionsDecksCards");
            }
        };
        setConfirmation(actionHandler, "Enter Your Deck's Name.", true, false);
    }

///start
    @FXML
    private void startSinglePlayer(){
        Player player = client.getPlayer();
        if(player.getCurrentDeckName() == null){
            setAlert("Please select your deck before you start a game.");
            navigateFromMenuToCollections();
            return;
        }
        client.sendOfflinePlayRequest();
    }

    @FXML
    private void startMultiPlayer(){
        Player player = client.getPlayer();
        if(player.getCurrentDeckName() == null){
            setAlert("Please select your deck before you start a game.");
            navigateFromMenuToCollections();
            return;
        }
        waitPane.setVisible(true);
        client.sendOnlinePlayRequest();
    }

    @FXML
    private void startDeckReader(){
        ArrayList<Deck> decks = DataManager.getInstance().getDeckReaderDecks();
        client.sendDeckReaderPlayRequest();
    }

    private BattleGroundController battleGroundController = null;
    public void starGame(Game game){
        waitPane.setVisible(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("battleGround.fxml"));
        StackPane gameBoard = null;
        try {
            gameBoard = fxmlLoader.load();
            BattleGroundController battleGroundController = fxmlLoader.getController();
            this.battleGroundController= battleGroundController;
            battleGroundController.setClient(client);
            battleGroundController.setGame(game);
            updateGame(game);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            setAlert(e.getMessage());
            client.logError(e);
            e.printStackTrace();
        }
        root.getChildren().add(gameBoard);
        alertBox.toFront();
        MediaManager.getInstance().stopMedia(GameConstants.getInstance().getString("menuSound"));
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("battleGroundSound"), -1);
        client.logInfo("start_a_game");
    }

    public void updateGame(Game game) {
        battleGroundController.setGame(game);
        battleGroundController.makeGameOk();
        Platform.runLater(() -> battleGroundController.renderActions());
    }

    public void handleException(Exception exception) {
        if(exception instanceof InvalidChoiceException || exception instanceof SelectionNeededException){
            Platform.runLater(() -> battleGroundController.exceptionHandling(exception));
        }
        else Platform.runLater(() -> setAlert(exception.getMessage()));
    }
    ///end
    @FXML
    private void deleteAccount() {
        ActionHandler actionHandler = new ActionHandler() {
            @Override
            public void runAction() throws Exception {
                client.deleteRequest(getTextField.getText());
            }
        };
        setConfirmation(actionHandler, "Enter Your Password To Confirm.", true, false);
    }
    public void deleteUpdate(){
        navigateFromSettingsToMenu();
        client.logInfo("USER_DELETED");
        navigateFromMenuToLogIn();
    }
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    private Client client = null;

    @FXML
    private StackPane networkPage;

    @FXML
    private void connect(){
        try {
            client = new Client(ipField.getText(), Integer.parseInt(portField.getText()), this);
            client.start();
            logInPage.setVisible(true);
            networkPage.setVisible(false);
        } catch (Exception e) {
            setAlert(e.getMessage());
        }
    }

    public void currentDeckCheck() {
        if(currentCollectionsDeck != null){
            for(Deck deck: client.getPlayer().getAllDecks()){
                if(deck.getId() == currentCollectionsDeck.getId()){
                    currentCollectionsDeck = deck;
                    showCurrentCollectionsDecksCards();
                }
            }
        }
    }

    public void endGame() {
        Platform.runLater(() ->{
            battleGroundController.endGame();
            battleGroundController = null;
        });
    }

    public void setConnectionWait(boolean isWait) {
        connectionPane.setVisible(isWait);
    }

    public void backFormGame() {
        battleGroundController.backFormGame();
        battleGroundController = null;
    }
}
