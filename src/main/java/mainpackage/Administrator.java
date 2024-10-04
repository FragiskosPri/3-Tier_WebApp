package mainpackage;

import java.util.ArrayList;

public class Administrator extends Users{
  ArrayList<Seller> sellerList;
  ArrayList<Program> programList;

    public Administrator(){
        this.setId(UserType.ADMINISTRATOR);
    }

    public ArrayList<Seller> getSellerList() {
        return sellerList;
    }

    public void setSellerList(ArrayList<Seller> sellerList) {
        this.sellerList = sellerList;
    }

    public ArrayList<Program> getProgramList() {
        return programList;
    }

    public void setProgramList(ArrayList<Program> programList) {
        this.programList = programList;
    }
}
