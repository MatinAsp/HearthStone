package Network.Server;

import Logic.PlayersManager;
import Models.Player;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class ClientHandler extends Thread{
    private Server server;
    private Player player = null;
    private Scanner scanner;
    private PrintStream printStream;
    private Gson gson;
    public ClientHandler(InputStream inputStream, OutputStream outputStream, Server server){
        this.server = server;
        scanner = new Scanner(inputStream);
        printStream = new PrintStream(outputStream);
        gson = new Gson();
    }

    @Override
    public void run() {
        while(!isInterrupted()){
            String string = scanner.nextLine();
            ArrayList<String> massagesList = toListMassages(string);
            String methodName = massagesList.get(0);
            massagesList.remove(0);
            if(player == null && !methodName.equalsIgnoreCase("logIn") && !methodName.equalsIgnoreCase("signIn")){
                continue;
            }
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
                        sendException((Exception) e.getCause());
                    }
                    break;
                }
            }
        }
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

    private void logIn(String username, String password) throws Exception {
        player = PlayersManager.getInstance().logIn(username, password);
        send(new String[]{"logIn"});
    }

    private void signIn(String username, String password) throws Exception {
        player = PlayersManager.getInstance().signIn(username, password);
        send(new String[]{"logIn"});
    }

    public void sendException(Exception exception){
        send(new String[]{"error", exception.getClass().getName(), gson.toJson(exception)});
    }

    public synchronized void send(String[] massages){
        String finalMassage = "null";
        if(player != null) finalMassage = gson.toJson(player);
        for(int i = 0; i < massages.length; i++){
            finalMassage += "," + massages[i];
        }
        printStream.println(finalMassage);
        printStream.flush();
    }
}
