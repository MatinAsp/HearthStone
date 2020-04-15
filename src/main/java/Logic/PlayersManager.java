package Logic;

import Logic.Models.Player;
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

    public void login(){
        String username = null, password = null;
        Scanner scanner = new Scanner(System.in);
        while (username == null){
            System.out.print("username: ");
            username = scanner.nextLine();
            if(!exist(username) || username.equals("")){
                System.out.println("invalid username");
                username = null;
                continue;
            }
        }
        Player player = getPlayer(username);
        while(password == null){
            System.out.print("password: ");
            password = scanner.nextLine();
            if(password.equals("") || Integer.parseInt(player.getPassword()) != password.hashCode()){
                System.out.println("invalid password");
                password = null;
                continue;
            }
        }
        currentPlayer = player;
        LogCenter logCenter = LogCenter.getInstance();
        logCenter.setLogFile(currentPlayer);
        Logger logger = logCenter.getLogger();
        logger.info("log_in");
    }

    public void singin() throws IOException {
        String username = null, password = null;
        Scanner scanner = new Scanner(System.in);
        while (username == null){
            System.out.print("username: ");
            username = scanner.nextLine();
            if(exist(username) || username.equals("")){
                System.out.println("this username has been already taken or it's invalid.");
                username = null;
                continue;
            }
        }
        while(password == null || password.equals("")){
            System.out.print("password: ");
            password = scanner.nextLine();
            if(password.equals("")){
                System.out.println("invalid password");
                continue;
            }
        }
        Player player = new Player(username, password);
        allPlayers.add(player);
        currentPlayer = player;
        LogCenter logCenter = LogCenter.getInstance();
        logCenter.createLogFile(player);
        logCenter.setLogFile(currentPlayer);
        Logger logger = logCenter.getLogger();
        logger.info("sign_in");
    }

    public boolean exist(String username){
        for(Player player: allPlayers){
            if(player.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public void deleteCurrentPlayer() throws IOException {
        String answer;
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.print("password: ");
            answer = scanner.nextLine();
            if (answer.hashCode() == Integer.parseInt(currentPlayer.getPassword())){
                allPlayers.remove(currentPlayer);
                DataManager dataManager = DataManager.getInstance();
                dataManager.deletePlayer(currentPlayer.getUsername());
                currentPlayer = null;
                System.out.println("deleted!");
                return;
            }
            else {
                Logger logger = LogCenter.getInstance().getLogger();
                logger.error("wrong_password");
                System.out.println("wrong password");
            }
        }
    }
}
