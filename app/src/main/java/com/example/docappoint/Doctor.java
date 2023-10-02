package com.example.docappoint;

// Extend Doctor with User
public class Doctor extends User {

    // Unique attributes for Doctor
    private int employeeNum, numSpecialties;
    private String[] specialties;

    // Constructor for Doctor
    public Doctor(byte age, String firstName, String lastName, String email, String password, String checkPassword, String phoneNumber, String
            address, int employeeNum, String[] specialties, int numSpecialties) {
        super(firstName, lastName, email, password, checkPassword, phoneNumber, address);

        this.employeeNum = employeeNum;
        this.specialties = new String[numSpecialties];
    }

    // Main method
    public static void main(String[] args) {

    }
}