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
            if(string.equalsIgnoreCase("exit")){
                //todo
            }
            client.getMassage(string);
        }
    }
}
