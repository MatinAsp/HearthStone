package Models.Cards;

import Interfaces.Cloneable;

public class Quest extends Card implements Cloneable {
    public Quest(){}

    private Quest(Quest quest) {
        super(quest);
    }

    @Override
    public String toString() {
        return super.toString()+"\n";
    }

    @Override
    public Quest newOne() {
        return new Quest(this);
    }
}
