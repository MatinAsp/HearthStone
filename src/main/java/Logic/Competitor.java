package Logic;

import Data.GameConstants;
import Exceptions.EmptyDeckException;
import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Interfaces.ActionHandler;
import Interfaces.CardAction;
import Interfaces.QuestActionHandler;
import Models.Cards.*;
import Models.Deck;
import Models.Hero;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@JsonIgnoreProperties(value = {"deck", "questsInProgress", "spentMana", "deckAddActions", "handAddActions", "drawNumber"})
public class Competitor{
    private String username;
    private int fullMana = 0, leftMana = 0, time = 0;
    private Deck deck;
    private Hero hero;
    private Weapon heroWeapon = null;
    private ArrayList<Card> inDeckCards, inHandCards;
    private ArrayList<Minion> onBoardCards;
    private ArrayList<Quest> quests;
    private ArrayList<Double> questsProgresses;
    private HashMap<Quest, QuestActionHandler> questsInProgress = new HashMap<>();
    private HashMap<Class, Integer> spentMana = new HashMap<>();
    private ArrayList<ActionHandler> deckAddActions = new ArrayList<>(), handAddActions = new ArrayList<>();
    private int drawNumber;

    public Competitor(Competitor competitor) {
        username = competitor.username;
        fullMana = competitor.fullMana;
        leftMana = competitor.leftMana;
        time = competitor.time;
        hero = competitor.hero;
        heroWeapon = competitor.heroWeapon;
        inDeckCards = competitor.inDeckCards;
        inHandCards = competitor.inHandCards;
        onBoardCards = competitor.onBoardCards;
        quests = competitor.quests;
        questsProgresses = competitor.questsProgresses;
    }

    public void addDeckAddActions(ActionHandler actionHandler){
        deckAddActions.add(actionHandler);
    }

    public void addHandAddActions(ActionHandler actionHandler){
        handAddActions.add(actionHandler);
    }

    public Competitor() {}

    public Competitor(String username){
        this.username = username;
        drawNumber = GameConstants.getInstance().getInteger("drawNumber");
        inDeckCards = new ArrayList<>();
        inHandCards = new ArrayList<>();
        onBoardCards = new ArrayList<>();
        questsProgresses = new ArrayList<>();
        quests = new ArrayList<>();
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
        questsProgresses.clear();
        for (Quest quest: quests){
            if(questsInProgress.get(quest).getQuestPercent() >= 1){
                QuestActionHandler questActionHandler = questsInProgress.get(quest);
                questsInProgress.remove(quest);
                this.quests.remove(quest);
                try {
                    questActionHandler.runAction();
                } catch (Exception e) { }
            }
            else {
                questsProgresses.add(questsInProgress.get(quest).getQuestPercent());
            }
        }
    }

    public void addQuest(Quest quest, QuestActionHandler questActionHandler){
        quests.add(quest);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setInDeckCards(ArrayList<Card> inDeckCards) {
        this.inDeckCards = inDeckCards;
    }

    public void setInHandCards(ArrayList<Card> inHandCards) {
        this.inHandCards = inHandCards;
    }

    public void setOnBoardCards(ArrayList<Minion> onBoardCards) {
        this.onBoardCards = onBoardCards;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public ArrayList<Quest> getQuests() {
        return quests;
    }

    public void setQuests(ArrayList<Quest> quests) {
        this.quests = quests;
    }

    public ArrayList<Double> getQuestsProgresses() {
        return questsProgresses;
    }

    public void setQuestsProgresses(ArrayList<Double> questsProgresses) {
        this.questsProgresses = questsProgresses;
    }
}
