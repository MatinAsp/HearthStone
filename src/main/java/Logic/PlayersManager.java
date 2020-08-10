package Logic;

import Data.DataManager;
import Models.Player;

import java.util.ArrayList;

public class PlayersManager {
    private static PlayersManager instancePlayersManager= null;
    private ArrayList<Player> allPlayers;
    private Object lock = new Object();
    private PlayersManager() {
        DataManager dataManager = DataManager.getInstance();
        allPlayers = new ArrayList<>();
        allPlayers.addAll(dataManager.getAllPlayers());
    }

    private Player getPlayer(String username){
        for(Player player: allPlayers){
            if(player.getUsername().equals(username)){
                return player;
            }
        }
        return null;
    }

    static public PlayersManager getInstance() {
        if(instancePlayersManager == null){
            instancePlayersManager = new PlayersManager();
        }
        return instancePlayersManager;
    }

    public Player logIn(String username, String password) throws Exception{
        checkUsername(username);
        checkPassword(username, password);
        return getPlayer(username);
    }

    public void checkUsername(String username) throws Exception {
        if(username == null || !exist(username) || username.equals("")){
            throw new Exception("Username dose not exist.");
        }
    }

    public void checkPassword(String username, String password) throws Exception {
        Player player = getPlayer(username);
        if(password == null || password.equals("") || Integer.parseInt(player.getPassword()) != password.hashCode()){
            throw new Exception("wrong username or password.");
        }
    }

    public Player signIn(String username, String password) throws Exception {
        if(username == null || username.equals("")){
            throw new Exception("please enter your username.");
        }
        if(exist(username)){
            throw new Exception("this username is already taken.");
        }
        if(password.equals("")){
            throw new Exception("please enter your password.");
        }
        Player player = PlayerFactory.getInstance().build(username, password);
        allPlayers.add(player);
        DataManager.getInstance().savePlayer(player);
        return player;
    }

    public boolean exist(String username){
        for(Player player: allPlayers){
            if(player.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public void deletePlayer(String username, String password) throws Exception {
        Player player = getPlayer(username);
        if (password.hashCode() == Integer.parseInt(player.getPassword())) {
            allPlayers.remove(player);
            DataManager dataManager = DataManager.getInstance();
            dataManager.deletePlayer(player.getUsername());
            return;
        }
        else throw new Exception("Wrong Password");
    }

    public void save() {
        synchronized (lock){
            for(Player player: allPlayers){
                DataManager.getInstance().savePlayer(player);
            }
        }
    }

    public void changePlayer(Player player1, Player player2){
        synchronized (lock){
            allPlayers.remove(player1);
            allPlayers.add(player2);
        }
    }
}
