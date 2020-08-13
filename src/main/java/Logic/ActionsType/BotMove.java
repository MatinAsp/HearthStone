package Logic.ActionsType;

import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Exceptions.SelectionNeededException;
import Logic.ActionRequest;
import Models.InfoPack;

import java.util.ArrayList;
import java.util.Collections;

public class BotMove extends Action{

    public BotMove(ActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    public boolean execute(ArrayList<InfoPack> allInfoPack) throws GameOverException {
        Collections.shuffle(allInfoPack);
        boolean check = false;
        for(InfoPack infoPack: allInfoPack){
            InfoPack[] infoPacks = {infoPack};
            try {
                getActionRequest().getPerformAction().execute(infoPacks);
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
                    getActionRequest().getPerformAction().execute(infoPacks);
                    check = true;
                    break;
                } catch (SelectionNeededException | InvalidChoiceException e) {
                    continue;
                }
            }
        }
        return check;
    }
}
