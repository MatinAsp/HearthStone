package Models.Cards;


public class Quest extends Card {
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
