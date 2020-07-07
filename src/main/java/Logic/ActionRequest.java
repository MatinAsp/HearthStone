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
import java.util.Random;

public enum ActionRequest {
    END_TURN{
        @Override
        public void execute() throws GameOverException {
            try{
                game.changeTurn();
                super.execute();
                game.checkAll();
                game.chargeCards();
            }catch (GameOverException e){
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
            }
            super.execute();
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
        public void execute(Card minion, int side) throws GameOverException, InvalidChoiceException {
            game.summon((Minion) minion, side);
            summoned = true;
            super.execute(minion, side);
        }
    };

    public ArrayList<ActionHandler> getBeforeActions() {
        return beforeActions;
    }

    public void execute(Card card, int side) throws GameOverException, InvalidChoiceException {
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
    private static int numberOfDraws;
    private static boolean summoned;
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
        boolean answer = summoned;
        summoned = false;
        return answer;
    }

    public static InfoPack readPlayed(){
        InfoPack answer = played;
        played = null;
        return answer;
    }

    public static int readDrawNumber(){
        int answer = numberOfDraws;
        numberOfDraws = 0;
        return answer;
    }

    public static ArrayList<InfoPack> readAttackingList(){
        ArrayList<InfoPack> infoPacks = new ArrayList<>();
        if(attackList.size() > 0){
            infoPacks.add(attackList.get(0));
            infoPacks.add(attackList.get(1));
        }
        attackList.clear();
        return infoPacks;
    }

    public void addAction(ActionHandler actionHandler){
        actions.add(actionHandler);
    }

    public void addBeforeAction(ActionHandler actionHandler) {
        beforeActions.add(actionHandler);
    }
}
