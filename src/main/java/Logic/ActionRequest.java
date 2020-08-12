package Logic;

import Exceptions.GameOverException;
import Logic.ActionsType.*;
import Models.Cards.Card;
import Models.InfoPack;

import java.util.ArrayList;

public class ActionRequest {
    private boolean useHeroPower;
    private int numberOfDraws;
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

    public ActionRequest(Game game){
        this.game = game;
        botMove = new BotMove(this);
        drawCard = new DrawCard(this);
        endTurn = new EndTurn(this);
        performAction = new PerformAction(this);
        summonMinion = new SummonMinion(this);
        numberOfDraws = 0;
        summoned = false;
        played = null;
        attackList = new ArrayList<>();
        game.initialize();
    }

    public void selectCard(ArrayList<Card> cardsSelected) throws GameOverException {
        try {
            game.selectCard(cardsSelected);
        } catch (GameOverException e) {
            game.engGame();
            throw e;
        }
    }

    public void reduceDrawNumber(int cnt) {
        numberOfDraws -= cnt;
    }

    public boolean readSummoned(){
        return summoned;
    }

    public boolean readTurnEnded(){
        return turnEnded;
    }

    public boolean readUseHeroPower(){
        return useHeroPower;
    }

    public InfoPack readPlayed(){
        return played;
    }

    public int readDrawNumber(){
        return numberOfDraws;
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
        turnEnded =false;
        useHeroPower = false;
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
}
