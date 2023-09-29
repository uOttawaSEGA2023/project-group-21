// Import package
package com.example.testapplication;

// Extend Patient with User
public class Patient extends User {

    // Unique attributes for Patient
   private int healthCardNum;

    // Constructor for Patient
    public Patient(String firstName, String lastName, String email, String password, int phoneNumber, String
            address, int healthCardNum){
        super(firstName, lastName, email, password, phoneNumber, address);
        this.healthCardNum = healthCardNum;
    }

    // Main method
    public static void main (String [] args){

    }

}