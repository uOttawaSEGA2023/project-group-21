package com.example.docappoint;

public class ListRequest {
    private String accountFirstName;
    private String accountLastName;

    private String accountType;

    private boolean wasRejected;

    public ListRequest(String accountFirstName, String accountLastName, String accountType) {
        this.accountFirstName = accountFirstName;
        this.accountLastName = accountLastName;
        this.accountType = accountType;

        // Default rejection to false to add to list
        this.wasRejected = false;

    }

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


}
