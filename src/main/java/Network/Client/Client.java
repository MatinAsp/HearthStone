package Network.Client;

import Graphics.Controller;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Client extends Thread{
    private Socket socket;
    private PrintStream printStream;
    private Receiver receiver;
    private Controller controller;

    public Client(String serverIP, int serverPort, Controller controller) throws IOException {
        this.controller = controller;
        socket = new Socket(serverIP, serverPort);
        printStream = new PrintStream(socket.getOutputStream());
        receiver = new Receiver(this, socket.getInputStream());
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

    public void sendLogInRequest(String username, String password){
        send(new String[]{"log in", username, password});
    }

    public void stopRunning() {
        send(new String[]{"exit"});
    }

    private synchronized void send(String[] massages){
        String finalMassage = massages[0];
        for(int i = 1; i < massages.length; i++){
            finalMassage += "," + massages[i];
        }
        printStream.println(finalMassage);
        printStream.flush();
    }
}
