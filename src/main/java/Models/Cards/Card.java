package Models.Cards;

import Models.Character;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Card extends Character {
    @Column
    private String rarity;
    @Column
    private String heroClass;
    @Column
    private String type;
    @Column
    private String description;
    @Column
    private int mana;
    @Column
    private int price;

    public Card(){

    }
    public Card(Card card){
        super(card.getName());
        rarity = card.getRarity();
        heroClass = card.getHeroClass();
        type = card.getType();
        description = card.getDescription();
        mana = card.getMana();
        price = card.getPrice();
    }

    public String getHeroClass(){
        return heroClass;
    }

    public int getPrice(){
        return price;
    }

//    @Override
//    public String toString(){
//        String answer="";
//        answer+="\u26AB "+super.getName()+": ";
//        answer+=description+"\n";
//        answer+=" \u2606Type:"+type+" \u2605Rarity:"+rarity+" \u26AAMana:"+mana+" \u26C2Price:"+price+
//                " \u26C9Class:"+heroClass;
//        return answer;
//    }


    @Override
    public String toString() {
        return super.toString() + " Card{" +
                "rarity='" + rarity + '\'' +
                ", heroClass='" + heroClass + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", mana=" + mana +
                ", price=" + price +
                '}';
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

    public void setDescription(String description){
        this.description = description;
    }

    @Override
    public Card newOne() {
        return new Card(this);
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setHeroClass(String heroClass) {
        this.heroClass = heroClass;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
