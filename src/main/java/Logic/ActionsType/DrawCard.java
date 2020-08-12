package Logic.ActionsType;

import Exceptions.EmptyDeckException;
import Exceptions.GameOverException;
import Logic.ActionRequest;
import Logic.Game;

public class DrawCard extends Action{

    public DrawCard(ActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    public void execute() throws GameOverException {
        boolean check = false;
        try {
            getActionRequest().getGame().drawCard();
        } catch (EmptyDeckException e) {
            check = true;
        }
        if(!check){
            getActionRequest().setNumberOfDraws(getActionRequest().getNumberOfDraws() + 1);
            super.execute();
        }
    }
}
