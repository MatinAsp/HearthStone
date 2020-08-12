package Logic.ActionsType;

import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Exceptions.SelectionNeededException;
import Interfaces.ActionHandler;
import Interfaces.PerformActionHandler;
import Logic.ActionRequest;
import Models.Cards.HeroPower;
import Models.Cards.Weapon;
import Models.InfoPack;
import Models.Passive;

public class PerformAction extends Action{

    public PerformAction(ActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    public void execute(InfoPack[] parameters) throws SelectionNeededException, InvalidChoiceException, GameOverException {
        try {
            executeBeforeActions(parameters);
            getActionRequest().getGame().performAction(parameters);
            record(parameters);
            super.execute(parameters);
            getActionRequest().getGame().checkAll();
        }catch (GameOverException e){
            record(parameters);
            getActionRequest().getGame().engGame();
            throw e;
        }
    }

    private void record(InfoPack[] parameters){
        if(!parameters[0].isOnGround()) {
            if (parameters[0].getCharacter() instanceof Weapon) {
                getActionRequest().setSummoned(true);
            }
            if (!(parameters[0].getCharacter() instanceof Passive)){
                getActionRequest().setPlayed(parameters[0]);
            }
        }
        else {
            if(!(parameters[0].getCharacter() instanceof HeroPower)){
                getActionRequest().getAttackList().add(parameters[0]);
                getActionRequest().getAttackList().add(parameters[1]);
            }
            else getActionRequest().setUseHeroPower(true);
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
}
