package com.example.docappoint;

import java.util.ArrayList;
import java.util.List;

public class ListRequest {

    // MANDATORY FIELDS
    private final String accountFirstName;
    private final String accountLastName;

    private final String accountType;

    private boolean wasRejected;

    private final String accountAddress;
    private final String accountPhoneNumber;
    private final String accountEmail;
    private final String accountPassword;
    private final String accountUID;

    // UNIQUE PATIENT FIELDS
    private String accountHealthCardNumber;

    // UNIQUE DOCTOR FIELDS
    private String accountEmployeeNumber;
    private ArrayList<String> accountSpecialties;


    // Constructor for Patient
    public ListRequest(String accountFirstName, String accountLastName, String accountType, String accountAddress, String accountPhoneNumber, String accountEmail,String accountPassword, String accountUID, String accountHealthCardNumber, boolean wasRejected) {
        this.accountFirstName = accountFirstName;
        this.accountLastName = accountLastName;
        this.accountType = accountType;
        this.accountAddress = accountAddress;
        this.accountPhoneNumber = accountPhoneNumber;
        this.accountEmail = accountEmail;
        this.accountPassword = accountPassword;
        this.accountUID = accountUID;
        this.accountHealthCardNumber = accountHealthCardNumber;
        this.wasRejected = wasRejected;
    }

    // Constructor for Doctor
    public ListRequest(String accountFirstName, String accountLastName, String accountType, String accountAddress, String accountPhoneNumber, String accountEmail, String accountPassword, String accountUID, String accountEmployeeNumber, ArrayList<String> accountSpecialties, boolean wasRejected) {
        this.accountFirstName = accountFirstName;
        this.accountLastName = accountLastName;
        this.accountType = accountType;
        this.accountAddress = accountAddress;
        this.accountPhoneNumber = accountPhoneNumber;
        this.accountEmail = accountEmail;
        this.accountPassword = accountPassword;
        this.accountUID = accountUID;
        this.accountEmployeeNumber = accountEmployeeNumber;
        this.accountSpecialties = accountSpecialties;
        this.wasRejected = wasRejected;
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

    public boolean getStatus(){ return wasRejected; }

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


    // DOCTOR GETTER METHODS

    public String getAccountEmployeeNumber() {
        return accountEmployeeNumber;
    }

    public ArrayList<String> getAccountSpecialties() {
        return accountSpecialties;
    }

    //setter for was rejected value
    public void setRejected(boolean b){
        this.wasRejected = b;
    }

}