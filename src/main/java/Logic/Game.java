package Logic;


import Data.GameConstants;
import Exceptions.EmptyDeckException;
import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Exceptions.SelectionNeededException;
import Interfaces.ActionHandler;
import Interfaces.CardAction;
import Models.Cards.Card;
import Models.Cards.HeroPower;
import Models.Cards.Minion;
import Models.Cards.Spell;
import Models.Character;
import Models.Deck;
import Models.InfoPack;
import Models.Passive;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@JsonIgnoreProperties(value = {"actions", "infoPacksPool", "isDraw", "isUsePassive"})
public class Game {
    private Competitor[] competitor = new Competitor[2];
    private int turn, winner = 0;
    private Actions actions;
    private ActionRequest actionRequest;
    private boolean isWithBot = false;
    private HashMap<Integer, InfoPack> infoPacksPool = new HashMap<>();
    private boolean isDraw[] = {false, false};
    private boolean isUsePassive[] = {false, false};

    public Game(){}

    public Game(Competitor competitor1, Competitor competitor2, boolean isWithBot){
        this.isWithBot = isWithBot;
        competitor[0] = competitor1;
        competitor[1] = competitor2;
        turn = 0;
        actionRequest = new ActionRequest(this);
        actions = new Actions(this);
    }

    public Game(Game game) {
        competitor[0]= new Competitor(game.getCompetitor(0));
        competitor[1]= new Competitor(game.getCompetitor(1));
        turn = game.getTurn();
        winner = game.getWinner();
        isWithBot = game.isWithBot();
        actionRequest = game.getActionRequest();
    }

    public ActionRequest getActionRequest(){
        return actionRequest;
    }

    public Competitor[] getCompetitors(){
        return competitor;
    }

    public void performAction(InfoPack[] parameters) throws SelectionNeededException, InvalidChoiceException, GameOverException{
        if((!parameters[0].isOnGround() && !(parameters[0].getCharacter() instanceof Passive)) || parameters[0].getCharacter() instanceof HeroPower){
            checkForMana((Card) parameters[0].getCharacter(), parameters[0].getSide());
        }
        actions.performAction(parameters);
        if(!parameters[0].isOnGround() && !(parameters[0].getCharacter() instanceof Passive)){
            playCard((Card) parameters[0].getCharacter(), parameters[0].getSide());
        }
    }

    public void changeTurn() throws GameOverException {
        turn = (turn+1)%2;
        refreshMana();
        for(int i = 0; i < competitor[turn].getDrawNumber(); i++){
            actionRequest.getDrawCard().execute();
        }
    }

    public void refreshMana() {
        if(competitor[turn].getFullMana() < GameConstants.getInstance().getInteger("manaMax")){
            competitor[turn].setFullMana(competitor[turn].getFullMana()+1);
        }
        competitor[turn].setLeftMana(competitor[turn].getFullMana());
    }

    private void playCard(Card card, int side) {
        competitor[side].playCard(card);
        competitor[side].setLeftMana(competitor[side].getLeftMana() - card.getMana());
    }

    private void checkForMana(Card card, int side) throws InvalidChoiceException {
        if(card.getMana() > competitor[side].getLeftMana()) {
            throw new InvalidChoiceException();
        }
    }

    public void setTurn(int turn){
        this.turn = turn%2;
    }

    public int getTurn(){
        return turn;
    }

    public Competitor getCompetitor(int index){
        return competitor[index%2];
    }

    private void setGameOverRecords(int winnerIndex){
        winner = winnerIndex;
        Deck deck1 = competitor[winnerIndex].getDeck(), deck2 = competitor[(winnerIndex + 1) % 2].getDeck();
        deck1.setWinsNumber(deck1.getWinsNumber() + 1);
        deck1.setCup(deck1.getCup() + 1);
        deck2.setCup(Math.max(deck2.getCup() - 1, 0));
    }

    public void endGame(int winnerIndex){
        if(competitor[(turn+1)%2].getHero().getHp() <= 0){
            setGameOverRecords(turn);
        }
        else {
            if (competitor[turn].getHero().getHp() <= 0){
                setGameOverRecords((turn+1)%2);
            }
            else {
                setGameOverRecords(winnerIndex);
            }
        }
        competitor[0].getDeck().setPlaysNumber(competitor[0].getDeck().getPlaysNumber() + 1);
        competitor[1].getDeck().setPlaysNumber(competitor[1].getDeck().getPlaysNumber() + 1);
    }

    public int getWinner() {
        return winner;
    }

    public void drawCard() throws GameOverException, EmptyDeckException {
        try {
            competitor[turn].drawCard();
        } catch (EmptyDeckException e) {
            competitor[turn].damageToHero(GameConstants.getInstance().getInteger("DeckEmptyLifeDecrease"));
            throw new EmptyDeckException();
        }
    }

    public void summon(Minion minion, int side, int summonPlace) {
        if(competitor[side].getOnBoardCards().size() < GameConstants.getInstance().getInteger("groundMaxCard")){
            if(summonPlace < 0){
                competitor[side].addCardOnBoard(minion);
            }
            else {
                competitor[side].getOnBoardCards().add(summonPlace, minion);
            }
        }
    }

    public boolean isWithBot() {
        return isWithBot;
    }

    public void checkAll() {
        competitor[0].runQuestRewards();
        competitor[1].runQuestRewards();
        for(int i = 0; i < 2; i++){
            try {
                if(competitor[i].getHeroWeapon().getDurability() <= 0){
                    competitor[i].setHeroWeapon(null);
                }
            } catch (NullPointerException e){}
            for(int j = 0; j < competitor[i].getOnBoardCards().size(); j++){
                Minion minion = competitor[i].getOnBoardCards().get(j);
                if(minion.getHp() <= 0){
                    competitor[i].getOnBoardCards().remove(minion);
                    j--;
                }
            }
        }
    }

    public void chargeCards(){
        for(int i = 0; i < 2; i++){
            try {
                competitor[i].getHeroWeapon().setCharge(true);
            } catch (NullPointerException e){}
            for(int j = 0; j < competitor[i].getOnBoardCards().size(); j++){
                Minion minion = competitor[i].getOnBoardCards().get(j);
                minion.setRush(false);
                minion.setCharge(true);
            }
            competitor[i].getHero().getHeroPower().setCharge(true);
        }
    }

    public void initialize() {
        setForPaladin();
        setForMage();
        setForRogue();
    }

    private void setForRogue() {
        for(int i = 0; i < 2; i++){
            if(competitor[i].getHero().getName().equals("Rogue")){
                ArrayList<Card> cards = new ArrayList<>();
                cards.addAll(competitor[i].getInDeckCards());
                cards.addAll(competitor[i].getInHandCards());
                int finalI = i;
                CardAction cardAction = new CardAction() {
                    @Override
                    public void runAction(Card card) {
                        if(!card.getHeroClass().equalsIgnoreCase(competitor[finalI].getHero().getName()) && !card.getHeroClass().equalsIgnoreCase("Neutral")){
                            card.setMana(Math.max(card.getMana() - 2, 0));
                        }
                    }
                    @Override
                    public void runAction() throws Exception { }
                };
                for(Card card: cards){
                    cardAction.runAction(card);
                }
                competitor[i].addDeckAddActions(cardAction);
                competitor[i].addHandAddActions(cardAction);
            }
        }
    }

    private void setForMage() {
        for(int i = 0; i < 2; i++){
            if(competitor[i].getHero().getName().equals("Mage")){
                ArrayList<Card> cards = new ArrayList<>();
                cards.addAll(competitor[i].getInDeckCards());
                cards.addAll(competitor[i].getInHandCards());
                CardAction cardAction = new CardAction() {
                    @Override
                    public void runAction(Card card) {
                        if(card instanceof Spell){
                            card.setMana(Math.max(card.getMana() - 2, 0));
                        }
                    }
                    @Override
                    public void runAction() throws Exception { }
                };
                for(Card card: cards){
                    cardAction.runAction(card);
                }
                competitor[i].addDeckAddActions(cardAction);
                competitor[i].addHandAddActions(cardAction);
            }
        }
    }

    private void setForPaladin() {
        for(int i = 0; i < 2; i++){
            if(competitor[i].getHero().getName().equals("Paladin")){
                int finalI = i;
                actionRequest.getEndTurn().addAction(new ActionHandler() {
                    @Override
                    public void runAction() throws Exception {
                        if(turn != finalI){
                            Random random = new Random();
                            ArrayList<Minion> minions = competitor[finalI].getOnBoardCards();
                            if(minions.size() > 0){
                                Minion minion = minions.get(random.nextInt(minions.size()));
                                minion.setHp(minion.getHp() + 1);
                                minion.setAttack(minion.getAttack() + 1);
                            }
                        }
                    }
                });
            }
        }
    }

    public void selectCard(ArrayList<Card> cardsSelected, int competitorIndex) throws GameOverException, InvalidChoiceException {
        if(isDraw[competitorIndex]) throw new InvalidChoiceException();
        isDraw[competitorIndex] = true;
        boolean changed = false;
        if (turn != competitorIndex){
            changed = true;
            turn = (turn + 1) % 2;
        }
        for(Card card: cardsSelected){
            actionRequest.getDrawCard().execute();
        }
        for(Card card: cardsSelected){
            Card card1 = (Card) getCharacter(card.getId());
            competitor[turn].removeCardFromHand(card1);
            competitor[turn].addCardInDeck(card1);
        }
        if(changed){
            turn = (turn + 1) % 2;
        }
    }

    private Character getCharacter(int id) {
        refreshPool();
        if(!infoPacksPool.containsKey(id)) return null;
        return infoPacksPool.get(id).getCharacter();
    }

    public Competitor getCompetitor(String username) {
        if(competitor[0].getUsername().equals(username)) return competitor[0];
        if(competitor[1].getUsername().equals(username)) return competitor[1];
        return null;
    }

    public void refreshPool(){
        infoPacksPool.clear();
        for(int i = 0; i < 2; i++){
            mapList(competitor[i].getInHandCards(), i, false);
            mapList(competitor[i].getOnBoardCards(), i, true);
            infoPacksPool.put(competitor[i].getHero().getId(), new InfoPack(competitor[i].getHero(), i, true));
            infoPacksPool.put(competitor[i].getHero().getHeroPower().getId(), new InfoPack(competitor[i].getHero().getHeroPower(), i, true));
            if(competitor[i].getHeroWeapon() != null){
                infoPacksPool.put(competitor[i].getHeroWeapon().getId(), new InfoPack(competitor[i].getHeroWeapon(), i, true));
            }
        }
    }

    private void mapList(ArrayList<? extends Character> characters, int side, boolean isOnGround) {
        for(Character character: characters){
            infoPacksPool.put(character.getId(), new InfoPack(character,side,isOnGround));
        }
    }

    public InfoPack getInfoPack(int id) {
        refreshPool();
        if(!infoPacksPool.containsKey(id)) return null;
        return infoPacksPool.get(id);
    }

    public int getCompetitorIndex(String username) {
        if(competitor[0].getUsername().equals(username)) return 0;
        if(competitor[1].getUsername().equals(username)) return 1;
        return -1;
    }

    public void changeSide() {
        turn = (turn + 1) % 2;
        winner = (winner + 1) % 2;
        Competitor tmp = competitor[0];
        competitor[0] = competitor[1];
        competitor[1] = tmp;
        actionRequest.setDrawTurn((actionRequest.getDrawTurn() + 1) % 2);
        if(actionRequest.getPlayed() != null){
            InfoPack infoPack = actionRequest.getPlayed();
            infoPack.setSide((actionRequest.getPlayed().getSide() + 1) % 2);
            actionRequest.setPlayed(infoPack);
        }
        for(InfoPack infoPack: actionRequest.readAttackingList()){
            infoPack.setSide((infoPack.getSide()+1)%2);
        }
    }

    public void setWithBot(boolean isWithBot){
        this.isWithBot = isWithBot;
    }

    public boolean usePassive(int index) {
        if(isUsePassive[index]) return true;
        isUsePassive[index] = true;
        return false;
    }

    public Competitor[] getCompetitor() {
        return competitor;
    }

    public void setCompetitor(Competitor[] competitor) {
        this.competitor = competitor;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public void setActionRequest(ActionRequest actionRequest) {
        this.actionRequest = actionRequest;
    }
}