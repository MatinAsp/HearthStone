package Logic.ActionsType;

import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Exceptions.SelectionNeededException;
import Interfaces.ActionHandler;
import Interfaces.PerformActionHandler;
import Interfaces.PlayActionHandler;
import Logic.ActionRequest;
import Logic.Game;
import Models.Cards.Card;
import Models.InfoPack;

import java.util.ArrayList;

public class Action {
    private ActionRequest actionRequest;
    private ArrayList<ActionHandler> actions = new ArrayList<>(), beforeActions = new ArrayList<>();


    public Action(ActionRequest actionRequest){
        this.actionRequest = actionRequest;
    }

    public ActionRequest getActionRequest(){
        return actionRequest;
    }

    public ArrayList<ActionHandler> getActions(){
        return actions;
    }

    public void addAction(ActionHandler actionHandler){
        actions.add(actionHandler);
    }

    public void addBeforeAction(ActionHandler actionHandler) {
        beforeActions.add(actionHandler);
    }

    public ArrayList<ActionHandler> getBeforeActions() {
        return beforeActions;
    }

    public boolean execute(ArrayList<InfoPack> allInfoPacks) throws GameOverException {return false;}

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
}
