package Network.Client;

import Models.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Receiver extends Thread{
    private Client client;
    private Scanner scanner;
    private Gson gson;

    public Receiver(Client client, InputStream inputStream){
        this.client = client;
        scanner = new Scanner(inputStream);
        gson = new Gson();
    }

    @Override
    public void run() {
        while (!isInterrupted()){
            String string = scanner.nextLine();
            switch(string){
                case "status":
                    getStatus();
                    break;
                case "log in":
                    logIn();
                    break;
                case "state":
                    getGameState();
                    break;
                case "exit":
                    return;
            }
        }
    }

    private void getGameState() {
        client.updateGameState(scanner.nextLine());
    }

    private void logIn() {
        client.updateLogInState(scanner.nextLine());
    }

    private void getStatus() {
        ArrayList<Player> accounts = gson.fromJson(scanner.nextLine(), new TypeToken<ArrayList<Player>>(){}.getType());
        ArrayList<String> onLines = gson.fromJson(scanner.nextLine(), new TypeToken<ArrayList<String>>(){}.getType());
        client.updateStatus(accounts, onLines);
    }
}
