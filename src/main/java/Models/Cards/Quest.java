package Models.Cards;

import javax.persistence.Entity;

@Entity
public class Quest extends Card {
    public Quest(){}

    private Quest(Quest quest) {
        super(quest);
    }

    @Override
    public String toString() {
        return super.toString()+" ";
    }

    @Override
    public Quest newOne() {
        return new Quest(this);
    }
}
