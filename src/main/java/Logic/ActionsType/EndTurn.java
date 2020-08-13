package Logic.ActionsType;

import Exceptions.GameOverException;
import Logic.ActionRequest;
import Logic.Game;

public class EndTurn extends Action{

    public EndTurn(ActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    public void execute() throws GameOverException {
        try{
            getActionRequest().getGame().changeTurn();
            super.execute();
            getActionRequest().getGame().checkAll();
            getActionRequest().getGame().chargeCards();
            getActionRequest().setTurnEnded(true);
        }catch (GameOverException e){
            getActionRequest().setTurnEnded(true);
            throw e;
        }
    }
}
