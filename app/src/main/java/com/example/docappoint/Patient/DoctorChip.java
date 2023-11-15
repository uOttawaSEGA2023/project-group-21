package com.example.docappoint.Patient;

import com.example.docappoint.Appointment;

import java.util.ArrayList;
import java.util.List;

public class DoctorChip {
    private String firstName;
    private String lastName;
    private ArrayList<String> specialties;
    private float rating;
    private int numberOfRatings;
    private ArrayList<Appointment> appointments;
    private String uid;

    public DoctorChip(String firstName, String lastName, ArrayList<String> specialties, float rating, int numberOfRatings, ArrayList<Appointment> l, String uid){
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialties = specialties;
        this.rating = rating;
        this.numberOfRatings = numberOfRatings;
        this.appointments = l;
        this.uid = uid;
    }

    public String getAccountFirstName() {
        return this.firstName;
    }

    public String getAccountLastName() {
        return this.lastName;
    }

    public ArrayList<String> getSpecialties() {
        return this.specialties;
    }

    public float getRatingNumber() {
        return this.rating;
    }

    public int getNumberOfRatings() {
        return this.numberOfRatings;
    }

    public String getNextAvailableDate() {
        return "0";
    }

    public String getNextAvailableTime() {
        return "0";
    }
}
