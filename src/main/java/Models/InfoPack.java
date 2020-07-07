package Models;

import javafx.scene.Parent;

public class InfoPack {
    private Character character;
    private int side, summonPlace;
    private boolean isOnGround;
    private Parent parent;

    public int getSide() {
        return side;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public InfoPack(Character character, int side, boolean isOnGround, Parent parent){
        summonPlace = -1;
        this.character = character;
        this.side = side;
        this.isOnGround = isOnGround;
        this.parent = parent;
    }

    public InfoPack(Character character, int side, boolean isOnGround, Parent parent, int summonPlace){
        this.summonPlace = summonPlace;
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

    public int getSummonPlace() {
        return summonPlace;
    }
}
