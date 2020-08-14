package Graphics;

import Models.Deck;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class RankGraphicsController {
    @FXML
    private Label username;
    @FXML
    private Label rank;
    @FXML
    private Label cup;

    public void loadRank(String username, int rank, int cup) {
        this.username.setText(username);
        this.rank.setText(String.valueOf(rank)+".");
        this.cup.setText(String.valueOf(cup));
    }

}
