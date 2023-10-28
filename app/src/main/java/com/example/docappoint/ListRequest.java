package com.example.docappoint;

import java.util.ArrayList;
import java.util.List;

public class ListRequest {

    // MANDATORY FIELDS
    private String accountFirstName;
    private String accountLastName;

    private String accountType;

    private boolean wasRejected;

    private String accountAddress;
    private String accountPhoneNumber;
    private String accountEmail;

    // UNIQUE PATIENT FIELDS
    private String accountHealthCardNumber;

    // UNIQUE DOCTOR FIELDS
    private String accountEmployeeNumber;
    private ArrayList<String> accountSpecialties;


    // Constructor for Patient
    public ListRequest(String accountFirstName, String accountLastName, String accountType, String accountAddress, String accountPhoneNumber, String accountEmail, String accountHealthCardNumber) {
        this.accountFirstName = accountFirstName;
        this.accountLastName = accountLastName;
        this.accountType = accountType;
        this.accountAddress = accountAddress;
        this.accountPhoneNumber = accountPhoneNumber;
        this.accountEmail = accountEmail;
        this.accountHealthCardNumber = accountHealthCardNumber;
    }

    // Constructor for Doctor
    public ListRequest(String accountFirstName, String accountLastName, String accountType, String accountAddress, String accountPhoneNumber, String accountEmail, String accountEmployeeNumber, ArrayList<String> accountSpecialties) {
        this.accountFirstName = accountFirstName;
        this.accountLastName = accountLastName;
        this.accountType = accountType;
        this.accountAddress = accountAddress;
        this.accountPhoneNumber = accountPhoneNumber;
        this.accountEmail = accountEmail;
        this.accountEmployeeNumber = accountEmployeeNumber;
        this.accountSpecialties = accountSpecialties;
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

    public boolean getStatus(){ return wasRejected; };

    public String getAccountAddress() {
        return accountAddress;
    }

    public String getAccountPhoneNumber() {
        return accountPhoneNumber;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

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

}
