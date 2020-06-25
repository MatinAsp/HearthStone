package Models;

import Data.DataManager;
import Models.Cards.Card;

import java.io.IOException;
import java.util.*;

public class Player {
    private String username,password;
    private int wallet, id;
    private ArrayList<Card> allCards;
    private ArrayList<Hero> allHeroes;
    private ArrayList<Deck> allDecks;
    private String currentDeckName;

    public Player(
            String username,
            String password,
            int id,
            int wallet,
            ArrayList<Card> allCards,
            ArrayList<Hero> allHeroes
    ) throws IOException {
        this.id = id;
        this.wallet = wallet;
        this.username = username;
        this.password = password;
        this.allCards = allCards;
        this.allHeroes = allHeroes;
        allDecks = new ArrayList<>();
    }

    public Player(){}

    public void createDeck(String name, String heroName) throws Exception {
        if(name == null || name.equals("")) throw new Exception("You didn't enter the name.");
        if(!haveHero(heroName)) throw new Exception("Don't have this hero.");
        if(haveDeck(name)) throw new Exception("This name is for another deck.");
        allDecks.add(new Deck(name, DataManager.getInstance().getObject(Hero.class, heroName)));
    }

    public void removeDeck(String name){
        for(Deck deck: allDecks){
            if(deck.getName().equals(name)){
                allDecks.remove(deck);
                break;
            }
        }
    }

    public Deck getDeck(String name){
        for(Deck deck: allDecks){
            if(deck.getName().equals(name)){
                return deck;
            }
        }
        return null;
    }

    public void changeDeckHero(String name, String heroName) throws Exception {
        if(!haveHero(heroName)) throw new Exception("You don't have this hero.");
        Deck deck = getDeck(name);
        deck.changeHero(heroName);
    }

    public boolean haveDeck(String name){
        for(Deck deck: allDecks){
            if(deck.getName().equals(name)) return true;
        }
        return false;
    }

    public boolean usingCard(String card){
        for(Deck deck: allDecks){
            ArrayList<Card> heroCards = deck.getCards();
            for(Card playerCard: heroCards){
                if(playerCard.getName().equals(card)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean haveCard(String card){
        for(Card playerCard: allCards){
            if(playerCard.getName().equals(card)){
                return true;
            }
        }
        return false;
    }

    public boolean haveHero(String heroName){
        for(Hero hero: allHeroes){
            if(hero.getName().equals(heroName)){
                return true;
            }
        }
        return false;
    }

    public void addToCards(Card card) throws IOException {
        addToCards(card.getName());
    }

    public void addToCards(String card) throws IOException {
        allCards.add(DataManager.getInstance().getObject(Card.class, card));
    }

    public void removeCard(Card card) throws IOException {
        removeCard(card.getName());
    }

    public void removeCard(String card) throws IOException {
        allCards.remove(findCard(card));
        for(Deck deck: allDecks){
            while (deck.haveCard(card)){
                deck.removeCard(card);
            }
        }
    }

    public Card findCard(String card){
        for(Card playerCard: allCards){
            if(playerCard.getName().equals(card)){
                return playerCard;
            }
        }
        return null;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password) throws IOException {
        this.password = password;
    }

    public void setWallet(int wallet) throws IOException {
        this.wallet = wallet;
    }

    public void setCurrentDeck(String deckName) throws IOException {
        if (deckName == null){
            currentDeckName = null;
            return;
        }
        for(Deck deck: allDecks){
            if(deck.getName().equals(deckName)){
                currentDeckName = deck.getName();
            }
        }
    }

    public int getWallet(){
        return wallet;
    }

    public ArrayList<Hero> getAllHeroes(){
        return allHeroes;
    }

    public int getId(){
        return id;
    }

    public ArrayList<Card> getAllCards(){
        return allCards;
    }

    public String getCurrentDeckName(){
        return currentDeckName;
    }

    public void saveData() throws IOException {
        DataManager dataManager = DataManager.getInstance();
        dataManager.savePlayer(this);
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", wallet=" + wallet +
                ", id=" + id +
                ", allCards=" + allCards +
                ", allHeroes=" + allHeroes +
                ", currentDeck=" + currentDeckName +
                '}';
    }

    public String closestHero(String answer){
        int dif = 100;
        String ans = "";
        for(Hero hero: allHeroes){
            int cnt = Math.abs(answer.length()-hero.getName().length());
            for(int i=0; i<Math.min(answer.length(),hero.getName().length()); i++){
                if(answer.charAt(i) != hero.getName().charAt(i)){
                    cnt++;
                }
            }
            if(cnt < dif){
                dif = cnt;
                ans = hero.getName();
            }
        }
        return ans;
    }

    public ArrayList<Deck> getAllDecks() {
        Collections.sort(allDecks);
        return allDecks;
    }
}
