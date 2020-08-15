import Exceptions.EmptyDeckException;
import Logic.Competitor;

public class ReflectedCompetitor extends Competitor {

    public ReflectedCompetitor(String username) {
        super(username);
    }

    @Override
    public void drawCard() throws EmptyDeckException {
        super.drawCard();
    }
}
