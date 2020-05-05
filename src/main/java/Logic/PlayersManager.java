package Logic;

import Data.DataManager;
import Log.LogCenter;
import Models.Player;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PlayersManager {
    private static PlayersManager instancePlayersManager= null;
    private ArrayList<Player> allPlayers;
    private Player currentPlayer = null;
    private PlayersManager() throws IOException {
        DataManager dataManager = DataManager.getInstance();
        allPlayers = new ArrayList<>();
        allPlayers.addAll(dataManager.getAllPlayers());
    }

    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    public Player getPlayer(String username){
        for(Player player: allPlayers){
            if(player.getUsername().equals(username)){
                return player;
            }
        }
        return null;
    }

    static public PlayersManager getInstance() throws IOException {
        if(instancePlayersManager == null){
            instancePlayersManager = new PlayersManager();
        }
        return instancePlayersManager;
    }

    public void logIn(String username, String password) throws Exception{
        if(username == null || !exist(username) || username.equals("")){
            throw new Exception("wrong username or password.");
        }
        Player player = getPlayer(username);
        if(password == null || password.equals("") || Integer.parseInt(player.getPassword()) != password.hashCode()){
            throw new Exception("wrong username or password.");
        }
        currentPlayer = player;
        LogCenter logCenter = LogCenter.getInstance();
        logCenter.setLogFile(currentPlayer);
    }

    public void signIn(String username, String password) throws Exception {
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
        currentPlayer = player;
        DataManager.getInstance().addPlayer(player);
        LogCenter logCenter = LogCenter.getInstance();
        logCenter.createLogFile(player);
        logCenter.setLogFile(currentPlayer);
    }

    public boolean exist(String username){
        for(Player player: allPlayers){
            if(player.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public void deleteCurrentPlayer(String password) throws Exception {
        if (password.hashCode() == Integer.parseInt(currentPlayer.getPassword())) {
            allPlayers.remove(currentPlayer);
            DataManager dataManager = DataManager.getInstance();
            dataManager.deletePlayer(currentPlayer.getUsername());
            currentPlayer = null;
            LogCenter.getInstance().getLogger().info("USER_DELETED");
            return;
        }
        else throw new Exception("Wrong Password");
    }
}
