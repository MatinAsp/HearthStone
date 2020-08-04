package Network.Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class ClientHandler extends Thread{
    private Server server;
    private String token = null;
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
            String string = scanner.nextLine();
            if(string.equals("log in")){
                logIn();
            }
            if(string.equals("exit")){

            }
            if(string.equals(token) && token != null){
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
    }

    private void logIn() {
        String username = scanner.nextLine();
        String password = scanner.nextLine();
        token = server.logIn(username, password, this);
        if(!token.equals(null)){
            this.username = username;
            send(new String[]{"log in", token});
        }
    }

    public String getUsername() {
        return username;
    }

    public synchronized void send(String[] massages){
        for(String massage: massages){
            printStream.println(massage);
        }
        printStream.flush();
    }
}
