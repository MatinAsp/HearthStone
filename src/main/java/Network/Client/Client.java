package Network.Client;

import Graphics.Controller;
import Log.LogCenter;
import Models.Player;
import Network.Server.ClientHandler;
import com.google.gson.Gson;

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
    private Player player = null;
    private Gson gson;

    public Client(String serverIP, int serverPort, Controller controller) throws IOException {
        this.controller = controller;
        socket = new Socket(serverIP, serverPort);
        printStream = new PrintStream(socket.getOutputStream());
        receiver = new Receiver(this, socket.getInputStream());
        gson = new Gson();
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
        LogCenter.getInstance().createLogFile(player);
        controller.logInActionUpdate();
    }

    public void stopRunning() {
        send(new String[]{"exit"});
    }

    private void error(String className, String gsonString){
        try {
            Exception exception = (Exception) gson.fromJson(gsonString, Class.forName(className));
            //todo
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private synchronized void send(String[] massages){
        String finalMassage = massages[0];
        for(int i = 1; i < massages.length; i++){
            finalMassage += "," + massages[i];
        }
        printStream.println(finalMassage);
        printStream.flush();
    }

    private ArrayList<String> toListMassages(String string){
        ArrayList<String> massagesList = new ArrayList<>();
        int st = 0, ed = 1;
        for(; ed < string.length(); ed++){
            if(string.charAt(ed) == ','){{
                massagesList.add(string.substring(st,ed));
                st = ed + 1;
            }}
        }
        massagesList.add(string.substring(st, ed));
        return massagesList;
    }

    public void getMassage(String string) {
        ArrayList<String> massagesList = toListMassages(string);
        if(massagesList.get(0).equalsIgnoreCase("null")) player = null;
        else player = gson.fromJson(massagesList.get(0), Player.class);
        String methodName = massagesList.get(1);
        massagesList.remove(0);
        massagesList.remove(1);
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

    public void logInfo(String massage){
        LogCenter.getInstance().info(player, massage);
    }

    public void logError(String massage){
        LogCenter.getInstance().error(player, massage);
    }

    public void logError(Exception massage){
        LogCenter.getInstance().error(player, massage);
    }

    public Player getPlayer(){
        return player;
    }
}
