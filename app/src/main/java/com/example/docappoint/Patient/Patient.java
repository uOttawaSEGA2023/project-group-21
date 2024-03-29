package com.example.docappoint.Patient;

import com.example.docappoint.User;

// Extend Patient with User
public class Patient extends User {

    // Unique attributes for Patient
    private final int healthCardNum;

    // Constructor for Patient
    public Patient(String firstName, String lastName, String email, String password, String confirmPassword, String phoneNumber, String
            address, int healthCardNum) {
        super(firstName, lastName, email, password, phoneNumber, address, confirmPassword);
        this.healthCardNum = healthCardNum;
    }

    // Main method
    public static void main(String[] args) {

    }

}

