package Network.Server;


import Data.GameConstants;
import Models.Deck;
import Models.Hero;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

public class ServerMain {
    public static void main(String[] arg) throws IOException {
        try {
            Server.getInstance(GameConstants.getInstance().getInteger("port")).start();
        }catch (Exception e){
            Server.getInstance().start();
        }
    }
}
