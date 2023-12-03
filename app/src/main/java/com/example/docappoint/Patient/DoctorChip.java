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

    public String getNextAvailableDate(){
        return this.nextAvailableDate;
    }

    public String getNextAvailableTime(){
        return this.nextAvailableTime;
    }


    public void calculateNextAvailableSlot() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(this.uid).collection("Appointments")
                .whereEqualTo("isAccepted", false)
                .whereEqualTo("hasHappened", false)
                .orderBy("appointmentDate")
                .orderBy("appointmentTime")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Calendar calendar = null;
                    SimpleDateFormat dateFormat = null;
                    SimpleDateFormat timeFormat = null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assuming working hours are from 9 AM to 5 PM
                        calendar = Calendar.getInstance();
                        dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                        timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

                        // Find the next available slot based on current date and time
                        boolean slotFound = false;
                        while (!slotFound) {
                            String currentDate = dateFormat.format(calendar.getTime());
                            for (int hour = 9; hour <= 17; hour++) {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                for (int minute = 0; minute < 60; minute += 30) { // Assuming 30-minute slots
                                    calendar.set(Calendar.MINUTE, minute);
                                    String potentialSlotTime = timeFormat.format(calendar.getTime());

                                    boolean isOccupied = queryDocumentSnapshots.getDocuments().stream().anyMatch(doc -> {
                                        String docDate = doc.getString("appointmentDate");
                                        String docTime = doc.getString("appointmentTime");
                                        return currentDate.equals(docDate) && potentialSlotTime.equals(docTime);
                                    });

                                    if (!isOccupied) {
                                        this.nextAvailableDate = currentDate;
                                        this.nextAvailableTime = potentialSlotTime;
                                        slotFound = true;
                                        break;
                                    }
                                }
                                if (slotFound) break;
                            }
                            if (!slotFound) calendar.add(Calendar.DATE, 1); // Check the next day
                        }
                    } else {
                        // No appointments, so the doctor is available from the next slot from now
                        this.nextAvailableDate = dateFormat.format(calendar.getTime());
                        this.nextAvailableTime = timeFormat.format(calendar.getTime());
                    }
                })
                .addOnFailureListener(e -> Log.e("DoctorChip", "Error fetching appointments", e));
    }
}


