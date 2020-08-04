package Network.Server;


import Data.GameConstants;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] arg) throws IOException {
        try {
            Server.getInstance(GameConstants.getInstance().getInteger("port")).start();
        }catch (Exception e){
            Server.getInstance().start();
        }
    }
}
