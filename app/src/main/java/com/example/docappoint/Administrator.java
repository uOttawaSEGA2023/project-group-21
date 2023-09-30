package com.example.docappoint;

public class Administrator extends User {

    // Credentials for admin log in
    public Administrator(String email, String password){
        super(email, password);
    }

    // Main method
    public static void main (String [] args){
        Administrator administrator = new Administrator("admin@docappoint.com", "docappoint");
    }

}

