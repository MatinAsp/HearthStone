package Network.Server;

import Logic.Competitor;
import Logic.Game;
import Logic.GameFactory;
import Logic.PlayersManager;
import Models.Player;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Server extends Thread{
    private static Server server;
    private static int defaultPort = 8080;
    private ArrayList<ClientHandler> clientHandlers;
    private ArrayList<ClientHandler>  waitingList;
    private ServerSocket serverSocket;
    private HashMap<ClientHandler, Game> gameMap;
    private HashMap<Game, String> gameKindMap;

    private Server(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        gameMap = new HashMap<>();
        clientHandlers = new ArrayList<>();
        waitingList = new ArrayList<>();
        gameKindMap = new HashMap<>();
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
        Thread thread = new Thread(()->{
            while (!isInterrupted()){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
                PlayersManager.getInstance().save();
            }
        });
        thread.start();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
           while (!isInterrupted()){
               if(scanner.nextLine().equals("exit")){
                   thread.interrupt();
                   try {
                       serverSocket.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   break;
               }
           }
        }).start();
        while(!isInterrupted() && !serverSocket.isClosed()){
            try {
                Socket socket = serverSocket.accept();
                ClientHandler  clientHandler = new ClientHandler(socket.getInputStream(), socket.getOutputStream(), this);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void moveInGame(ClientHandler clientHandler, int move) {
//        Game game = gameMap.get(clientHandler);
//        ClientHandler enemyClientHandler = null;
//        for(ClientHandler clientHandler1: gameMap.keySet()){
//            if(game == gameMap.get(clientHandler1) && clientHandler1 != clientHandler){
//                enemyClientHandler = clientHandler1;
//                break;
//            }
//        }
//        try {
//            game.move(game.getClientHandlerIndex(clientHandler), move);
//        } catch (InvalidMoveException ignore) {}
//        catch (GameOverException e) {
//            recordWinning(clientHandler.getUsername());
//            gameMap.remove(clientHandler);
//            gameMap.remove(enemyClientHandler);
//        }
//        clientHandler.send(new String[]{"state", "" + game.getClientHandlerIndex(clientHandler) + game.getGameState()});
//        enemyClientHandler.send(new String[]{"state", "" + game.getClientHandlerIndex(enemyClientHandler) + game.getGameState()});
    }

    private void checkForDeck(ClientHandler clientHandler) throws Exception {
        if(clientHandler.getPlayer().getCurrentDeckName() == null){
            throw new Exception("Please Select Your Deck To Start.");
        }
    }
    public synchronized void startOnlineGame(ClientHandler clientHandler1) throws Exception {
        checkForDeck(clientHandler1);
        boolean check = false;
        for(ClientHandler clientHandler2: waitingList){
            if(isOkForPlay(clientHandler1, clientHandler2)){
                check = true;
                Player player1 = clientHandler1.getPlayer();
                Player player2 = clientHandler2.getPlayer();
                Game game = GameFactory.getInstance().build(
                        player1.getUsername(),
                        player2.getUsername(),
                        player1.getDeck(player1.getCurrentDeckName()),
                        player2.getDeck(player2.getCurrentDeckName()),
                        false
                );
                gameMap.put(clientHandler1, game);
                gameMap.put(clientHandler2, game);
                waitingList.remove(clientHandler2);
                gameKindMap.put(game, "online");
                Gson gson = new Gson();
                Competitor competitor1 = game.getCompetitor(player1.getUsername());
                Competitor competitor2 = game.getCompetitor(player2.getUsername());
                clientHandler1.send(new String[]{"startGame", gson.toJson(GameFactory.getInstance().getPrivateGame(competitor1, competitor2))});
                clientHandler2.send(new String[]{"startGame", gson.toJson(GameFactory.getInstance().getPrivateGame(competitor2, competitor1))});
                break;
            }
        }
        if(!check) waitingList.add(clientHandler1);
    }

    private boolean isOkForPlay(ClientHandler clientHandler1, ClientHandler clientHandler2) {
        //todo
        return true;
    }

    public void exitClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        clientHandler.send(new String[]{"exit"});
    }

}
