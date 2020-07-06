package Models;

import javafx.scene.Parent;

public class InfoPack {
    private Character character;
    private int side;
    private boolean isOnGround;
    private Parent parent;

    public int getSide() {
        return side;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public InfoPack(Character character, int side, boolean isOnGround, Parent parent){
        this.character = character;
        this.side = side;
        this.isOnGround = isOnGround;
        this.parent = parent;
    }

    public Character getCharacter() {
        return character;
    }

    public Parent getParent() {
        return parent;
    }
}
