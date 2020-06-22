package Graphics;

import Data.AssetManager;
import Data.GameConstants;
import Data.GameSettings;
import Models.Cards.*;
import Models.Deck;
import Models.Hero;
import Models.Passive;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class GraphicRender {
    private static GraphicRender cardFactory = null;
    private AssetManager assetManager = AssetManager.getInstance();
    private GameConstants gameConstants = GameConstants.getInstance();
    private GraphicRender() throws IOException { }

    public static GraphicRender getInstance() throws IOException {
        if(cardFactory == null){
            cardFactory = new GraphicRender();
        }
        return cardFactory;
    }

    public Pane buildCard(Card card, boolean lock, boolean priceTag, boolean cardBack) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                card.getType().toLowerCase()+"CardGraphics.fxml"
        ));
        Pane graphicCard = fxmlLoader.load();
        CardGraphicsController cardGraphicsController = fxmlLoader.getController();
        cardGraphicsController.setCard(card);
        cardGraphicsController.setCardPic(assetManager.getCard(card.getName()));
        cardGraphicsController.setMana(card.getMana());
        cardGraphicsController.setCardName(card.getName());
        cardGraphicsController.setPrice(card.getPrice());
        cardGraphicsController.setDescription(card.getDescription());
        cardGraphicsController.setBorder(
                assetManager.getCardBorder(card.getType().toLowerCase()+card.getRarity())
        );
        if(card instanceof Minion){
            cardGraphicsController.setAttack(((Minion) card).getAttack());
            cardGraphicsController.setHp(((Minion) card).getHp());
        }
        if(card instanceof Weapon){
            cardGraphicsController.setAttack(((Weapon) card).getAttack());
            cardGraphicsController.setDurability(((Weapon) card).getDurability());
        }
        cardGraphicsController.priceVisible(priceTag);
        cardGraphicsController.lockVisible(lock);
        if (cardBack){
            GameSettings gameSettings = GameSettings.getInstance();
            cardGraphicsController.setCardBack(AssetManager.getInstance().getCardBack(gameSettings.getCardBack()));
        }
        return graphicCard;
    }

    public Pane buildCollectionsDeck(Deck deck) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("collectionsDeckGraphic.fxml"));
        Pane graphicDeck = fxmlLoader.load();
        CollectionsDeckGraphic collectionsDeckGraphic = fxmlLoader.getController();
        collectionsDeckGraphic.setImage(AssetManager.getInstance().getHeroImage(deck.getHero().getName()));
        collectionsDeckGraphic.setDeckName(deck.getName());
        return graphicDeck;
    }

    public Pane buildCollectionsDecksCard(String cardName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("collectionsDeckGraphic.fxml"));
        Pane graphicDeck = fxmlLoader.load();
        CollectionsDeckGraphic collectionsDeckGraphic = fxmlLoader.getController();
        collectionsDeckGraphic.setImage(AssetManager.getInstance().getCard(cardName));
        collectionsDeckGraphic.setDeckName(cardName);
        return graphicDeck;
    }

    public Pane buildHeroPlace(Hero hero) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("heroPlace.fxml"));
        Pane heroPlace = fxmlLoader.load();
        HeroPlaceController heroPlaceController  = fxmlLoader.getController();
        heroPlaceController.setHeroImage(AssetManager.getInstance().getHeroImage(hero.getName()+"Place"));
        heroPlaceController.setHp(hero.getHp());
        return heroPlace;
    }

    public Pane buildBattleGroundMinion(Minion minion) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("boardMinion.fxml"));
        Pane battleGroundMinion = fxmlLoader.load();
        CardGraphicsController cardGraphicsController = fxmlLoader.getController();
        cardGraphicsController.setCard(minion);
        cardGraphicsController.setAttack(minion.getAttack());
        cardGraphicsController.setHp(minion.getHp());
        cardGraphicsController.setCardPic(AssetManager.getInstance().getCard(minion.getName()));
        return battleGroundMinion;
    }

    public Pane buildPassive(Passive passive) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("passiveGraphics.fxml"));
        Pane passiveGraphics = fxmlLoader.load();
        CardGraphicsController passiveGraphicsController  = fxmlLoader.getController();
        passiveGraphicsController.setCardName(passive.getName());
        passiveGraphicsController.setDescription(passive.getDescription());
        passiveGraphicsController.setCardPic(AssetManager.getInstance().getPassive(passive.getName()));
        return passiveGraphics;
    }

    public Pane buildHeroPower(Card heroPower) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("heroPower.fxml"));
        Pane heroPowerGraphics = fxmlLoader.load();
        HeroPowerController heroPowerController  = fxmlLoader.getController();
        heroPowerController.setMana(heroPower.getMana());
        heroPowerController.setHeroPowerImage(AssetManager.getInstance().getCard(heroPower.getName()));
        return heroPowerGraphics;
    }

    public Pane buildDecksStatus(Deck deck) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("decksStatusGraphics.fxml"));
        Pane decksStatusGraphics = fxmlLoader.load();
        DecksStatusGraphicsController decksStatusGraphicsController  = fxmlLoader.getController();
        decksStatusGraphicsController.loadDeck(deck);
        return decksStatusGraphics;
    }
}
