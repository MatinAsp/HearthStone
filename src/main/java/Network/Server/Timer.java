package Network.Server;


import Exceptions.GameOverException;
import Logic.Game;

public class Timer extends Thread{
    private int time;
    private boolean isEnded;
    private Server server;
    private ClientHandler clientHandler;
    private Game game;

    public Timer(int time, Server server, ClientHandler clientHandler, Game game){
        this.time = time;
        isEnded = false;
        this.server = server;
        this.clientHandler = clientHandler;
        this.game = game;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }

    @Override
    public void run() {
        while (time-- > 0 && !isEnded){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!isEnded) {
            try {
                server.executeEndTurn(game);
            } catch (GameOverException e) {
                server.endGame(clientHandler, true);
            }
        }
    }
}
