package Models.Cards;

public class Card {
    private String name,rarity,heroClass,type;
    private String description;
    private int mana,price;

    public Card(){

    }

    public String getHeroClass(){
        return heroClass;
    }

    public int getPrice(){
        return price;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        String answer="";
        answer+="\u26AB "+name+": ";
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
