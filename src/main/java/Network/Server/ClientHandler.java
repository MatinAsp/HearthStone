package Network.Server;

import Logic.PlayersManager;

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
    private String username = null;
    private Scanner scanner;
    private PrintStream printStream;
    public ClientHandler(InputStream inputStream, OutputStream outputStream, Server server){
        this.server = server;
        scanner = new Scanner(inputStream);
        printStream = new PrintStream(outputStream);
    }

    @Override
    public void run() {
        while(!isInterrupted()){
            ArrayList<String> massagesList = new ArrayList<>();
            String string = scanner.nextLine();
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
            if(username == null && !methodName.equalsIgnoreCase("logIn") && !methodName.equalsIgnoreCase("signIn")){
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
                        //todo
                        e.printStackTrace();
                    }
                    break;
                }
            }
            /////
            if(string.equals("log in")){
                logIn();
            }
            switch (scanner.nextLine()){
                case "status":
                    send(new String[]{"status", server.getAllPlayersList(), server.getOnLines()});
                    break;
                case "play":
                    server.startGame(this);
                    break;
                case "move":
                    server.moveInGame(this, Integer.parseInt(scanner.nextLine()));
                    break;
                case "exit":
                    server.exitClient(this);
                    return;
            }
        }
    }

    private void logIn(String username, String password) {
        try{
            PlayersManager.getInstance().logIn(username, password);
        }catch (Exception e){
            sendError(e.getMessage());
            return;
        }
        this.username = username;
        send(new String[]{"logIn"});
    }

    private void signIn(String username, String password) {
        try{
            PlayersManager.getInstance().signIn(username, password);
        }catch (Exception e){
            sendError(e.getMessage());
            return;
        }
        this.username = username;
        send(new String[]{"logIn"});
    }

    public String getUsername() {
        return username;
    }

    public void sendError(String error){
        send(new String[]{"error", error});
    }

    public synchronized void send(String[] massages){
        String finalMassage = massages[0];
        for(int i = 1; i < massages.length; i++){
            finalMassage += "," + massages[i];
        }
        printStream.println(finalMassage);
        printStream.flush();
    }
}
