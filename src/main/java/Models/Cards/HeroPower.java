package Models.Cards;

public class HeroPower extends Card implements Cloneable{
    private boolean charge;

    public HeroPower(){}

    private HeroPower(HeroPower heroPower) {
        super(heroPower);
        charge = heroPower.isCharge();

    }

    @Override
    public String toString() {
        return super.toString()+"\n";
    }

    @Override
    public HeroPower newOne() {
        return new HeroPower(this);
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }
}
