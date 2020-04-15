package Logic;

import Logic.Models.Cards.Card;
import Logic.Models.Hero;
import Logic.Models.Player;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Collections {
    private static Collections instanceCollection= null;
    private Collections(){ }

    static public Collections getInstance(){
        if(instanceCollection == null){
            instanceCollection = new Collections();
        }
        return instanceCollection;
    }

    public void CLI() throws IOException {
        System.out.println("Collections:");
        PlayersManager playersManager = PlayersManager.getInstance();
        Player player = playersManager.getCurrentPlayer();
        Scanner scanner = new Scanner(System.in);
        LogCenter logCenter = LogCenter.getInstance();
        Logger logger = logCenter.getLogger();
        String answer;
        while (true){
            boolean pas = false;
            answer = scanner.next();
            switch (answer){
                case "ls":
                    answer = scanner.next();
                    if(answer.equals("-a")){
                        answer = scanner.next();
                        if(answer.equals("-heroes")){
                            System.out.println("your heroes:");
                            for(Hero hero: player.getAllHeroes()){
                                System.out.println(hero.getName());
                            }
                            logger.info("list_player'sHeroes");
                            pas = true;
                        }
                        if(answer.equals("-cards")){
                            System.out.println("your cards:");
                            for(Card card: player.getAllCards()){
                                System.out.println(card);
                            }
                            logger.info("list_player'sCards");
                            pas = true;
                        }
                    }
                    else if (answer.equals("-m")){
                        answer = scanner.next();
                        if(answer.equals("-heroes")){
                            System.out.println("current hero:");
                            System.out.println(player.getCurrentHero().getName());
                            logger.info("show_currentHero");
                            pas = true;
                        }
                        if(answer.equals("-cards")){
                            ArrayList<Card> deckCards = player.getCurrentHero().getCards();
                            System.out.println("number of the deck's cards is "+deckCards.size()+".");
                            for(Card card: deckCards){
                                System.out.println(card);
                            }
                            logger.info("list_currentDeck");
                            pas = true;
                        }
                    }
                    else if(answer.equals("-n")){
                        answer = scanner.next();
                        if(answer.equals("-cards")){
                            System.out.println("chooseable cards:");
                            for(Card card: player.getAllCards()){
                                if(!player.getCurrentHero().getCards().contains(card)){
                                    if(player.getCurrentHero().isForHero(card)){
                                        System.out.println(card);
                                    }
                                }
                            }
                            logger.info("list_choosableCards");
                            pas = true;
                        }
                    }
                    if(!pas){
                        logger.error("wrong_command");
                        System.out.println("command not found. \"hearthstone --help\" for more help.");
                    }
                    break;
                case "select":
                    answer = scanner.nextLine().trim();
                    if(player.haveHero(answer)){
                        logger.info("select_hero("+answer+")");
                        player.setCurrentHero(answer);
                        System.out.println("selected.");
                    }
                    else {
                        logger.error("wrong_command");
                        System.out.println("hero not found.did you mean "+player.closestHero(answer)+"?");
                    }
                    break;
                case "remove":
                    answer = scanner.nextLine().trim();
                    if(player.getCurrentHero().haveCard(answer)){
                        player.getCurrentHero().removeCard(answer);
                        player.save();
                        System.out.println("removed.");
                        logger.info("remove_card("+answer+")");
                    }
                    else{
                        logger.error("wrong_command");
                        System.out.println("card not found in this deck.");
                    }
                    break;
                case "add":
                    answer = scanner.nextLine().trim();
                    Hero hero = player.getCurrentHero();
                    if(!hero.fullDeck() && player.haveCard(answer) && hero.cardNum(answer) < 2){
                        hero.addCard(player.findCard(answer));
                        player.save();
                        System.out.println("added.");
                        logger.info("add_card("+answer+")");
                    }
                    else {
                        logger.error("wrong_command");
                        System.out.println("can't add this card.");
                    }
                    break;
                case "hearthstone":
                    if(scanner.next().equals("--help")){
                        logger.info("get_help");
                        Scanner sc = new Scanner(new File("src"+File.separator+"Data"+File.separator+"General"+File.separator+"Collections Help"));
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
