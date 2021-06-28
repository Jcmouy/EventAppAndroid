package com.example.admin.eventappb.Utils;

/**
 * Created by JC on 25/2/2018.
 */

public class MyInvitedContacts {
    String mobile;
    String name;
    String confirmation;

    public MyInvitedContacts(String mobile, String name, String confirmation) {
        this.mobile = mobile;
        this.name = name;
        this.confirmation = confirmation;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }
}
