package Graphics;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class HeroPlaceController {
    @FXML
    private Label hp;
    @FXML
    private ImageView shield;

    @FXML
    private ImageView heroImage;

    public void setHeroImage(Image image){
        heroImage.setImage(image);
    }

    public void setHp(int hp){
        this.hp.setText(Integer.toString(hp));
    }

    public void setShield(boolean shield){
        this.shield.setVisible(shield);
    }
}
