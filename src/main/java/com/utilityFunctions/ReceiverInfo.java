package com.utilityFunctions;

// This is purely used for the call generation

public class ReceiverInfo {
    private String phonenumber;
    private int AFM;

    public ReceiverInfo(String phonenumber, int AFM) {
        this.phonenumber = phonenumber;
        this.AFM = AFM;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public int getAFM() {
        return AFM;
    }
}
