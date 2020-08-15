package Models;

import Data.Converter;
import Data.DataManager;
import Data.GameConstants;
import Models.Cards.Card;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Deck implements Comparable {
    @Column
    private String name;
    @Id
    @Column
    private int id = new SecureRandom().nextInt();
    @Column
    private int cup = 0;
    @ManyToMany
    @Cascade(CascadeType.ALL)
    private List<Card> cards;
    @ManyToOne
    @Cascade(CascadeType.ALL)
    private Hero hero;
    @Column
    private int playsNumber;
    @Column
    private int winsNumber;
    @Column
    private boolean inOrder;
    @ElementCollection
    @Cascade(CascadeType.ALL)
    private Map<String, Integer> cardsPlaysNumber;

    @PostLoad
    private void postLoad() {
        this.cardsPlaysNumber = new HashMap<>(this.cardsPlaysNumber);
        this.cards = new ArrayList<>(this.cards);
    }

    public Deck() {
    }

    public Deck(String name, Hero hero) {
        this.name = name;
        this.hero = hero;
        cards = new ArrayList<>();
        cardsPlaysNumber = new HashMap<>();
        inOrder = false;
    }

    public Deck(String name, Hero hero, boolean inOrder) {
        this.name = name;
        this.hero = hero;
        cards = new ArrayList<>();
        cardsPlaysNumber = new HashMap<>();
        this.inOrder = inOrder;
    }

    public double getWinsPercent(){
        double percent = (double)winsNumber/playsNumber;
        return Double.parseDouble(String.format("%.2f", percent*100));
    }

    public Hero getHero() {
        return hero;
    }

    public double getAverageMana(){
        double sum = 0;
        for(Card card: cards){
            sum += card.getMana();
        }
        sum /= cards.size();
        return Double.parseDouble(String.format("%.2f", sum));
    }

    public String getMostPlayedCard(){
        int maxCount = -1;
        String cardName = "";
        for(Map.Entry<String, Integer> entry: cardsPlaysNumber.entrySet()){
            if(entry.getValue() > maxCount){
                cardName = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return cardName;
    }

    public void addCard(Card card) throws Exception {
        if(cards.size() == hero.getDeckMax()) throw new Exception("Deck is full!");
        if (!isForDeck(card)) throw new Exception("This card is not for this hero.");
        if(getUseNumber(card.getName()) >= GameConstants.getInstance().getInteger("cardMaxInDeck"))
            throw new Exception("You can't add more of this card.");
        cards.add(card);
        resetRecord();
    }

    public int getUseNumber(String cardName){
        int cnt = 0;
        for(Card card: cards){
            if (card.getName().equals(cardName)) cnt++;
        }
        return cnt;
    }

    public void removeCard(String cardName){
        for(Card card: cards){
            if (card.getName().equals(cardName)){
                cards.remove(card);
                break;
            }
        }
        resetRecord();
    }

    public void changeHero(String heroName) throws Exception {
        for(Card card: cards){
            if (!card.getHeroClass().equals("Neutral") && !card.getHeroClass().equals(heroName)){
                throw new Exception("Can't change the hero.");
            }
        }
        hero = DataManager.getInstance().getObject(Hero.class, heroName);
        resetRecord();
    }

    public boolean isForDeck(Card card){
        if (!card.getHeroClass().equals("Neutral") && !card.getHeroClass().equals(hero.getName())) {
            return false;
        }
        return true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void playCard(String name){
        if(cardsPlaysNumber.containsKey(name)){
            cardsPlaysNumber.replace(name, cardsPlaysNumber.get(name)+1);
        }
        else {
            cardsPlaysNumber.put(name, 1);
        }
    }

    public void setPlaysNumber(int playsNumber) {
        this.playsNumber = playsNumber;
    }

    public void setWinsNumber(int winsNumber) {
        this.winsNumber = winsNumber;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Card> getCards() {
        return (ArrayList<Card>) cards;
    }

    public int getPlaysNumber() {
        return playsNumber;
    }

    public int getWinsNumber() {
        return winsNumber;
    }

    public void resetRecord(){
        cardsPlaysNumber.clear();
        winsNumber = 0;
        playsNumber = 0;
    }

    @Override
    public int compareTo(Object o) {
        if(getCup() > ((Deck) o).getCup()) return 1;
        if(getCup() < ((Deck) o).getCup()) return -1;
        if(getWinsPercent() > ((Deck) o).getWinsPercent()) return 1;
        if(getWinsPercent() < ((Deck) o).getWinsPercent()) return -1;
        if(getWinsNumber() > ((Deck) o).getWinsNumber()) return 1;
        if(getWinsNumber() < ((Deck) o).getWinsNumber()) return -1;
        if(getPlaysNumber() > ((Deck) o).getPlaysNumber()) return 1;
        if(getPlaysNumber() < ((Deck) o).getPlaysNumber()) return -1;
        if(getAverageMana() > ((Deck) o).getAverageMana()) return 1;
        if(getAverageMana() < ((Deck) o).getAverageMana()) return -1;
        return 0;
    }

    public boolean haveCard(String cardName) {
        for(Card card: cards){
            if(card.getName().equals(cardName)){
                return true;
            }
        }
        return false;
    }

    public boolean isInOrder() {
        return inOrder;
    }

    public void addCardWithCheat(Card card) {
        cards.add(card);
    }

    public int getId(){
        return id;
    }

    public int getCup() {
        return cup;
    }

    public void setCup(int cup) {
        this.cup = cup;
    }
}
