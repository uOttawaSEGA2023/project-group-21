package com.example.docappoint.Doctor;

import com.example.docappoint.Appointment;
import com.example.docappoint.User;

import java.util.List;

// Extend Doctor with User
public class Doctor extends User {

    // Unique attributes for Doctor
    private final int employeeNum;
    private final String[] specialties;

    private final float rating;
    private final int numRatings;

    private List<Appointment> appointments;

    // Constructor for Doctor
    public Doctor(String firstName, String lastName, String email, String password, String checkPassword, String phoneNumber, String
            address, int employeeNum, String[] specialties) {
        super(firstName, lastName, email, password, checkPassword, phoneNumber, address);

        this.employeeNum = employeeNum;
        this.specialties = specialties;
        this.rating = 0.0f;
        this.numRatings = 0;
    }


    // Main method
    public static void main(String[] args) {

    }
}
