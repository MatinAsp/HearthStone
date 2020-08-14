import Logic.Game;
import Models.Cards.Minion;

public class ReflectedGame extends Game {

    @Override
    public void refreshMana() {
        super.refreshMana();
    }

    @Override
    public void summon(Minion minion, int side, int summonPlace) {
        super.summon(minion, side, summonPlace);
    }

    @Override
    public void initialize() {
        super.initialize();
    }
}
