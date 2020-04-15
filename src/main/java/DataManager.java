import Logic.Models.Cards.Minion;
import Logic.Models.Cards.Spell;
import Logic.Models.Cards.Weapon;
import Logic.Models.Cards.Quest;
import Logic.Models.Hero;
import Logic.Models.Player;
import Logic.Models.Cards.Card;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class DataManager {
    static private DataManager dataManager = null;
    private String dataPath = "src"+File.separator+"Data";
    private String playersPath = dataPath+File.separator+"Players";
    private String minionsPath = dataPath+File.separator+"Cards"+File.separator+"Minions";
    private String spellsPath = dataPath+File.separator+"Cards"+File.separator+"Spells";
    private String weaponsPath = dataPath+File.separator+"Cards"+File.separator+"Weapons";
    private String questsPath = dataPath+File.separator+"Cards"+File.separator+"Quests";
    private String heroesPath = dataPath+File.separator+"Heroes";
    private Gson gson;

    private DataManager(){
        gson = new Gson();
    }

    public static DataManager getInstance() {
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
        LogCenter logCenter = LogCenter.getInstance();
        Logger logger = logCenter.getLogger();
        logger.info("USER_DELETED");
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
        FileWriter fileWriter = new FileWriter(dataPath+File.separator+"Players"+File.separator+player.getUsername());
        gson.toJson(player,fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }
}
