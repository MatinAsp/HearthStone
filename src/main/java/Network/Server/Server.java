package Network.Server;

import Logic.Game;
import Models.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class Server extends Thread{
    private static Server server;
    private static int defaultPort = 8080;
    private ArrayList<Player> players;
    private ArrayList<ClientHandler> clientHandlers;
    private ArrayList<ClientHandler>  waitingList;
    private ServerSocket serverSocket;
    private HashMap<ClientHandler, Game> gameMap;

    private Server(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        gameMap = new HashMap<>();
        clientHandlers = new ArrayList<>();
        waitingList = new ArrayList<>();
        players = gson.fromJson(new FileReader(new File(Configs.getInstance().readString("playersPath"))), new TypeToken<ArrayList<Player>>(){}.getType());
    }

    public synchronized static Server getInstance() throws IOException {
        return getInstance(-1);
    }

    public synchronized static Server getInstance(int port) throws IOException {
        if(port == -1) port = defaultPort;
        if(server == null){
            server = new Server(port);
        }
        return server;
    }

    @Override
    public void run() {
        new Thread(()->{
            while (!isInterrupted()){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(Configs.getInstance().readString("playersPath"));
                    gson.toJson(players,fileWriter);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) { }
            }
        }).start();
        while(!isInterrupted()){
            try {
                Socket socket =serverSocket.accept();
                new ClientHandler(socket.getInputStream(), socket.getOutputStream(), this).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized String logIn(String username, String password, ClientHandler clientHandler) {
        for(Player player: players){
            if(username.equals(player.getUsername())){
                if(player.isPasswordOk(password)){
                    clientHandlers.add(clientHandler);
                    return Integer.toString(new SecureRandom().nextInt());
                }
                else{
                    return null;
                }
            }
        }
        players.add(new Player(username, password));
        clientHandlers.add(clientHandler);
        return Integer.toString(new SecureRandom().nextInt());
    }

    public synchronized String getOnLines() {
        TreeSet<String> treeSet = new TreeSet<>();
        for(ClientHandler clientHandler: clientHandlers){
            treeSet.add(clientHandler.getUsername());
        }
        return gson.toJson(treeSet);
    }

    public synchronized void moveInGame(ClientHandler clientHandler, int move) {
        Game game = gameMap.get(clientHandler);
        ClientHandler enemyClientHandler = null;
        for(ClientHandler clientHandler1: gameMap.keySet()){
            if(game == gameMap.get(clientHandler1) && clientHandler1 != clientHandler){
                enemyClientHandler = clientHandler1;
                break;
            }
        }
        try {
            game.move(game.getClientHandlerIndex(clientHandler), move);
        } catch (InvalidMoveException ignore) {}
        catch (GameOverException e) {
            recordWinning(clientHandler.getUsername());
            gameMap.remove(clientHandler);
            gameMap.remove(enemyClientHandler);
        }
        clientHandler.send(new String[]{"state", "" + game.getClientHandlerIndex(clientHandler) + game.getGameState()});
        enemyClientHandler.send(new String[]{"state", "" + game.getClientHandlerIndex(enemyClientHandler) + game.getGameState()});
    }

    private void recordWinning(String username) {
        for(Player player: players){
            if(player.getUsername().equals(username)){
                player.setScore(player.getScore() + 1);
                break;
            }
        }
    }

    public synchronized void startGame(ClientHandler clientHandler){
        waitingList.add(clientHandler);
        if(waitingList.size() > 1){
            ClientHandler clientHandler1 = waitingList.get(0), clientHandler2 = waitingList.get(1);
            Game game = new Game(clientHandler1, clientHandler2);
            gameMap.put(clientHandler1, game);
            gameMap.put(clientHandler2, game);
            waitingList.remove(clientHandler1);
            waitingList.remove(clientHandler2);
            clientHandler1.send(new String[]{"state", "" + game.getClientHandlerIndex(clientHandler1) + game.getGameState()});
            clientHandler2.send(new String[]{"state", "" + game.getClientHandlerIndex(clientHandler2) + game.getGameState()});
        }
    }

    public void exitClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        clientHandler.send(new String[]{"exit"});
    }

}
