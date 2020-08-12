package Graphics;

import Data.AssetManager;
import Data.GameConstants;
import Data.GameSettings;
import Interfaces.QuestActionHandler;
import Log.LogCenter;
import Models.Cards.*;
import Models.Deck;
import Models.Hero;
import Models.Passive;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.awt.font.ImageGraphicAttribute;
import java.io.IOException;

public class GraphicRender {
    private static GraphicRender cardFactory = null;
    private AssetManager assetManager = AssetManager.getInstance();
    private GameConstants gameConstants = GameConstants.getInstance();
    private GraphicRender() { }

    public static GraphicRender getInstance() {
        if(cardFactory == null){
            cardFactory = new GraphicRender();
        }
        return cardFactory;
    }

    public Pane buildCard(Card card, boolean lock, boolean priceTag, boolean cardBack) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                card.getType().toLowerCase()+"CardGraphics.fxml"
        ));
        Pane graphicCard = null;
        try {
            graphicCard = fxmlLoader.load();
        } catch (IOException e) {
//            LogCenter.getInstance().getLogger().error(e);
            e.printStackTrace();
        }
        CardGraphicsController cardGraphicsController = fxmlLoader.getController();
        cardGraphicsController.priceVisible(priceTag);
        cardGraphicsController.lockVisible(lock);
        ImageView imageView = new ImageView(AssetManager.getInstance().getImage("X"));
        graphicCard.getChildren().add(imageView);
        imageView.setLayoutX(graphicCard.getPrefWidth()/2 - imageView.getImage().getWidth()/2);
        imageView.setLayoutY(graphicCard.getPrefHeight()/2 - imageView.getImage().getHeight()/2);
        imageView.setVisible(false);
        if (cardBack){
            GameSettings gameSettings = GameSettings.getInstance();
            cardGraphicsController.setCardBack(AssetManager.getInstance().getImage(gameSettings.getCardBack()));
            return graphicCard;
        }
        cardGraphicsController.setCardPic(assetManager.getImage(card.getName()));
        cardGraphicsController.setMana(card.getMana());
        cardGraphicsController.setCardName(card.getName());
        cardGraphicsController.setPrice(card.getPrice());
        cardGraphicsController.setDescription(card.getDescription());
        cardGraphicsController.setBorder(
                assetManager.getImage(card.getType().toLowerCase()+card.getRarity())
        );
        if(card instanceof Minion){
            cardGraphicsController.setAttack(((Minion) card).getAttack());
            cardGraphicsController.setHp(((Minion) card).getHp());
        }
        if(card instanceof Weapon){
            cardGraphicsController.setAttack(((Weapon) card).getAttack());
            cardGraphicsController.setDurability(((Weapon) card).getDurability());
        }
        return graphicCard;
    }

    public Pane buildCollectionsDeck(Deck deck) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("collectionsDeckGraphic.fxml"));
        Pane graphicDeck = null;
        try {
            graphicDeck = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
        CollectionsDeckGraphic collectionsDeckGraphic = fxmlLoader.getController();
        collectionsDeckGraphic.setImage(AssetManager.getInstance().getImage(deck.getHero().getName()));
        collectionsDeckGraphic.setDeckName(deck.getName());
        return graphicDeck;
    }

    public Pane buildCollectionsDecksCard(String cardName) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("collectionsDeckGraphic.fxml"));
        Pane graphicDeck = null;
        try {
            graphicDeck = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
        CollectionsDeckGraphic collectionsDeckGraphic = fxmlLoader.getController();
        collectionsDeckGraphic.setImage(AssetManager.getInstance().getImage(cardName));
        collectionsDeckGraphic.setDeckName(cardName);
        return graphicDeck;
    }

    public Pane buildHeroPlace(Hero hero) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("heroPlace.fxml"));
        Pane heroPlace = null;
        try {
            heroPlace = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
        HeroPlaceController heroPlaceController  = fxmlLoader.getController();
        heroPlaceController.setHeroImage(AssetManager.getInstance().getImage(hero.getName()+"Place"));
        heroPlaceController.setHp(hero.getHp());
        heroPlaceController.setShield(hero.isDivineShield());
        return heroPlace;
    }

    public Pane buildBattleGroundMinion(Minion minion) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("boardMinion.fxml"));
        Pane battleGroundMinion = null;
        try {
            battleGroundMinion = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
        CardGraphicsController cardGraphicsController = fxmlLoader.getController();
        cardGraphicsController.setAttack(minion.getAttack());
        cardGraphicsController.setHp(minion.getHp());
        cardGraphicsController.setCardPic(AssetManager.getInstance().getImage(minion.getName()));
        cardGraphicsController.setTaunt(minion.isTaunt());
        cardGraphicsController.setShield(minion.isDivineShield());
        cardGraphicsController.setStealth(minion.isStealth());
        cardGraphicsController.setCharge(minion.isCharge()||minion.isRush());
        return battleGroundMinion;
    }

    public Pane buildPassive(Passive passive) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("passiveGraphics.fxml"));
        Pane passiveGraphics = null;
        try {
            passiveGraphics = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
        CardGraphicsController passiveGraphicsController  = fxmlLoader.getController();
        passiveGraphicsController.setCardName(passive.getName());
        passiveGraphicsController.setDescription(passive.getDescription());
        passiveGraphicsController.setCardPic(AssetManager.getInstance().getImage(passive.getName()));
        return passiveGraphics;
    }

    public Pane buildHeroPower(Card heroPower) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("heroPower.fxml"));
        Pane heroPowerGraphics = null;
        try {
            heroPowerGraphics = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
        HeroPowerController heroPowerController  = fxmlLoader.getController();
        heroPowerController.setMana(heroPower.getMana());
        heroPowerController.setHeroPowerImage(AssetManager.getInstance().getImage(heroPower.getName()));
        return heroPowerGraphics;
    }

    public Pane buildDecksStatus(Deck deck, boolean isUsing) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("decksStatusGraphics.fxml"));
        Pane decksStatusGraphics = null;
        try {
            decksStatusGraphics = fxmlLoader.load();
            DecksStatusGraphicsController decksStatusGraphicsController  = fxmlLoader.getController();
            decksStatusGraphicsController.loadDeck(deck, isUsing);
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
        return decksStatusGraphics;
    }

    public Pane buildHeroWeapon(Weapon card) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("heroWeapon.fxml"));
        Pane graphicCard = null;
        try {
            graphicCard = fxmlLoader.load();
        } catch (IOException e) {
//            LogCenter.getInstance().getLogger().error(e);
            e.printStackTrace();
        }
        CardGraphicsController cardGraphicsController = fxmlLoader.getController();
        cardGraphicsController.setCardPic(assetManager.getImage(card.getName()));
        cardGraphicsController.setAttack(card.getAttack());
        cardGraphicsController.setDurability(card.getDurability());
        return graphicCard;
    }

    public Pane buildQuestStatus(Quest quest, QuestActionHandler questActionHandler) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("questStatus.fxml"));
        Pane questStatusGraphics = null;
        try {
            questStatusGraphics = fxmlLoader.load();
            QuestStatusController questStatusController = fxmlLoader.getController();
            questStatusController.setPercent("Percent: "+String.format("%.2f", questActionHandler.getQuestPercent()*100));
            questStatusController.setQuestPic(AssetManager.getInstance().getImage(quest.getName()));
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
        return questStatusGraphics;
    }

    public Pane buildHeroPowerShow(HeroPower card) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(card.getType().toLowerCase()+"CardGraphics.fxml"));
        Pane graphicCard = null;
        try {
            graphicCard = fxmlLoader.load();
        } catch (IOException e) {
//            LogCenter.getInstance().getLogger().error(e);
            e.printStackTrace();
        }
        CardGraphicsController cardGraphicsController = fxmlLoader.getController();
        cardGraphicsController.setCardPic(assetManager.getImage(card.getName()));
        cardGraphicsController.setMana(card.getMana());
        cardGraphicsController.setCardName(card.getName());
        cardGraphicsController.setDescription(card.getDescription());
        cardGraphicsController.setBorder(assetManager.getImage(card.getType().toLowerCase()+card.getRarity()));
        return graphicCard;
    }
}
