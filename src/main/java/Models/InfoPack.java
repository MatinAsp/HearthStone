package Models;


import Models.Cards.Card;

public class InfoPack {
    private Character character;
    private int side, summonPlace;
    private boolean isOnGround;

    public InfoPack() {}

    public int getSide() {
        return side;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public InfoPack(Character character, int side, boolean isOnGround){
        summonPlace = -1;
        this.character = character;
        this.side = side;
        this.isOnGround = isOnGround;
    }

    public InfoPack(Character character, int side, boolean isOnGround, int summonPlace){
        this.summonPlace = summonPlace;
        this.character = character;
        this.side = side;
        this.isOnGround = isOnGround;
    }

    public Character getCharacter() {
        return character;
    }

    public int getSummonPlace() {
        return summonPlace;
    }

    public void setSummonPlace(int summonPlace) {
        this.summonPlace = summonPlace;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public void setCharacter(Card card) {
        character = card;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }
}
