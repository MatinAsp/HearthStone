package Logic;

import Logic.Models.Cards.Card;
import Logic.Models.Player;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Store {
    private ArrayList<Card> allCards;
    private static Store instanceStore = null;
    private Store() throws IOException {
        DataManager dataManager = DataManager.getInstance();
        allCards = new ArrayList<>();
        allCards.addAll(dataManager.getAllCards());
    }

    static public Store getInstance() throws IOException {
        if(instanceStore == null){
            instanceStore = new Store();
        }
        return instanceStore;
    }

    public void CLI() throws IOException {
        System.out.println("Store:");
        PlayersManager playersManager = PlayersManager.getInstance();
        Player player = playersManager.getCurrentPlayer();
        ArrayList<Card> forSellCards = new ArrayList<>(), forBuyCards = new ArrayList<>();
        LogCenter logCenter = LogCenter.getInstance();
        Logger logger = logCenter.getLogger();
        for(Card card: allCards){
            if(player.usingCard(card.getName())){
                continue;
            }
            if(player.haveCard(card.getName())){
                forSellCards.add(card);
            }
            else {
                if(card.getHeroClass().equals("Neutral") || player.haveHero(card.getHeroClass())){
                    forBuyCards.add(card);
                }
            }
        }
        Scanner scanner = new Scanner(System.in);
        String answer;
        while (true){
            boolean pas = false;
            answer = scanner.next();
            switch (answer){
                case "wallet":
                    logger.info("show_wallet");
                    System.out.println(player.getWallet());
                    break;
                case "ls":
                    answer = scanner.next();
                    pas = false;
                    if(answer.equals("-s")){
                        System.out.println("sellable cards:");
                        for(Card card: forSellCards){
                            System.out.println(card);
                        }
                        logger.info("list_forSellCards");
                        pas = true;
                    }
                    if(answer.equals("-b")){
                        System.out.println("purchaseable cards:");
                        for(Card card: forBuyCards){
                            System.out.println(card);
                        }
                        logger.info("list_forBuyCards");
                        pas = true;
                    }
                    if(!pas){
                        logger.error("wrong_command");
                        System.out.println("command not found. \"hearthstone --help\" for more help.");
                    }
                    break;
                case "buy":
                    answer = scanner.nextLine().trim();
                    pas =false;
                    for(Card card: forBuyCards){
                        if(card.getName().equals(answer)){
                            if (player.getWallet() >= card.getPrice()){
                                System.out.println("purchased.");
                                logger.info("buy_card("+card.getName()+")");
                                player.setWallet(player.getWallet()-card.getPrice());
                                player.addToCards(card);
                                forBuyCards.remove(card);
                                forSellCards.add(card);
                                pas = true;
                            }
                            break;
                        }
                    }
                    if(!pas){
                        logger.error("wrong_command");
                        System.out.println("you can't buy this card.");
                    }
                    break;
                case "sell":
                    answer = scanner.nextLine().trim();
                    pas =false;
                    for(Card card: forSellCards){
                        if(card.getName().equals(answer)){
                            System.out.println("sold.");
                            logger.info("sell_card("+card.getName()+")");
                            player.setWallet(player.getWallet()+card.getPrice());
                            player.removeCard(card);
                            forSellCards.remove(card);
                            forBuyCards.add(card);
                            pas = true;
                            break;
                        }
                    }
                    if(!pas){
                        logger.error("wrong_command");
                        System.out.println("you can't sell this card.");
                    }
                case "hearthstone":
                    if(scanner.next().equals("--help")){
                        logger.info("get_help");
                        Scanner sc = new Scanner(new File("src"+File.separator+"Data"+File.separator+"General"+File.separator+"Store Help"));
                        while (sc.hasNext()){
                            System.out.println(sc.nextLine());
                        }
                        sc.close();
                    }
                    else{
                        logger.error("wrong_command");
                        System.out.println("command not found. \"hearthstone --help\" for more help.");
                    }
                    break;
                case "exit\n":
                    logger.info("navigate_mainMenu");
                    System.out.println("Main Menu:");
                    return;
                case "exit":
                    if(scanner.next().equals("-a")){
                        logger.info("exit_game");
                        System.exit(0);
                    }
                    else{
                        logger.error("wrong_command");
                        System.out.println("command not found. \"hearthstone --help\" for more help.");
                    }
                    break;
                default:
                    logger.error("wrong_command");
                    System.out.println("command not found. \"hearthstone --help\" for more help.");
            }
        }
    }
}
