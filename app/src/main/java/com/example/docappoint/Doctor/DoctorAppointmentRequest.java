package com.example.docappoint.Doctor;

import java.util.ArrayList;

public class DoctorAppointmentRequest {
    private String accountFirstName;
    private String accountLastName;

    private String accountType;

    private String accountAddress;
    private String accountPhoneNumber;
    private String accountEmail;
    private String accountPassword;
    private  String accountUID;

    // UNIQUE PATIENT FIELDS
    private String accountHealthCardNumber;

    // Constructor for Patient
    public DoctorAppointmentRequest(String accountFirstName, String accountLastName, String accountType, String accountAddress, String accountPhoneNumber, String accountEmail,String accountPassword, String accountUID, String accountHealthCardNumber, boolean wasRejected) {
        this.accountFirstName = accountFirstName;
        this.accountLastName = accountLastName;
        this.accountType = accountType;
        this.accountAddress = accountAddress;
        this.accountPhoneNumber = accountPhoneNumber;
        this.accountEmail = accountEmail;
        this.accountPassword = accountPassword;
        this.accountUID = accountUID;
        this.accountHealthCardNumber = accountHealthCardNumber;
    }

    // MANDATORY GETTER METHODS

    public String getAccountFirstName() {
        return accountFirstName;
    }

    public String getAccountLastName(){
        return accountLastName;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public String getAccountPhoneNumber() {
        return accountPhoneNumber;
    }

    public String getAccountEmail() {
        return accountEmail;
    }
    public String getAccountPassword() {
        return accountPassword;
    }
    public String getAccountUID() {return  accountUID;}

    // PATIENT GETTER METHODS
    public String getAccountHealthCardNumber() {
        return accountHealthCardNumber;
    }

}
