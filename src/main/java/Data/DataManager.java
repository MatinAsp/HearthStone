package Data;

import Models.Character;
import Models.Hero;
import Models.Player;
import Models.Cards.Card;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DataManager {
    static private DataManager dataManager = null;
    private HashMap<Class, ArrayList> dataMap;
    private String playersPath;
    private String generalPath;
    private Gson gson;

    private DataManager() throws IOException {
        GameConstants gameConstants = GameConstants.getInstance();
        playersPath = gameConstants.getString("playersPath");
        generalPath = gameConstants.getString("generalPath");
        gson = new Gson();
        dataMap = new HashMap<>();
    }

    private <T> ArrayList<T> loadData(Class<T> tClass, String address, String specialOne) throws IOException, ClassNotFoundException {
        ArrayList<T> arr = new ArrayList<>();
        File[] dir = (new File(address)).listFiles();
        for(File file: dir){
            if(file.isDirectory()){
                arr.addAll((ArrayList<T>) loadData(Class.forName(GameConstants.getInstance().getString(file.getName()+"Class")), file.getAbsolutePath(), specialOne));
            }
            else if(specialOne == null || specialOne.equalsIgnoreCase(file.getName())){
                FileReader fileReader = new FileReader(file);
                arr.add(gson.fromJson(fileReader,tClass));
                fileReader.close();
            }
        }
        return arr;
    }

    public static DataManager getInstance() throws IOException {
        if(dataManager == null){
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public <T> ArrayList<T> getAll(Class<T> tClass){
        try {
            return loadData(tClass, GameConstants.getInstance().getString(tClass.getSimpleName().toLowerCase()+"Path"), null);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T extends Character> T getObject(Class<T> tClass, String name){
        try {
            return loadData(tClass, GameConstants.getInstance().getString(tClass.getSimpleName().toLowerCase()+"Path"), name).get(0);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Player getPlayer(String username) {
        for(Player player: (ArrayList<Player>) dataMap.get(Player.class)){
            if(player.getUsername().equals(username)){
                return player;
            }
        }
        return null;
    }

    public void deletePlayer(String username) {
        (new File(playersPath+File.separator+username)).delete();
    }

    public void savePlayer(Player player) throws IOException {
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
            defaultCards.add(getObject(Card.class, str));
        }
        return defaultCards;
    }

    public ArrayList<Hero> getDefaultHeroes() throws IOException {
        Scanner scanner = new Scanner(new File(generalPath+File.separator+"Default Heroes"));
        ArrayList<Hero> defaultHeroes = new ArrayList<>();
        while (scanner.hasNext()){
            String str=scanner.nextLine();
            defaultHeroes.add(getObject(Hero.class, str));
        }
        return defaultHeroes;
    }
}
