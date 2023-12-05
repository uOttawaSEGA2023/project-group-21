package com.example.docappoint.Patient;

import android.util.Log;

import com.example.docappoint.Appointment;
import com.example.docappoint.Doctor.Doctor;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

public class DoctorChip {
    private final String firstName;
    private final String lastName;
    private final ArrayList<String> specialties;
    private final float rating;
    private final int numberOfRatings;
    private final ArrayList<Appointment> appointments;
    private final String uid;
    private String nextAvailableDate = "0";
    private String nextAvailableTime = "0";

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


    public String getUID() {
        return uid;
    }

}


