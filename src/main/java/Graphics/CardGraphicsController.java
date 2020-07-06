package Graphics;

import Models.Cards.Card;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Ellipse;

public class CardGraphicsController {
    @FXML
    private Label mana;
    @FXML
    private Label attack;
    @FXML
    private Label cardName;
    @FXML
    private Label hp;
    @FXML
    private Label description;
    @FXML
    private Label price;
    @FXML
    private ImageView coin;
    @FXML
    private ImageView cardPic;
    @FXML
    private ImageView border;
    @FXML
    private ImageView lock;
    @FXML
    private Label durability;
    @FXML
    private Ellipse view;
    @FXML
    private ImageView cardBack;


    public void setMana(int mana) {
        this.mana.setText(Integer.toString(mana));
    }

    public void setAttack(int attack) {
        this.attack.setText(Integer.toString(attack));
    }

    public void setHp(int hp) {
        this.hp.setText(Integer.toString(hp));
    }

    public void setCardName(String cardName) {
        this.cardName.setText(cardName);
    }

    public void setDurability(int durability) {
        this.durability.setText(Integer.toString(durability));
    }

    public void setDescription(String description) {
        this.description.setText(description);
    }

    public void setPrice(int price) {
        this.price.setText(Integer.toString(price));
    }

    public void priceVisible(boolean value) {
        coin.setVisible(value);
        price.setVisible(value);
    }

    public void lockVisible(boolean value) {
        lock.setVisible(value);
    }

    public void setCardPic(Image image) {
        cardPic.setImage(image);
        if (view != null){
            final Ellipse clip = new Ellipse(
                    cardPic.getFitWidth()/2, cardPic.getFitHeight()/2,
                    view.getRadiusX()-3, view.getRadiusY()-3
            );
            clip.setStroke(view.getStroke());
            clip.setStrokeWidth(view.getStrokeWidth());
            this.cardPic.setClip(clip);
        }
    }

    public void setBorder(Image border) {
        this.border.setImage(border);
    }

    public void setCardBack(Image cardBack){
        this.cardBack.setImage(cardBack);
    }
}
