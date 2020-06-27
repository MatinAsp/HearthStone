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

    private DataManager() throws IOException {
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

    private <T> ArrayList<T> loadData(Class<T> tClass, String address) throws IOException, ClassNotFoundException {
        ArrayList<T> arr = new ArrayList<>();
        File[] dir = (new File(address)).listFiles();
        for(File file: dir){
            if(file.isDirectory()){
                arr.addAll((ArrayList<T>) loadData(Class.forName(GameConstants.getInstance().getString(file.getName()+"Class")), file.getAbsolutePath()));
            }
                else{
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
        try {
            return loadData(Player.class, playersPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
