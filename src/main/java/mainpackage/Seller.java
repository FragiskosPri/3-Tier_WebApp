package mainpackage;

import java.util.ArrayList;

public class Seller extends Users {
    private ArrayList<Client> clientList;
    private ArrayList<Program> programList;
    private int sellerID; // Way to Identify every Seller

    public Seller(){
      setId(UserType.SELLER);
    }

    public int getSellerID() {
        return sellerID;
    }

    public ArrayList<Program> getProgramList() {
        return programList;
    }

    public ArrayList<Client> getClientList() {
        return clientList;
    }

    public void setClientList(ArrayList<Client> clientList) {
        this.clientList = clientList;
    }

    public void setProgramList(ArrayList<Program> programList) {
        this.programList = programList;
    }

    public void setSellerID(int sellerID) {
        this.sellerID = sellerID;
    }
}