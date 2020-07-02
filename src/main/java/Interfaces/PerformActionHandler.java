package Interfaces;

import Exceptions.InvalidChoiceException;
import Models.InfoPack;

public interface PerformActionHandler extends ActionHandler{
    public void runAction(InfoPack[] infoPacks) throws InvalidChoiceException;
}
