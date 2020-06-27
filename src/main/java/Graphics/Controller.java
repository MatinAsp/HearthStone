package Graphics;

import Data.DataManager;
import Data.GameConstants;
import Data.GameSettings;
import Data.MediaManager;
import Interfaces.ActionHandler;
import Log.LogCenter;
import Logic.GameFactory;
import Models.Cards.Card;
import Models.Deck;
import Models.Hero;
import Models.Player;
import Logic.PlayerFactory;
import Logic.PlayersManager;
import Logic.Store;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    public Controller() throws IOException {
    }

    @FXML
    private void logInAction(ActionEvent e) throws IOException {
        try{
            PlayersManager.getInstance().logIn(usernameField.getText(), passwordField.getText());
            LogCenter.getInstance().getLogger().info("log_in");
            usernameField.clear();
            passwordField.clear();
            navigateFromLogInToMenu();
        }catch (Exception o){
            System.out.println(o);
            setAlert(o.getMessage());
        }
    }

    @FXML
    private void signInAction(ActionEvent e) throws Exception {
        try{
            PlayersManager.getInstance().signIn(usernameField.getText(), passwordField.getText());
            LogCenter.getInstance().getLogger().info("sign_in");
            usernameField.clear();
            passwordField.clear();
            navigateFromLogInToMenu();
        }catch (Exception o){
            System.out.println(o);
            setAlert(o.getMessage());
        }
    }

    @FXML
    private void exit(ActionEvent e){
        try {
            PlayersManager.getInstance().getCurrentPlayer().saveData();
            LogCenter.getInstance().getLogger().info("exit");
        } catch (Exception ex) {
            System.out.println("exit on login page.");;
        }
        System.exit(0);
    }

    @FXML
    private void okAlert(ActionEvent e){
        LogCenter.getInstance().getLogger().info("alert_dismissed");
        alertBox.setVisible(false);
    }

    @FXML
    private void navigateFromLogInToMenu() throws IOException {
        LogCenter.getInstance().getLogger().info("navigate_from_log_in_to_menu");
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("menuSoundTrack"), -1);
        navigate(logInPage, menu);
    }

    @FXML
    private void navigateFromMenuToLogIn() throws IOException {
        try {
            PlayersManager.getInstance().getCurrentPlayer().saveData();
            LogCenter.getInstance().getLogger().info("log_out");
        }catch (NullPointerException e){

        }
        MediaManager.getInstance().stopMedia(GameConstants.getInstance().getString("menuSoundTrack"));
        navigate(menu, logInPage);
    }

    @FXML
    private void navigateFromMenuToStore() throws IOException {
        LogCenter.getInstance().getLogger().info("navigate_from_menu_to_store");
        loadStore();
        navigate(menu, storePage);
    }

    @FXML
    private void navigateFromMenuToStatus() throws IOException {
        LogCenter.getInstance().getLogger().info("navigate_from_menu_to_status");
        loadStatus();
        navigate(menu, status);
    }

    @FXML
    private StackPane settingsPane;

    @FXML
    private void navigateFromSettingsToMenu() throws IOException {
        navigate(settingsPane, menu);
        try{
            LogCenter.getInstance().getLogger().info("navigate_from_settings_to_menu");
        }catch (Exception e){}
    }

    @FXML
    private void navigateFromMenuToSettings() throws IOException {
        LogCenter.getInstance().getLogger().info("navigate_from_menu_to_settings");
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
        LogCenter.getInstance().getLogger().info("settings_applied");
    }

    @FXML
    private GridPane decksStatusBoard;
    private void loadStatus() throws IOException {
        ArrayList<Node> nodes = new ArrayList<>();
        for(Deck deck: PlayersManager.getInstance().getCurrentPlayer().getAllDecks()){
            Pane decksStatusGraphics = GraphicRender.getInstance().buildDecksStatus(deck);
            nodes.add(decksStatusGraphics);
        }
        gridPaneRender(decksStatusBoard, nodes);
    }

    @FXML
    private void navigateFromStatusToMenu() throws IOException {
        LogCenter.getInstance().getLogger().info("navigate_from_status_to_menu");
        navigate(status, menu);
    }

    @FXML
    private void navigateFromCollectionsToStore() throws IOException {
        LogCenter.getInstance().getLogger().info("navigate_from_collections_to_store");
        loadStore();
        navigate(collectionsPage, storePage);
    }

    @FXML
    private void navigateFromStoreToMenu(){
        LogCenter.getInstance().getLogger().info("navigate_from_store_to_menu");
        navigate(storePage, menu);
    }

    @FXML
    private void navigateFromMenuToCollections() throws IOException {
        LogCenter.getInstance().getLogger().info("navigate_from_menu_to_collections");
        loadCollections();
        navigate(menu, collectionsPage);
    }

    private void loadCollections() throws IOException {
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
        LogCenter.getInstance().getLogger().info("navigate_from_collections_to_menu");
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
       /* EventHandler<KeyEvent> keyEvenHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER){
                    try {
                        logInAction(new ActionEvent());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };*/
        try {
            initializeHeroSelection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private StackPane heroSelectionPane;
    @FXML
    private GridPane heroSelectionGridPane;

    private void initializeHeroSelection() throws IOException {
        ArrayList<Node> nodes = new ArrayList<>();
        for(Hero hero: DataManager.getInstance().getAllCharacter(Hero.class)){
            Pane heroPane = GraphicRender.getInstance().buildHeroPlace(hero);
            heroPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                try {
                    PlayersManager.getInstance().getCurrentPlayer().changeDeckHero(
                            currentCollectionsDeck.getName(), hero.getName()
                    );
                    LogCenter.getInstance().getLogger().info("change_deck's_hero");
                    heroSelectionPane.setVisible(false);
                } catch (Exception e) {
                    LogCenter.getInstance().getLogger().error(e);
                    setAlert(e.getMessage());
                }
            });
            nodes.add(heroPane);
        }
        gridPaneRender(heroSelectionGridPane, nodes);
    }

    private void gridPaneRender(GridPane gridPane, List<Node> list){
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
        LogCenter.getInstance().getLogger().info("hero_selection_window_opened");
        heroSelectionPane.setVisible(true);
    }

    @FXML
    private GridPane storeCardsBoard;

    @FXML
    private GridPane collectionsCardsBoard;

    private void collectionsCardsRender() throws IOException {
        collectionsCardsBoard.getChildren().clear();
        Player player =PlayersManager.getInstance().getCurrentPlayer();
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

    private void collectionCardSetAction(Card card, Parent cardGraphic, boolean lock) throws IOException {
        if(lock) {
            cardGraphic.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                LogCenter.getInstance().getLogger().info("click_card");
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
                LogCenter.getInstance().getLogger().info("click_card");
                if (currentCollectionsDeck == null){
                    setAlert("Please select your deck.");
                }
                else {
                    try {
                        currentCollectionsDeck.addCard(card);
                        LogCenter.getInstance().getLogger().info("add_card_to_deck");
                        showCurrentCollectionsDecksCards();
                    } catch (Exception ex) {
                        LogCenter.getInstance().getLogger().error(ex);
                        setAlert(ex.getMessage());
                    }
                }
            });
        }
    }

    private void storeCardsRender() throws IOException {
        storeCardsBoard.getChildren().clear();
        Player player =PlayersManager.getInstance().getCurrentPlayer();
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

    private void storeCardSetAction(Card card, Parent cardGraphic, boolean lock) throws IOException {
        Store store = Store.getInstance();
        if(lock) {
            cardGraphic.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                LogCenter.getInstance().getLogger().info("click_card");
                ActionHandler cardAdder = new ActionHandler() {
                    @Override
                    public void runAction() throws Exception {
                        store.buyCard(card);
                        LogCenter.getInstance().getLogger().info("buy_card");
                        storeCardsRender();
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
                LogCenter.getInstance().getLogger().info("click_card");
                ActionHandler cardRemover = new ActionHandler() {
                    @Override
                    public void runAction() throws Exception {
                        store.sellCard(card);
                        LogCenter.getInstance().getLogger().info("sell_card");
                        storeCardsRender();
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
        LogCenter.getInstance().getLogger().info("set_confirmation");
        confirmBox.setVisible(true);
        getTextField.setVisible(gettingText);
        getTextField.setText(null);
        confirmMessage.setText(message);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LogCenter.getInstance().getLogger().info("confirmed");
                try {
                    actionHandler.runAction();
                    confirmBox.setVisible(false);
                } catch (Exception e) {
                    LogCenter.getInstance().getLogger().error(e);
                    setAlert(e.getMessage());
                    confirmBox.setVisible(!closeByException);
                    if(closeByException) LogCenter.getInstance().getLogger().info("confirm_dismissed");
                }
            }
        });
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LogCenter.getInstance().getLogger().info("confirm_dismissed");
                confirmBox.setVisible(false);
            }
        });
    }

    @FXML
    private TextField collectionsSearchBox;

    @FXML
    private void collectionsSearchFilter(ActionEvent e) throws IOException {
        collectionsFilterer.setSearchFilter(collectionsSearchBox.getText());
        LogCenter.getInstance().getLogger().info("collections_search_card");
        collectionsCardsRender();
    }

    @FXML
    private TextField storeSearchBox;

    @FXML
    private void storeSearchFilter(ActionEvent e) throws IOException {
        storeFilterer.setSearchFilter(storeSearchBox.getText());
        LogCenter.getInstance().getLogger().info("store_search_card");
        storeCardsRender();
    }

    private void setAlert(String message) {
        LogCenter.getInstance().getLogger().info("set_alert");
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
        LogCenter.getInstance().getLogger().info("collections_lock_filter_changed");
        collectionsCardsRender();
    }

    @FXML
    private void storeLockFilter() throws IOException {
        lockFilter(storeFilterer, storeLockingTabPane);
        LogCenter.getInstance().getLogger().info("store_lock_filter_changed");
        storeCardsRender();
    }

    private void lockFilter(Filterer filterer, TabPane tabPane) throws IOException {
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
        LogCenter.getInstance().getLogger().info("hero_filter_changed");
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
        LogCenter.getInstance().getLogger().info("mana_filter_changed");
        collectionsCardsRender();
    }

    @FXML
    private GridPane collectionsDecksBoard;

    @FXML
    private void collectionsDecksRender() throws IOException {
        collectionsDecksBoard.getChildren().clear();
        Player player =PlayersManager.getInstance().getCurrentPlayer();
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
            try {
                currentCollectionsDeck = deck;
                LogCenter.getInstance().getLogger().info("set_current_collections_deck");
                showCurrentCollectionsDecksCards();
            } catch (IOException e) {
                LogCenter.getInstance().getLogger().error(e);
                e.printStackTrace();
            }
        });
    }

    private void showCurrentCollectionsDecksCards() throws IOException {
        setDisableDecksButtons(false);
        collectionsDecksBoard.getChildren().clear();
        ArrayList<Node> nodes = new ArrayList<>();
        for (Card card: currentCollectionsDeck.getCards()){
            Pane decksCard = GraphicRender.getInstance().buildCollectionsDecksCard(card.getName());
            decksCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                currentCollectionsDeck.removeCard(card.getName());
                LogCenter.getInstance().getLogger().info("remove_card_from_deck");
                try {
                    showCurrentCollectionsDecksCards();
                } catch (IOException e) {
                    LogCenter.getInstance().getLogger().error(e);
                    e.printStackTrace();
                }
            });
            nodes.add(decksCard);
        }
        gridPaneRender(collectionsDecksBoard, nodes);
    }

    private Deck currentCollectionsDeck = null;

    @FXML
    private void selectDeckForPlay() throws IOException {
        PlayersManager.getInstance().getCurrentPlayer().setCurrentDeck(currentCollectionsDeck.getName());
        LogCenter.getInstance().getLogger().info("set_player_current_deck");
    }

    @FXML
    private void removeDeck() throws IOException {
        ActionHandler actionHandler = new ActionHandler() {
            @Override
            public void runAction() throws Exception {
                Player player = PlayersManager.getInstance().getCurrentPlayer();
                if(player.getCurrentDeckName().equals(currentCollectionsDeck.getName())){
                    player.setCurrentDeck(null);
                    LogCenter.getInstance().getLogger().info("set_player_current_deck_null");
                }
                PlayersManager.getInstance().getCurrentPlayer().removeDeck(currentCollectionsDeck.getName());
                LogCenter.getInstance().getLogger().info("remove_deck");
                setDisableDecksButtons(true);
                collectionsDecksRender();
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
    private void decksBackButtonAction() throws IOException {
        LogCenter.getInstance().getLogger().info("set_collections_current_deck_null");
        setDisableDecksButtons(true);
        collectionsDecksRender();
    }

    @FXML
    private void makeNewDeck(){
        ActionHandler actionHandler = new ActionHandler() {
            @Override
            public void runAction() throws Exception {
                Player player = PlayersManager.getInstance().getCurrentPlayer();
                player.createDeck(getTextField.getText(), player.getAllHeroes().get(0).getName());
                LogCenter.getInstance().getLogger().info("new_deck_created");
                currentCollectionsDeck = player.getDeck(getTextField.getText());
                LogCenter.getInstance().getLogger().info("set_collections_current_deck");
                showCurrentCollectionsDecksCards();
                heroSelectionPane.setVisible(true);
            }
        };
        setConfirmation(actionHandler, "Enter Your Deck's Name.", true, false);
    }

    @FXML
    private void changeDeckName(){
        ActionHandler actionHandler = new ActionHandler() {
            @Override
            public void runAction() throws Exception {
                currentCollectionsDeck.setName(getTextField.getText());
                LogCenter.getInstance().getLogger().info("change_deck's_name");
                showCurrentCollectionsDecksCards();
            }
        };
        setConfirmation(actionHandler, "Enter Your Deck's Name.", true, false);
    }


    @FXML
    private void startGame() throws Exception {
        if(PlayersManager.getInstance().getCurrentPlayer().getCurrentDeckName() == null){
            setAlert("Please select your deck before you start a game.");
            navigateFromMenuToCollections();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("battleGround.fxml"));
        StackPane gameBoard = fxmlLoader.load();
        BattleGroundController battleGroundController = fxmlLoader.getController();
        battleGroundController.setGame(GameFactory.getInstance().build(
                PlayersManager.getInstance().getCurrentPlayer(),
                PlayerFactory.getInstance().build("", "")
        ));
        battleGroundController.gameRender();
        root.getChildren().add(gameBoard);
        MediaManager.getInstance().stopMedia(GameConstants.getInstance().getString("menuSoundTrack"));
        MediaManager.getInstance().playMedia(GameConstants.getInstance().getString("battleGroundSoundTrack"), -1);
        LogCenter.getInstance().getLogger().info("start_a_game");
    }

    @FXML
    private void deleteAccount() throws IOException {
        ActionHandler actionHandler = new ActionHandler() {
            @Override
            public void runAction() throws Exception {
                navigateFromSettingsToMenu();
                PlayersManager.getInstance().deleteCurrentPlayer(getTextField.getText());
                navigateFromMenuToLogIn();
            }
        };
        setConfirmation(actionHandler, "Enter Your Password To Confirm.", true, false);
    }

}
