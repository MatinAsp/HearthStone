import Exceptions.EmptyDeckException;
import Logic.Competitor;

public class ReflectedCompetitor extends Competitor {
    @Override
    public void drawCard() throws EmptyDeckException {
        super.drawCard();
    }
}
