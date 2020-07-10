package Data;

import Log.LogCenter;
import Models.*;
import Models.Cards.*;
import Models.Character;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class DataManager {
    static private DataManager dataManager = null;
    private HashMap<Class, ArrayList> dataMap;
    private String playersPath;
    private String generalPath;
    private Gson gson;

    private DataManager(){
        GameConstants gameConstants = GameConstants.getInstance();
        playersPath = gameConstants.getString("playerPath");
        generalPath = gameConstants.getString("generalPath");
        gson = new Gson();
        dataMap = new HashMap<>();
        initialize();
        for(Minion minion: getAllCharacter(Minion.class)){
            System.out.print("\""+minion.getName()+"\",");
        }
        System.out.println();
        for(Spell minion: getAllCharacter(Spell.class)){
            System.out.print("\""+minion.getName()+"\",");
        }
        System.out.println();
        for(Weapon minion: getAllCharacter(Weapon.class)){
            System.out.print("\""+minion.getName()+"\",");
        }
    }

    private void initialize() {
        try {
            GameConstants gameConstants = GameConstants.getInstance();
            Scanner scanner = new Scanner(new File(gameConstants.getString("classToLoadPath")));
            while (scanner.hasNext()){
                String className = scanner.next();
                dataMap.put(
                    Class.forName(className),
                    loadData(Class.forName(className),gameConstants.getString(Class.forName(className).getSimpleName().toLowerCase()+"Path"))
                );
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            LogCenter.getInstance().getLogger().error(e);
        }
    }

    private <T> ArrayList<T> loadData(Class<T> tClass, String address) {
        ArrayList<T> arr = new ArrayList<>();
        File[] dir = (new File(address)).listFiles();
        for(File file: dir){
            if(file.isDirectory()){
                try {
                    arr.addAll((ArrayList<T>) loadData(Class.forName(GameConstants.getInstance().getString(file.getName()+"Class")), file.getAbsolutePath()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    LogCenter.getInstance().getLogger().error(e);
                }
            }
                else{
                FileReader fileReader = null;
                try {
                    fileReader = new FileReader(file);
                    arr.add(gson.fromJson(fileReader,tClass));
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogCenter.getInstance().getLogger().error(e);
                }
            }
        }
        return arr;
    }

    public static DataManager getInstance() {
        if(dataManager == null){
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public  <T extends Character> ArrayList<T> getAllCharacter(Class<T> tClass){
        ArrayList<T> arr = new ArrayList<>();
        for(T t: (ArrayList<T>) dataMap.get(tClass)){
            arr.add((T) t.newOne());
        }
        return arr;
    }

    public <T extends Character> T getObject(Class<T> tClass, String name){
        for(T obj: (ArrayList<T>) dataMap.get(tClass)){
            if((obj.getName().trim()).equalsIgnoreCase(name))
                return (T) obj.newOne();
        }
        return null;
    }

    public ArrayList<Player> getAllPlayers() {
        return loadData(Player.class, playersPath);
    }

    public void deletePlayer(String username) {
        (new File(playersPath+File.separator+username)).delete();
    }

    public void savePlayer(Player player) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(playersPath+ File.separator+player.getUsername());
            gson.toJson(player,fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            LogCenter.getInstance().getLogger().error(e);
        }
    }

    public ArrayList<Card> getDefaultCards() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(generalPath+File.separator+"Default Cards"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogCenter.getInstance().getLogger().error(e);
        }
        ArrayList<Card> defaultCards = new ArrayList<>();
        while (scanner.hasNext()){
            String str=scanner.nextLine();
            defaultCards.add(getObject(Card.class, str));
        }
        return defaultCards;
    }

    public ArrayList<Hero> getDefaultHeroes() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(generalPath+File.separator+"Default Heroes"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogCenter.getInstance().getLogger().error(e);
        }
        ArrayList<Hero> defaultHeroes = new ArrayList<>();
        while (scanner.hasNext()){
            String str=scanner.nextLine();
            defaultHeroes.add(getObject(Hero.class, str));
        }
        return defaultHeroes;
    }

    public Deck getBot(){
        Random random = new Random();
        ArrayList<Hero> heroes = getAllCharacter(Hero.class);
        Deck deck = new Deck("", heroes.get(random.nextInt(heroes.size())));
        ArrayList<Card> cards = getAllCharacter(Card.class);
        int size = cards.size();
        int cnt = 0;
        for(int i = 0; i < size; i++, cnt++){
            Card card = cards.get(cnt);
            if(card.getHeroClass().equalsIgnoreCase(deck.getHero().getName())){
                try {
                    deck.addCard(card);
                    cards.remove(card);
                    cnt--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                if(!card.getHeroClass().equals("Neutral")){
                    cards.remove(card);
                    cnt--;
                }
            }
        }
        size = cards.size();
        int deckSize = deck.getCards().size();
        for(int i = 0; i < Math.min(deck.getHero().getDeckMax() - deckSize, size); i++){
            Card card = cards.get(random.nextInt(cards.size()));
            try {
                deck.addCard(card);
                cards.remove(card);
            } catch (Exception e) {
                LogCenter.getInstance().getLogger().error(e);
                e.printStackTrace();
            }
        }
        return deck;
    }

    public ArrayList<Deck> getDeckReaderDecks(){
        FileReader fileReader = null;
        DeckReader deckReader = null;
        try {
            fileReader = new FileReader(new File(GameConstants.getInstance().getString("DeckReaderAddress")));
            deckReader = gson.fromJson(fileReader, DeckReader.class);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Deck friend = new Deck("", getObject(Hero.class, "Mage"), true);
        Deck enemy = new Deck("", getObject(Hero.class, "Mage"), true);
        for(String cardName:deckReader.getFriend()){
            friend.addCardWithCheat(getObject(Card.class, cardName));
        }
        for(String cardName:deckReader.getEnemy()){
            enemy.addCardWithCheat(getObject(Card.class, cardName));
        }
        ArrayList<Deck> decks = new ArrayList<>();
        decks.add(friend);
        decks.add(enemy);
        return decks;
    }
}
