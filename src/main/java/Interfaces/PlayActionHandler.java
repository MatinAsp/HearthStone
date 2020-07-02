package Interfaces;

import Models.Cards.Card;

public interface PlayActionHandler extends ActionHandler{
    void runAction(Card card, int side) throws Exception;
}
