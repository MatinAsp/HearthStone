package Data;

import Log.LogCenter;
import Logic.Game;
import Models.Cards.Minion;
import Models.Cards.Spell;
import Models.Cards.Weapon;
import Models.Cards.Quest;
import Models.Hero;
import Models.Passive;
import Models.Player;
import Models.Cards.Card;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DataManager {
    static private DataManager dataManager = null;
    private String playersPath;
    private String minionsPath;
    private String spellsPath;
    private String weaponsPath;
    private String questsPath;
    private String heroesPath;
    private String generalPath;
    private String passivesPath;
    private Gson gson;

    private DataManager() throws IOException {
        GameConstants gameConstants = GameConstants.getInstance();
        playersPath = gameConstants.getString("playersPath");
        minionsPath = gameConstants.getString("minionsPath");
        spellsPath = gameConstants.getString("spellsPath");
        weaponsPath = gameConstants.getString("weaponsPath");
        questsPath = gameConstants.getString("questsPath");
        heroesPath = gameConstants.getString("heroesPath");
        generalPath = gameConstants.getString("generalPath");
        passivesPath = gameConstants.getString("passivesPath");
        gson = new Gson();
    }

    public static DataManager getInstance() throws IOException {
        if(dataManager == null){
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public Player getPlayer(String username) throws IOException {
        File file = new File(playersPath+File.separator+username);
        FileReader fileReader = new FileReader(file);
        Player player = gson.fromJson(fileReader,Player.class);
        fileReader.close();
        return player;
    }

    public void deletePlayer(String username) {
        (new File(playersPath+File.separator+username)).delete();
    }

    public Card getCard(String cardName) throws IOException {
        FileReader fileReader;
        Card card = null;
        File file = new File(minionsPath+File.separator+cardName);
        if(file.exists()){
            fileReader = new FileReader(file);
            card = gson.fromJson(fileReader,Minion.class);
            fileReader.close();
        }
        file = new File(spellsPath+File.separator+cardName);
        if(file.exists()){
            fileReader = new FileReader(file);
            card = gson.fromJson(fileReader,Spell.class);
            fileReader.close();
        }
        file = new File(weaponsPath+File.separator+cardName);
        if(file.exists()){
            fileReader = new FileReader(file);
            card = gson.fromJson(fileReader,Weapon.class);
            fileReader.close();
        }
        file = new File(questsPath+File.separator+cardName);
        if(file.exists()){
            fileReader = new FileReader(file);
            card = gson.fromJson(fileReader,Quest.class);
            fileReader.close();
        }
        return card;
    }

    public ArrayList<Player> getAllPlayers() throws IOException {
        ArrayList<Player> arr = new ArrayList<>();
        File[] playersDir = (new File(playersPath)).listFiles();
        for(File file: playersDir){
            FileReader fileReader = new FileReader(file);
            arr.add(gson.fromJson(fileReader,Player.class));
            fileReader.close();
        }
        return arr;
    }

    public ArrayList<Card> getMinions() throws IOException {
        ArrayList<Card> arr = new ArrayList<>();
        File[] minionsDir = (new File(minionsPath)).listFiles();
        for(File file: minionsDir){
            FileReader fileReader = new FileReader(file);
            arr.add(gson.fromJson(fileReader,Minion.class));
            fileReader.close();
        }
        return arr;
    }

    public ArrayList<Card> getSpells() throws IOException {
        ArrayList<Card> arr = new ArrayList<>();
        File[] spellsDir = (new File(spellsPath)).listFiles();
        for(File file: spellsDir){
            FileReader fileReader = new FileReader(file);
            arr.add(gson.fromJson(fileReader,Spell.class));
            fileReader.close();
        }
        return arr;
    }

    public ArrayList<Card> getWeapons() throws IOException {
        ArrayList<Card> arr = new ArrayList<>();
        File[] weaponsDir = (new File(weaponsPath)).listFiles();
        for(File file: weaponsDir){
            FileReader fileReader = new FileReader(file);
            arr.add(gson.fromJson(fileReader,Weapon.class));
            fileReader.close();
        }
        return arr;
    }

    public ArrayList<Card> getQuests() throws IOException {
        ArrayList<Card> arr = new ArrayList<>();
        File[] questsDir = (new File(questsPath)).listFiles();
        for(File file: questsDir){
            FileReader fileReader = new FileReader(file);
            arr.add(gson.fromJson(fileReader,Quest.class));
            fileReader.close();
        }
        return arr;
    }

    public ArrayList<Card> getAllCards() throws IOException {
        ArrayList<Card> arr = new ArrayList<>();
        arr.addAll(getMinions());
        arr.addAll(getSpells());
        arr.addAll(getWeapons());
        arr.addAll(getQuests());
        return arr;
    }

    public Hero getHero(String hero) throws IOException {
        FileReader fileReader = new FileReader(new File(heroesPath+File.separator+hero));
        Hero realHero = gson.fromJson(fileReader, Hero.class);
        fileReader.close();
        return realHero;
    }

    public ArrayList<Hero> getAllHeroes() throws IOException {
        ArrayList<Hero> arr = new ArrayList<>();
        File[] heroesDir = (new File(heroesPath)).listFiles();
        for(File file: heroesDir){
            FileReader fileReader = new FileReader(file);
            arr.add(gson.fromJson(fileReader,Hero.class));
            fileReader.close();
        }
        return arr;
    }

    public void savePlayer(Player player) throws IOException {
        FileWriter fileWriter = new FileWriter(playersPath+File.separator+player.getUsername());
        gson.toJson(player,fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

    public void addPlayer(Player player) throws IOException{
        FileWriter fileWriter = new FileWriter(playersPath+File.separator+player.getUsername());
        gson.toJson(player,fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

    public ArrayList<Card> getDefaultCards() throws IOException {
        Scanner scanner = new Scanner(new File(generalPath+File.separator+"Default Cards"));
        ArrayList<Card> defaultCards = new ArrayList<>();
        while (scanner.hasNext()){
            String str=scanner.nextLine();
            defaultCards.add(getCard(str));
        }
        return defaultCards;
    }

    public ArrayList<Hero> getDefaultHeroes() throws IOException {
        Scanner scanner = new Scanner(new File(generalPath+File.separator+"Default Heroes"));
        ArrayList<Hero> defaultHeroes = new ArrayList<>();
        while (scanner.hasNext()){
            String str=scanner.nextLine();
            defaultHeroes.add(getHero(str));
        }
        return defaultHeroes;
    }

    public Passive getPassive(String passive) throws IOException {
        FileReader fileReader = new FileReader(new File(passivesPath+File.separator+passive));
        Passive realPassive = gson.fromJson(fileReader, Passive.class);
        fileReader.close();
        return realPassive;
    }

    public ArrayList<Passive> getAllPassives() throws IOException {
        ArrayList<Passive> arr = new ArrayList<>();
        File[] passivesDir = (new File(passivesPath)).listFiles();
        for(File file: passivesDir){
            FileReader fileReader = new FileReader(file);
            arr.add(gson.fromJson(fileReader,Passive.class));
            fileReader.close();
        }
        return arr;
    }
}
