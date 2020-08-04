package Network.Client;

import Graphics.Controller;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Thread{
    private Socket socket;
    private PrintStream printStream;
    private Receiver receiver;
    private String token;
    private Controller controller;

    public Client(String serverIP, int serverPort, Controller controller) throws IOException {
        this.controller = controller;
        socket = new Socket(serverIP, serverPort);
        printStream = new PrintStream(socket.getOutputStream());
        receiver = new Receiver(this, socket.getInputStream());
        token = null;
    }

    @Override
    public void run() {
        try {
            receiver = new Receiver(this, socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiver.start();
        while (receiver.isAlive()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateStatus(ArrayList<Account> accounts, ArrayList<String> onLines){
        controller.updateStatus(accounts, onLines);
    }

    public void sendPlayRequest(){
        send(new String[]{token, "play"});
    }

    public void startGame(){
        send(new String[]{token, "play"});
    }

    public void sendGameMoveRequest(int move){
        send(new String[]{token, "move", String.valueOf(move)});
    }

    public void updateGameState(String state){
        controller.updateGame(state, true);
    }

    public void sendLogInRequest(String username, String password){
        send(new String[]{"log in", username, password});
    }

    public void updateLogInState(String token){
        this.token = token;
        controller.goToMenu();
    }

    public void stopRunning() {
        send(new String[]{token, "exit"});
    }

    private synchronized void send(String[] massages){
        for(String massage: massages){
            printStream.println(massage);
        }
        printStream.flush();
    }
}
