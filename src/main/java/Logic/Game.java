package Logic;


import org.apache.log4j.*;

import java.io.*;
import java.util.Scanner;

public class Game {
    static public boolean run;
    static public Store store;
    static public Collections collections;
    static public PlayersManager playersManager;
    public static void main(String[] arg) throws IOException {
        System.out.println("Welcome To Hearthstone.");
        run = true;
        store = Store.getInstance();
        collections = Collections.getInstance();
        playersManager = PlayersManager.getInstance();
        mainCLI();
    }
    static void mainCLI() throws IOException {
        System.out.print("already have an account?(y/n) ");
        Scanner scanner = new Scanner(System.in);
        String answer = null;
        while(true){
            answer = scanner.nextLine();
            if(!answer.equals("y") && !answer.equals("n")){
                System.out.println("wrong command! please try again.");
                continue;
            }
            break;
        }
        if(answer.equals("y")){
            playersManager.login();
        }
        else{
            playersManager.singin();
        }
        System.out.println("Main Menu:");
        LogCenter logCenter = LogCenter.getInstance();
        Logger logger = logCenter.getLogger();
        while (true){
            answer = (scanner.nextLine()).trim();
            switch (answer){
                case "exit":
                    logger.info("exit_mainMenu");
                    mainCLI();
                    return;
                case "exit -a":
                    logger.info("exit_game");
                    return;
                case "collections":
                    logger.info("navigate_collections");
                    collections.CLI();
                    break;
                case  "store":
                    logger.info("navigate_store");
                    store.CLI();
                    break;
                case "hearthstone --help":
                    logger.info("get_help");
                    Scanner sc = new Scanner(new File("src"+File.separator+"Data"+File.separator+"General"+File.separator+"Main Menu Help"));
                    while (sc.hasNext()){
                        System.out.println(sc.nextLine());
                    }
                    sc.close();
                    break;
                case "delete-player":
                    logger.info("deleteUser_attempt");
                    playersManager.deleteCurrentPlayer();
                    mainCLI();
                    return;
                default:
                    logger.error("wrong_command");
                    System.out.println("command not found. \"hearthstone --help\" for more help.");
            }
        }
    }
}
