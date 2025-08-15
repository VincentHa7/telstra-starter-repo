package au.com.telstra.simcardactivator;

public class SimCard {
    String iccid;
    String customerEmail;

    public SimCard (String iccid, String customerEmail) {
        this.iccid = iccid;
        this.customerEmail = customerEmail;
    }

    public String getIccid() {
        return iccid;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
