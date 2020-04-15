package Logic.Models;

import DataManager;
import Logic.Models.Cards.Card;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Player {
    private String username,password;
    private int wallet, id;
    private ArrayList<Card> allCards;
    private ArrayList<Hero> allHeroes;
    private Hero currentHero;

    public Player(String username, String password) throws IOException {
        id = (new Random()).nextInt(Integer.MAX_VALUE);
        wallet = 50;
        this.username = username;
        this.password = Integer.toString(password.hashCode());
        allCards = new ArrayList<>();
        allHeroes = new ArrayList<>();
        DataManager dataManager = DataManager.getInstance();
        Scanner scanner = new Scanner(new File("src"+File.separator+"Data"+File.separator+"General"+File.separator+"Default Cards"));
        while (scanner.hasNext()){
            String str=scanner.nextLine();
            allCards.add(dataManager.getCard(str));
        }
        scanner = new Scanner(new File("src"+File.separator+"Data"+File.separator+"General"+File.separator+"Default Heroes"));
        while (scanner.hasNext()){
            allHeroes.add(dataManager.getHero(scanner.nextLine()));
        }
        currentHero = allHeroes.get(0);
        for(Card card: allCards){
            if(currentHero.isForHero(card)){
                currentHero.addCard(card);
            }
        }
        dataManager.addPlayer(this);
        scanner.close();
    }

    public Player(){}

    public boolean usingCard(String card){
        for(Hero hero: allHeroes){
            ArrayList<Card> heroCards = hero.getCards();
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
        allCards.add(card);
        save();
    }

    public void removeCard(Card card) throws IOException {
        allCards.remove(card);
        save();
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
        save();
    }

    public void setWallet(int wallet) throws IOException {
        this.wallet = wallet;
        save();
    }

    public void setCurrentHero(String hero) throws IOException {
        for(Hero playerHero: allHeroes){
            if(playerHero.getName().equals(hero)){
                currentHero = playerHero;
                save();
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

    public Hero getCurrentHero(){
        return currentHero;
    }

    public void save() throws IOException {
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
                ", currentHero=" + currentHero +
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
}
