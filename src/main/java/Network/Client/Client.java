package Network.Client;

import Graphics.Controller;
import Network.Server.ClientHandler;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

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
        send(new String[]{"logIn", username, password});
    }

    private void logIn(){
        controller.logInActionUpdate();
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

    public void getMassage(String string) {
        ArrayList<String> massagesList = new ArrayList<>();
        int st = 0, ed = 1;
        for(; ed < string.length(); ed++){
            if(string.charAt(ed) == ','){{
                massagesList.add(string.substring(st,ed));
                st = ed + 1;
            }}
        }
        massagesList.add(string.substring(st, ed));
        String methodName = massagesList.get(0);
        massagesList.remove(0);
        for(Method method: ClientHandler.class.getDeclaredMethods()){
            if(method.getName().equalsIgnoreCase(methodName)){
                try{
                    if(massagesList.size() == 0){
                        method.invoke(this);
                    }
                    else {
                        method.invoke(this, massagesList.toArray());
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    //todo
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
