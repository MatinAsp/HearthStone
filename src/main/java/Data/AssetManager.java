package Data;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class AssetManager {
    static private AssetManager assetManager = null;
    private String assetsAddress = GameConstants.getInstance().getString("assetsAddress");
    private AssetManager() throws IOException {}

    public static AssetManager getInstance() throws IOException {
        if(assetManager == null){
            assetManager = new AssetManager();
        }
        return assetManager;
    }

    public Image getImage(String name) throws IOException {
        Image image = new Image(Paths.get(
                assetsAddress+File.separator+name+".png"
        ).toUri().toString());
        return image;
    }

    public Image getCard(String name) throws IOException {
        Image card = new Image(Paths.get(
                assetsAddress+File.separator+"Cards"+File.separator+name+".png"
        ).toUri().toString());
        return card;
    }
    public Image getHeroImage(String name){
        Image hero = new Image(Paths.get(
                assetsAddress+File.separator+"Heroes"+File.separator+name+".png"
        ).toUri().toString());
        return hero;
    }
    public Image getCardBorder(String name) throws IOException {
        Image border = new Image(Paths.get(
                assetsAddress+File.separator+"CardBorders"+File.separator+name+".png"
        ).toUri().toString());
        return border;
    }
    public Image getPassive(String name){
        Image passive = new Image(Paths.get(
                assetsAddress+File.separator+"Passives"+File.separator+name+".png"
        ).toUri().toString());
        return passive;
    }

    public Image getBattleGround(String name) {
        Image arena = new Image(Paths.get(
                assetsAddress+File.separator+"BattleGrounds"+File.separator+name+".png"
        ).toUri().toString());
        return arena;
    }

    public Image getCardBack(String name) {
        Image cardBack = new Image(Paths.get(
                assetsAddress+File.separator+"CardsBack"+File.separator+name+".png"
        ).toUri().toString());
        return cardBack;
    }
}
