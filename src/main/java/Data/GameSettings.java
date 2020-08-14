package Data;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameSettings {
    static private GameSettings gameSettings = null;
    private double volume;
    private String cardBack, battleGroundArena;

    private GameSettings() {
        GameConstants gameConstants = GameConstants.getInstance();
        volume = gameConstants.getDouble("volume");
        cardBack = gameConstants.getString("cardBack");
        battleGroundArena = gameConstants.getString("battleGroundArena");
    }

    static synchronized public GameSettings getInstance() {
        if(gameSettings == null){
            gameSettings = new GameSettings();
        }
        return gameSettings;
    }


    public synchronized double getVolume() {
        return volume;
    }

    public synchronized void setVolume(double volume) {
        this.volume = volume;
    }

    public synchronized String getCardBack() {
        return cardBack;
    }

    public synchronized void setCardBack(String cardBack) {
        this.cardBack = cardBack;
    }

    public synchronized String getBattleGroundArena() {
        return battleGroundArena;
    }

    public synchronized void setBattleGroundArena(String battleGroundArena) {
        this.battleGroundArena = battleGroundArena;
    }

    public synchronized void applySettings() throws IOException {
        MediaManager.getInstance().setVolume(volume);
        GameConstants gameConstants = GameConstants.getInstance();
        gameConstants.setProperty("volume", Double.toString(volume));
        gameConstants.setProperty("cardBack", cardBack);
        gameConstants.setProperty("battleGroundArena", battleGroundArena);
        gameConstants.save();
    }

    public synchronized List<String> getAllArenas() throws IOException {
        return GameConstants.getInstance().getStingList("arenasList");
    }

    public synchronized List<String> getAllCardBack() throws IOException {
        return GameConstants.getInstance().getStingList("cardsBackList");
    }
}
