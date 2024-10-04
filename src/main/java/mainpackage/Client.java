package mainpackage;

import java.util.ArrayList;

public class Client extends Users {
    private int AFM;
    ArrayList<Phonecall> Phonecalls;
    private String phoneNumber;
    private float payment;
    public int programID;


    public Client() {
        setId(UserType.CLIENT);
    }

    public int getProgramID() {
        return programID;
    }

    public void setProgramID(int programID) {
        this.programID = programID;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAFM() {
        return AFM;
    }

    public void setAFM(int AFM) {
        this.AFM = AFM;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<Phonecall> getPhonecalls() {
        return Phonecalls;
    }

    public void setPhonecalls(ArrayList<Phonecall> Phonecalls) {
        this.Phonecalls = Phonecalls;
    }

    public float getPayment() {
        return payment;
    }

    public void setPayment(float payment) {
        this.payment = payment;
    }
}
