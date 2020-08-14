package Logic;

import Exceptions.GameOverException;
import Exceptions.InvalidChoiceException;
import Logic.ActionsType.*;
import Models.Cards.Card;
import Models.InfoPack;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;

import java.util.ArrayList;

@JsonIgnoreProperties(value = {"game", "botMove", "drawCard", "endTurn", "performAction", "summonMinion"})
public class ActionRequest {
    private boolean useHeroPower;
    private int numberOfDraws, drawTurn;
    private boolean summoned;
    private boolean turnEnded;
    private InfoPack played;
    private ArrayList<InfoPack> attackList;
    private Game game;
    private BotMove botMove;
    private DrawCard drawCard;
    private EndTurn endTurn;
    private PerformAction performAction;
    private SummonMinion summonMinion;

    public ActionRequest() {}

    public ActionRequest(Game game){
        this.game = game;
        botMove = new BotMove(this);
        drawCard = new DrawCard(this);
        endTurn = new EndTurn(this);
        performAction = new PerformAction(this);
        summonMinion = new SummonMinion(this);
        numberOfDraws = 0;
        drawTurn = 0;
        summoned = false;
        played = null;
        attackList = new ArrayList<>();
        useHeroPower = false;
        turnEnded = false;
    }

    public void selectCard(ArrayList<Card> cardsSelected, int competitorIndex) throws GameOverException, InvalidChoiceException {
        try {
            game.selectCard(cardsSelected, competitorIndex);
        } catch (GameOverException e) {
            throw e;
        }
    }

    public void reduceDrawNumber(int cnt) {
        numberOfDraws -= cnt;
    }

    public ArrayList<InfoPack> readAttackingList(){
        ArrayList<InfoPack> infoPacks = new ArrayList<>();
        if(attackList.size() > 0){
            infoPacks.add(attackList.get(0));
            infoPacks.add(attackList.get(1));
        }
        return infoPacks;
    }

    public void clearRecords(){
        attackList.clear();
        numberOfDraws = 0;
        played = null;
        summoned = false;
        turnEnded = false;
        useHeroPower = false;
        drawTurn = 0;
    }

    public boolean isUseHeroPower() {
        return useHeroPower;
    }

    public void setUseHeroPower(boolean useHeroPower) {
        this.useHeroPower = useHeroPower;
    }

    public int getNumberOfDraws() {
        return numberOfDraws;
    }

    public void setNumberOfDraws(int numberOfDraws) {
        this.numberOfDraws = numberOfDraws;
    }

    public boolean isSummoned() {
        return summoned;
    }

    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    public boolean isTurnEnded() {
        return turnEnded;
    }

    public void setTurnEnded(boolean turnEnded) {
        this.turnEnded = turnEnded;
    }

    public InfoPack getPlayed() {
        return played;
    }

    public void setPlayed(InfoPack played) {
        this.played = played;
    }

    public ArrayList<InfoPack> getAttackList() {
        return attackList;
    }

    public Game getGame() {
        return game;
    }

    public BotMove getBotMove() {
        return botMove;
    }

    public DrawCard getDrawCard() {
        return drawCard;
    }

    public EndTurn getEndTurn() {
        return endTurn;
    }

    public PerformAction getPerformAction() {
        return performAction;
    }

    public SummonMinion getSummonMinion() {
        return summonMinion;
    }

    public void setAttackList(ArrayList<InfoPack> attackList) {
        this.attackList = attackList;
    }

    public int getDrawTurn() {
        return drawTurn;
    }

    public void setDrawTurn(int drawTurn) {
        this.drawTurn = drawTurn;
    }
}
