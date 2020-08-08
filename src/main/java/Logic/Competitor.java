package Logic;

import Data.GameConstants;
import Exceptions.EmptyDeckException;
import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Interfaces.ActionHandler;
import Interfaces.CardAction;
import Interfaces.PlayActionHandler;
import Interfaces.QuestActionHandler;
import Log.LogCenter;
import Models.Cards.*;
import Models.Deck;
import Models.Hero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Competitor{
    private int fullMana = 0, leftMana = 0;
    private Deck deck;
    private Hero hero;
    private Weapon heroWeapon = null;
    private ArrayList<Card> inDeckCards, inHandCards;
    private ArrayList<Minion> onBoardCards;
    private HashMap<Quest, QuestActionHandler> questsInProgress = new HashMap<>();
    private HashMap<Class, Integer> spentMana = new HashMap<>();
    private ArrayList<ActionHandler> deckAddActions = new ArrayList<>(), handAddActions = new ArrayList<>();
    private int drawNumber;

    public void addDeckAddActions(ActionHandler actionHandler){
        deckAddActions.add(actionHandler);
    }

    public void addHandAddActions(ActionHandler actionHandler){
        handAddActions.add(actionHandler);
    }

    public Competitor(){
        drawNumber = GameConstants.getInstance().getInteger("drawNumber");
        inDeckCards = new ArrayList<>();
        inHandCards = new ArrayList<>();
        onBoardCards = new ArrayList<>();
        spentMana.put(Minion.class, 0);
        spentMana.put(Weapon.class, 0);
        spentMana.put(Spell.class, 0);
        spentMana.put(Quest.class, 0);
    }

    public void drawCard() throws EmptyDeckException {
        if (inDeckCards.size() == 0) throw new EmptyDeckException();
        Random random = new Random();
        Card card;
        if(!deck.isInOrder()) card = inDeckCards.get(random.nextInt(inDeckCards.size()));
        else card = inDeckCards.get(0);
        if(inHandCards.size() < GameConstants.getInstance().getInteger("handMaxCard")){
            inHandCards.add(card);
        }
        inDeckCards.remove(card);
    }

    public int getFullMana() {
        return fullMana;
    }

    public void setFullMana(int fullMana) {
        this.fullMana = fullMana;
    }

    public int getLeftMana() {
        return leftMana;
    }

    public void setLeftMana(int leftMana) {
        this.leftMana = leftMana;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public ArrayList<Card> getInDeckCards() {
        return inDeckCards;
    }

    public ArrayList<Card> getInHandCards() {
        return inHandCards;
    }

    public ArrayList<Minion> getOnBoardCards() {
        return onBoardCards;
    }

    public boolean haveCard(Card card){
        return inHandCards.contains(card);
    }

    public void playCard(Card card){
        deck.playCard(card.getName());
        inHandCards.remove(card);
        spentMana.replace(card.getClass(), spentMana.get(card.getClass()) + card.getMana());
    }

    public int getSpentManaOnClass(Class classType){
        return spentMana.get(classType);
    }

    public void damageToHero(int damage) throws GameOverException {
        hero.setHp(hero.getHp() - damage);
        if (hero.getHp() <= 0) {
            throw new GameOverException();
        }
    }

    public void addCardOnBoard(Minion minion) {
        onBoardCards.add(minion);
    }

    public void addCardInDeck(Card card) {
        for(ActionHandler actionHandler: deckAddActions){
            try {
                if(actionHandler instanceof CardAction){
                    ((CardAction)actionHandler).runAction(card);
                }
                actionHandler.runAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        inDeckCards.add(card);
    }

    public void addCardInHand(Card card) {
        for(ActionHandler actionHandler: handAddActions){
            try {
                if(actionHandler instanceof CardAction){
                    ((CardAction)actionHandler).runAction(card);
                }
                actionHandler.runAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        inHandCards.add(card);
    }

    public void removeCardFromHand(Card card) {
        inHandCards.remove(card);
    }

    public Weapon getHeroWeapon() {
        return heroWeapon;
    }

    public void setHeroWeapon(Weapon heroWeapon) {
        this.heroWeapon = heroWeapon;
    }

    public void runQuestRewards() {
        ArrayList<Quest> quests = new ArrayList<>();
        quests.addAll(questsInProgress.keySet());
        for (Quest quest: quests){
            if(questsInProgress.get(quest).getQuestPercent() >= 1){
                QuestActionHandler questActionHandler = questsInProgress.get(quest);
                questsInProgress.remove(quest);
                try {
                    questActionHandler.runAction();
                } catch (Exception e) { }
            }
        }
    }

    public void addQuest(Quest quest, QuestActionHandler questActionHandler){
        questsInProgress.put(quest, questActionHandler);
    }

    public int getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(int drawNumber) {
        this.drawNumber = drawNumber;
    }

    public void removeCardFromDeck(Card card) {
        inDeckCards.remove(card);
    }

    public void useHeroPower() throws InvalidChoiceException {
        if(!hero.getHeroPower().isCharge()){
            throw new InvalidChoiceException();
        }
        hero.getHeroPower().setCharge(false);
        leftMana -= hero.getHeroPower().getMana();
    }

    public HashMap<Quest, QuestActionHandler> getQuestsInProgress() {
        return questsInProgress;
    }
}
