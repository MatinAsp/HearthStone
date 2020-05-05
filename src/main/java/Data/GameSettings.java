package Data;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameSettings {
    static private GameSettings gameSettings = null;
    private double volume;
    private String cardBack, battleGroundArena;

    private GameSettings() throws IOException {
        GameConstants gameConstants = GameConstants.getInstance();
        volume = gameConstants.getDouble("volume");
        cardBack = gameConstants.getString("cardBack");
        battleGroundArena = gameConstants.getString("battleGroundArena");
    }

    static public GameSettings getInstance() throws IOException {
        if(gameSettings == null){
            gameSettings = new GameSettings();
        }
        return gameSettings;
    }


    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public String getCardBack() {
        return cardBack;
    }

    public void setCardBack(String cardBack) {
        this.cardBack = cardBack;
    }

    public String getBattleGroundArena() {
        return battleGroundArena;
    }

    public void setBattleGroundArena(String battleGroundArena) {
        this.battleGroundArena = battleGroundArena;
    }

    public void applySettings() throws IOException {
        MediaManager.getInstance().setVolume(volume);
        GameConstants gameConstants = GameConstants.getInstance();
        gameConstants.setProperty("volume", Double.toString(volume));
        gameConstants.setProperty("cardBack", cardBack);
        gameConstants.setProperty("battleGroundArena", battleGroundArena);
        gameConstants.save();
    }

    public List<String> getAllArenas() throws IOException {
        return GameConstants.getInstance().getStingList("arenasList");
    }

    public List<String> getAllCardBack() throws IOException {
        return GameConstants.getInstance().getStingList("cardsBackList");
    }
}
