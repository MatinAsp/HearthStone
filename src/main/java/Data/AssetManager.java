package Data;


import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class AssetManager {
    static private AssetManager assetManager = null;
    private HashMap<String, Image> imageMap = new HashMap<>();
    private String assetsAddress = GameConstants.getInstance().getString("assetsAddress");
    private AssetManager() throws IOException {}

    public static AssetManager getInstance() throws IOException {
        if(assetManager == null){
            assetManager = new AssetManager();
        }
        return assetManager;
    }

    public Image getImage(String name){
        if(!imageMap.containsKey(name)){
            imageMap.put(name, searchImage(name, assetsAddress));
        }
        return imageMap.get(name);
    }

    public Image searchImage(String name, String address){
        File dir = new File(address);
        for(File file: dir.listFiles()){
            if(file.isDirectory()){
                Image image = searchImage(name, file.getAbsolutePath());
                if(image != null) return image;
            }
            else{
                if(file.getName().equalsIgnoreCase(name+".png")){
                    return new Image(Paths.get(file.getAbsolutePath()).toUri().toString());
                }
            }
        }
        return null;
    }
}
