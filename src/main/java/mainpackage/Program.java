package mainpackage;

public class Program {
    private int talkingTime; // Talking Time
    private float fixedCharge; // fixed charge
    private float extraCharge; // extra charges
    private int programID;
    private int sellerID;

    public int getTalkingTime() {
        return talkingTime;
    }

    public float getFixedCharge() {
        return fixedCharge;
    }

    public float getExtraCharge() {
        return extraCharge;
    }

    public void setTalkingTime(int talkingTime) {
        this.talkingTime = talkingTime;
    }

    public void setFixedCharge(float fixedCharge) {
        this.fixedCharge = fixedCharge;
    }

    public void setExtraCharge(float extraCharge) {
        this.extraCharge = extraCharge;
    }

    public int getProgramID() {
        return programID;
    }

    public void setProgramID(int programID) {
        this.programID = programID;
    }

    public int getSellerID() {
        return sellerID;
    }

    public void setSellerID(int sellerID) {
        this.sellerID = sellerID;
    }

    public float calculateCost(int duration) {
        if (duration > talkingTime) {
            duration -= talkingTime;
            return extraCharge * duration + fixedCharge;
        } else return fixedCharge;
    }
}
