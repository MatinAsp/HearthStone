package Network.Client;

import Graphics.Controller;
import Log.LogCenter;
import Models.Player;
import Network.Server.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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

    public void sendSignInRequest(String username, String password){
        send(new String[]{"signIn", username, password});
    }

    private void logIn(){
        controller.logInActionUpdate();
    }

    private void signIn(){
        System.out.println(11111111);
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
        ArrayList<String> massagesList = new ArrayList<>();
        massagesList.addAll(Arrays.asList(massages));
        printStream.println(gson.toJson(massagesList));
        printStream.flush();
        System.out.println("send: "+gson.toJson(massagesList));
    }

    public void getMassage(String string) {
        ArrayList<String> massagesList = gson.fromJson(string, new TypeToken<ArrayList<String>>(){}.getType());
        if(massagesList.get(0).equalsIgnoreCase("null")) player = null;
        else player = gson.fromJson(massagesList.get(0), Player.class);
        String methodName = massagesList.get(1);
        massagesList.remove(0);
        massagesList.remove(0);
        for(Method method: Client.class.getDeclaredMethods()){
            if(method.getName().equals(methodName)){
                System.out.println(methodName);
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
