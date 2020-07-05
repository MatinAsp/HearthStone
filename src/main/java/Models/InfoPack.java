package Models;

public class InfoPack {
    private Character character;
    private int side;
    private boolean isOnGround;

    public int getSide() {
        return side;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public InfoPack(Character character, int side, boolean isOnGround){
        this.character = character;
        this.side = side;
        this.isOnGround = isOnGround;
    }

    public Character getCharacter() {
        return character;
    }
}
