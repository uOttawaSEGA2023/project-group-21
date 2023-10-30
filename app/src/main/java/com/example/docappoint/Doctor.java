package com.example.docappoint;

// Extend Doctor with User
public class Doctor extends User {

    // Unique attributes for Doctor
    private int employeeNum;
    private String[] specialties;

    // Constructor for Doctor
    public Doctor(String firstName, String lastName, String email, String password, String checkPassword, String phoneNumber, String
            address, int employeeNum, String[] specialties) {
        super(firstName, lastName, email, password, checkPassword, phoneNumber, address);

        this.employeeNum = employeeNum;
        this.specialties = specialties;
    }


    // Main method
    public static void main(String[] args) {

    }
}
