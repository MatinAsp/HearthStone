import Logic.Competitor;
import Logic.Game;
import Models.Cards.Minion;

public class ReflectedGame extends Game {

    public ReflectedGame(Competitor competitor1, Competitor competitor2, boolean isWithBot) {
        super(competitor1, competitor2, isWithBot);
    }

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
