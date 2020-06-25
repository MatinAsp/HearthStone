package Models.Cards;

import Models.Character;

public class Card extends Character {
    private String rarity,heroClass,type;
    private String description;
    private int mana,price;

    public Card(){

    }
    public Card(Card card){
        rarity = card.getRarity();
        heroClass = card.getHeroClass();
        type = card.getType();
        description = card.getDescription();
        mana = card.getMana();
        price = getPrice();
    }

    public String getHeroClass(){
        return heroClass;
    }

    public int getPrice(){
        return price;
    }

    @Override
    public String toString(){
        String answer="";
        answer+="\u26AB "+super.getName()+": ";
        answer+=description+"\n";
        answer+=" \u2606Type:"+type+" \u2605Rarity:"+rarity+" \u26AAMana:"+mana+" \u26C2Price:"+price+
                " \u26C9Class:"+heroClass;
        return answer;
    }

    public String getRarity() {
        return rarity;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getMana() {
        return mana;
    }

}
