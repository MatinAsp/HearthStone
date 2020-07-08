package Graphics;

import Logic.PlayersManager;
import Models.Deck;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class QuestStatusController {
    @FXML
    private Label percent;
    @FXML
    private ImageView questPic;

    public void setPercent(String percent){
        this.percent.setText(percent);
    }

    public void setQuestPic(Image questPic) {
        this.questPic.setImage(questPic);
    }
}
