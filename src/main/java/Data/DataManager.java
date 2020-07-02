package Data;

import Models.Cards.*;
import Models.Character;
import Models.Hero;
import Models.Player;
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

    private DataManager(){
        GameConstants gameConstants = GameConstants.getInstance();
        playersPath = gameConstants.getString("playerPath");
        generalPath = gameConstants.getString("generalPath");
        gson = new Gson();
        dataMap = new HashMap<>();
        initialize();
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
            if((obj.getName()).equals(name))
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
        }
    }

    public ArrayList<Card> getDefaultCards() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(generalPath+File.separator+"Default Cards"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        }
        ArrayList<Hero> defaultHeroes = new ArrayList<>();
        while (scanner.hasNext()){
            String str=scanner.nextLine();
            defaultHeroes.add(getObject(Hero.class, str));
        }
        return defaultHeroes;
    }
}
