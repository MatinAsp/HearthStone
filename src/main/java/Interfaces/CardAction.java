package Interfaces;

import Exceptions.InvalidChoiceException;
import Models.Cards.Card;
import Models.InfoPack;

public interface CardAction extends ActionHandler{
    public void runAction(Card card);
}
