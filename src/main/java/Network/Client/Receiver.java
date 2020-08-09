package Network.Client;

import com.google.gson.Gson;

import java.io.InputStream;
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
            System.out.println("get: "+string);
            if(string.equalsIgnoreCase("exit")){
                return;
            }
            client.getMassage(string);
        }
    }
}
