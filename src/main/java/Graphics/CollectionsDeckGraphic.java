package Graphics;

import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CollectionsDeckGraphic {
    @FXML
    private Label deckName;

    @FXML
    private ImageView heroImage;

    public void setImage(Image image){
        heroImage.setImage(image);
    }

    public void setDeckName(String name){
        deckName.setText(name);
    }

}
