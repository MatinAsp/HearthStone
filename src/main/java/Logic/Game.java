package Logic;


import Data.GameConstants;
import Exceptions.EmptyDeckException;
import Exceptions.GameOverException;
import Models.Cards.Card;
import Models.Cards.Minion;
import Models.Deck;
import Models.Hero;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    private Competitor[] competitor = new Competitor[2];
    private int turn, winner = 0;

    public Game(){
        competitor[0] = new Competitor();
        competitor[1] = new Competitor();
        turn = 0;
    }

    public void changeTurn() throws GameOverException, IOException, EmptyDeckException {
        turn = (turn+1)%2;
        if(competitor[turn].getFullMana() < GameConstants.getInstance().getInteger("manaMax")){
            competitor[turn].setFullMana(competitor[turn].getFullMana()+1);
        }
        competitor[turn].setLeftMana(competitor[turn].getFullMana());
        try {
            competitor[turn].putCardFromDeckToHand();
        } catch (Exception e) {
            competitor[turn].damageToHero(GameConstants.getInstance().getInteger("DeckEmptyLifeDecrease"));
            throw new EmptyDeckException();
        }
    }

    public void playCard(Card card) throws Exception {
        if(!competitor[turn].haveCard(card) || card.getMana() > competitor[turn].getLeftMana()) {
            throw new Exception("can't play the card.");
        }
        competitor[turn].playCard(card);
        competitor[turn].setLeftMana(competitor[turn].getLeftMana() - card.getMana());
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

    private void engGame() throws GameOverException {
        if(competitor[(turn+1)%2].getHero().getHp() <= 0){
            winner = turn;
            competitor[turn].deck.setWinsNumber(competitor[turn].deck.getWinsNumber() + 1);
        }
        else {
            winner = (turn+1)%2;
            competitor[(turn+1)%2].deck.setWinsNumber(competitor[(turn+1)%2].deck.getWinsNumber() + 1);
        }
        competitor[0].deck.setPlaysNumber(competitor[0].deck.getPlaysNumber() + 1);
        competitor[1].deck.setPlaysNumber(competitor[1].deck.getPlaysNumber() + 1);
        turn = -1;
        throw new GameOverException();
    }

    public int getWinner() {
        return winner;
    }

    public class Competitor{
        private int fullMana = 0, leftMana = 0;
        private Deck deck;
        private Hero hero;
        private ArrayList<Card> inDeckCards, inHandCards, onBoardCards;

        Competitor(){
            inDeckCards = new ArrayList<>();
            inHandCards = new ArrayList<>();
            onBoardCards = new ArrayList<>();
        }

        public void putCardFromDeckToHand() throws Exception {
            if (inDeckCards.size() == 0) throw new Exception("Deck is empty.");
            Random random = new Random();
            Card card = inDeckCards.get(random.nextInt(inDeckCards.size()));
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

        public ArrayList<Card> getOnBoardCards() {
            return onBoardCards;
        }

        public boolean haveCard(Card card){
            return inHandCards.contains(card);
        }

        public void playCard(Card card) throws IOException {
            deck.playCard(card.getName());
            inHandCards.remove(card);
            if(card instanceof Minion){
                if(onBoardCards.size() < GameConstants.getInstance().getInteger("groundMaxCard")){
                    onBoardCards.add(card);
                }
            }
        }

        public void damageToHero(int damage) throws GameOverException {
            hero.setHp(competitor[turn].getHero().getHp() - damage);
            if (hero.getHp() <= 0) {
                engGame();
            }
        }
    }
}
