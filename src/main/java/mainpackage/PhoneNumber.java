package mainpackage;


class PhoneNumber {
    private String phoneNumber;
    private Program program;
    private float bill;
    private int timeTalked;

    // Constructor
    public PhoneNumber(String phoneNumber, Program program) {
        this.phoneNumber = phoneNumber;
        this.program = program;
        this.timeTalked = 0;
    }

    public String getNumber() {
        return phoneNumber;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public float getBill() {
        return bill;
    }

    // This calculates the bill cost once a call is made
    public void makeCall(int time) {
        float totalMin = timeTalked + time;
        if (totalMin > program.getTalkingTime()) {

            float extraCost = totalMin - program.getTalkingTime();
            extraCost = extraCost * program.getTalkingTime();

            bill = program.getFixedCharge() + extraCost;
        } else
            bill = program.getFixedCharge();
    }

    public void setBill(float bill) {
        this.bill = bill;
    }
}

