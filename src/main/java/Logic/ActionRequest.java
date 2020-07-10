package Logic;

import Exceptions.EmptyDeckException;
import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Exceptions.SelectionNeededException;
import Graphics.BattleGroundController;
import Interfaces.ActionHandler;
import Interfaces.PerformActionHandler;
import Interfaces.PlayActionHandler;
import Log.LogCenter;
import Models.Cards.Card;
import Models.Cards.HeroPower;
import Models.Cards.Minion;
import Models.Cards.Weapon;
import Models.InfoPack;
import Models.Passive;

import java.util.ArrayList;
import java.util.Collections;

public enum ActionRequest {
    END_TURN{
        @Override
        public void execute() throws GameOverException {
            try{
                game.changeTurn();
                super.execute();
                game.checkAll();
                game.chargeCards();
                turnEnded = true;
            }catch (GameOverException e){
                turnEnded = true;
                game.engGame();
                throw e;
            }
        }
    },
    DRAW_CARD{
        @Override
        public void execute() throws GameOverException {
            boolean check = false;
            try {
                game.drawCard();
            } catch (EmptyDeckException e) {
                check = true;
            }
            if(!check){
                numberOfDraws++;
                super.execute();
            }
        }
    },
    PERFORM_ACTION{
        @Override
        public void execute(InfoPack[] parameters) throws SelectionNeededException, InvalidChoiceException, GameOverException {
            try {
                executeBeforeActions(parameters);
                game.performAction(parameters);
                record(parameters);
                super.execute(parameters);
                game.checkAll();
            }catch (GameOverException e){
                record(parameters);
                game.engGame();
                throw e;
            }
        }

        private void record(InfoPack[] parameters){
            if(!parameters[0].isOnGround()) {
                if (parameters[0].getCharacter() instanceof Weapon) {
                    summoned = true;
                }
                if (!(parameters[0].getCharacter() instanceof Passive)){
                    played = parameters[0];
                }
            }
            else {
                if(!(parameters[0].getCharacter() instanceof HeroPower)){
                    attackList.add(parameters[0]);
                    attackList.add(parameters[1]);
                }
                else useHeroPower = true;
            }
        }

        private void executeBeforeActions(InfoPack[] infoPacks) throws InvalidChoiceException {
            for(ActionHandler actionHandler: getBeforeActions()){
                if(actionHandler instanceof PerformActionHandler){
                    ((PerformActionHandler) actionHandler).runAction(infoPacks);
                }
                try {
                    actionHandler.runAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    },
    SUMMON_MINION{
        @Override
        public void execute(Card minion, int side, int summonPlace) throws GameOverException, InvalidChoiceException {
            game.summon((Minion) minion, side, summonPlace);
            summoned = true;
            super.execute(minion, side, summonPlace);
        }
    },
    BOT_MOVE{
        @Override
        public boolean execute(ArrayList<InfoPack> allInfoPack) throws GameOverException {
            Collections.shuffle(allInfoPack);
            boolean check = false;
            try {

                for(InfoPack infoPack: allInfoPack){
                    InfoPack[] infoPacks = {infoPack};
                    try {
                        ActionRequest.PERFORM_ACTION.execute(infoPacks);
                        check = true;
                        break;
                    } catch (SelectionNeededException | InvalidChoiceException e) {
                        continue;
                    }
                }
                for(int i = 0; i < allInfoPack.size() && !check; i++ ){
                    for(int j = 0; j < allInfoPack.size(); j++){
                        if(i == j) continue;
                        InfoPack[] infoPacks = {allInfoPack.get(i), allInfoPack.get(j)};
                        try {
                            ActionRequest.PERFORM_ACTION.execute(infoPacks);
                            check = true;
                            break;
                        } catch (SelectionNeededException | InvalidChoiceException e) {
                            continue;
                        }
                    }
                }
            }catch (GameOverException e){
                game.engGame();
                throw e;
            }
            return check;
        }
    };

    public boolean execute(ArrayList<InfoPack> allInfoPacks) throws GameOverException {return false;}
    public static void selectCard(ArrayList<Card> cardsSelected) throws GameOverException {
        try {
            game.selectCard(cardsSelected);
        } catch (GameOverException e) {
            game.engGame();
            throw e;
        }
    }

    public static void reduceDrawNumber(int cnt) {
        numberOfDraws -= cnt;
    }

    public ArrayList<ActionHandler> getBeforeActions() {
        return beforeActions;
    }

    public void execute(Card card, int side, int summonPlace) throws GameOverException, InvalidChoiceException {
        for (ActionHandler actionHandler: actions){
            try {
                if(actionHandler instanceof PlayActionHandler){
                    ((PlayActionHandler) actionHandler).runAction(card, side);
                }
                actionHandler.runAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void execute() throws GameOverException {
        for (ActionHandler actionHandler: actions){
            try {
                actionHandler.runAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void execute(InfoPack[] parameters) throws SelectionNeededException, InvalidChoiceException, GameOverException {
        for (ActionHandler actionHandler: actions){
            try {
                if (actionHandler instanceof PerformActionHandler){
                    ((PerformActionHandler) actionHandler).runAction(parameters);
                }
                actionHandler.runAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Game game;
    private static boolean useHeroPower;
    private static int numberOfDraws;
    private static boolean summoned;
    private static boolean turnEnded;
    private static InfoPack played;
    private static ArrayList<InfoPack> attackList;
    private ArrayList<ActionHandler> actions = new ArrayList<>(), beforeActions = new ArrayList<>();

    public static void setCurrentGame(Game game){
        ActionRequest.game = game;
        numberOfDraws = 0;
        summoned = false;
        played = null;
        attackList = new ArrayList<>();
        for(ActionRequest actionRequest: values()){
            actionRequest.actions.clear();
        }
        game.initialize();
    }

    public static boolean readSummoned(){
        return summoned;
    }

    public static boolean readTurnEnded(){
        return turnEnded;
    }

    public static boolean readUseHeroPower(){
        return useHeroPower;
    }

    public static InfoPack readPlayed(){
        return played;
    }

    public static int readDrawNumber(){
        return numberOfDraws;
    }

    public static ArrayList<InfoPack> readAttackingList(){
        ArrayList<InfoPack> infoPacks = new ArrayList<>();
        if(attackList.size() > 0){
            infoPacks.add(attackList.get(0));
            infoPacks.add(attackList.get(1));
        }
        return infoPacks;
    }

    public static void clearRecords(){
        attackList.clear();
        numberOfDraws = 0;
        played = null;
        summoned = false;
        turnEnded =false;
        useHeroPower = false;
    }

    public void addAction(ActionHandler actionHandler){
        actions.add(actionHandler);
    }

    public void addBeforeAction(ActionHandler actionHandler) {
        beforeActions.add(actionHandler);
    }
}
