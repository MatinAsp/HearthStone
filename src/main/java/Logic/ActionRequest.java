package Logic;

import Exceptions.EmptyDeckException;
import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Exceptions.SelectionNeededException;
import Interfaces.ActionHandler;
import Interfaces.PerformActionHandler;
import Interfaces.PlayActionHandler;
import Models.Cards.Card;
import Models.Cards.Minion;
import Models.InfoPack;

import java.util.ArrayList;

public enum ActionRequest {
    END_TURN{
        @Override
        public void execute() throws EmptyDeckException, GameOverException {
            game.changeTurn();
            super.execute();
        }
    },
    DRAW_CARD{
        @Override
        public void execute() throws EmptyDeckException, GameOverException {
            game.drawCard();
            super.execute();
        }
    },
    PERFORM_ACTION{
        @Override
        public void execute(InfoPack[] parameters) throws SelectionNeededException, InvalidChoiceException, GameOverException {
            if(!parameters[0].isOnGround()){
                game.checkForMana((Card) parameters[0].getCharacter(), parameters[0].getSide());
            }
            executeBeforeActions(parameters);
            game.performAction(parameters);
            if(!parameters[0].isOnGround()){
                game.playCard((Card) parameters[0].getCharacter(), parameters[0].getSide());
            }
            super.execute(parameters);
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
            super.execute(minion, side);
        }
    },
    PLAY_CARD{
        @Override
        public void execute(Card card, int side) throws GameOverException, InvalidChoiceException {
            game.playCard(card, side);
            super.execute(card, side);
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

    public void execute() throws EmptyDeckException, GameOverException {
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
    private ArrayList<ActionHandler> actions = new ArrayList<>(), beforeActions = new ArrayList<>();

    public static void setCurrentGame(Game game){
        ActionRequest.game = game;
        for(ActionRequest actionRequest: values()){
            actionRequest.actions.clear();
        }
    }

    public void addAction(ActionHandler actionHandler){
        actions.add(actionHandler);
    }

    public void addBeforeAction(ActionHandler actionHandler) {
        beforeActions.add(actionHandler);
    }
}
