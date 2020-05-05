package Graphics;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Ellipse;

public class HeroPowerController {
    @FXML
    private Label mana;

    @FXML
    private ImageView heroPowerImage;

    public void setHeroPowerImage(Image image) {
        heroPowerImage.setImage(image);
    }

    public void setMana(int mana){
        this.mana.setText(Integer.toString(mana));
    }

}
