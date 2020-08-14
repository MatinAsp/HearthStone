package Data;

import Log.LogCenter;

import java.io.*;
import java.util.List;

public class GameConstants {
    private static GameConstants gameConstants = null;
    private static String defaultAddress =
            "src"+File.separator+"main"+File.separator+"resources"+File.separator+"Configs.properties";
    private Configs configs;

    private GameConstants(String address) {
        configs = new Configs();
        try {
            configs.load(new FileReader(new File(address)));
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
    }

    public synchronized static GameConstants getInstance() {
        return getInstance("default");
    }

    public synchronized static GameConstants getInstance(String address) {
        if(gameConstants == null){
            if(address.equals("default")){
                address = defaultAddress;
            }
            gameConstants = new GameConstants(address);
            defaultAddress = address;
        }
        return gameConstants;
    }

    public synchronized int getInteger (String name){
        return configs.readInteger(name);
    }

    public synchronized boolean getBoolean (String name){
        return configs.readBoolean(name);
    }

    public synchronized String getString (String name){
        return configs.getProperty(name);
    }

    public synchronized double getDouble(String name) {
        return configs.readDouble(name);
    }

    public synchronized List<String> getStingList(String name) {
        return configs.readStringList(name);
    }

    public synchronized List<Integer> getIntegerList(String name) {
        return configs.readIntegerList(name);
    }

    public synchronized void setProperty(String key, String newValue){
        configs.setProperty(key, newValue);
    }

    public synchronized void save() {
        try {
            configs.store(new FileOutputStream(defaultAddress), "");
        } catch (IOException e) {
            e.printStackTrace();
//            LogCenter.getInstance().getLogger().error(e);
        }
    }
}
