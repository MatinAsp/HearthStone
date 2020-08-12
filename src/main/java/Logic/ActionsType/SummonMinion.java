package Logic.ActionsType;

import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Logic.ActionRequest;
import Models.Cards.Card;
import Models.Cards.Minion;

public class SummonMinion extends Action{

    public SummonMinion(ActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    public void execute(Card minion, int side, int summonPlace) throws GameOverException, InvalidChoiceException {
        getActionRequest().getGame().summon((Minion) minion, side, summonPlace);
        getActionRequest().setSummoned(true);
        super.execute(minion, side, summonPlace);
    }
}
