package Models;

import Data.DataManager;
import Models.Cards.Card;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAnyAttribute;
import java.io.IOException;
import java.util.*;

@Entity
public class Player implements Comparable{
    @Id
    @Column
    private int id;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private int wallet;
    @Column
    private int cup;
    @ManyToMany
    @JoinTable(name="player_card")
  //  @Cascade(CascadeType.ALL)
    private List<Card> allCards;
    @ManyToMany
    @JoinTable(name="player_hero")
 //   @Cascade(CascadeType.ALL)
    private List<Hero> allHeroes;
    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<Deck> allDecks;
    @Column
    private String currentDeckName;

    @PostUpdate
    @PostPersist
    @PostLoad
    private void postLoad() {
        this.allDecks = new ArrayList<>(this.allDecks);
        this.allHeroes = new ArrayList<>(this.allHeroes);
        this.allCards = new ArrayList<>(this.allCards);
    }

    public Player(
            String username,
            String password,
            int id,
            int wallet,
            ArrayList<Card> allCards,
            ArrayList<Hero> allHeroes
    ) {
        cup = 0;
        this.id = id;
        this.wallet = wallet;
        this.username = username;
        this.password = password;
        this.allCards = allCards;
        this.allHeroes = allHeroes;
        currentDeckName = null;
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
            List<Card> heroCards = deck.getCards();
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

    public void addToCards(Card card) {
        addToCards(card.getName());
    }

    public void addToCards(String card) {
        allCards.add(DataManager.getInstance().getObject(Card.class, card));
    }

    public void removeCard(Card card) {
        removeCard(card.getName());
    }

    public void removeCard(String card) {
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

    public void setPassword(String password){
        this.password = password;
    }

    public void setWallet(int wallet){
        this.wallet = wallet;
    }

    public void setCurrentDeck(String deckName){
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

    public List<Hero> getAllHeroes(){
        return allHeroes;
    }

    public int getId(){
        return id;
    }

    public List<Card> getAllCards(){
        return allCards;
    }

    public String getCurrentDeckName(){
        return currentDeckName;
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

    public List<Deck> getAllDecks() {
        Collections.sort(allDecks);
        return allDecks;
    }

    public int getCup() {
        return cup;
    }

    public void setCup(int cup) {
        this.cup = cup;
    }

    @Override
    public int compareTo(Object o) {
        if(cup > ((Player) o).getCup()) return 1;
        if(cup < ((Player) o).getCup()) return -1;
        return 0;
    }

    public void replaceDeck(Deck deck) {
        for(Deck deck1: allDecks){
            if(deck.getName().equals(deck1.getName())){
                allDecks.remove(deck1);
                allDecks.add(deck);
                return;
            }
        }
    }
}
