package com.example.docappoint.Doctor;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.docappoint.Appointment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DoctorAppointmentRequest {
    private String accountFirstName;
    private String accountLastName;
    private String accountAddress;
    private String accountPhoneNumber;
    private String accountEmail;
    private String accountHealthCardNumber;
    private String appointmentDate;
    private String appointmentTime;
    private Appointment appointment;

    private DoctorAppointmentRequest(String accountFirstName, String accountLastName, String accountHealthCardNumber,
                                     String accountAddress, String accountPhoneNumber, String accountEmail,
                                     String appointmentDate, String appointmentTime, Appointment app) {
        this.accountFirstName = accountFirstName;
        this.accountLastName = accountLastName;
        this.accountHealthCardNumber = accountHealthCardNumber;
        this.accountAddress = accountAddress;
        this.accountPhoneNumber = accountPhoneNumber;
        this.accountEmail = accountEmail;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.appointment = app;
    }

    public static void createFromUID(Appointment appointment, PatientInfoCallback callback) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference patientRef = fStore.collection("Users").document(appointment.getPatientUID());
        patientRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Extract the patient info
                    String accountFirstName = document.getString("First Name");
                    String accountLastName = document.getString("Last Name");
                    String accountHealthCardNumber = document.getString("Health Card Number");
                    String accountAddress = document.getString("Address");
                    String accountPhoneNumber = document.getString("Phone Number");
                    String accountEmail = document.getString("Email");

                    // Create a new instance and pass it to the callback
                    DoctorAppointmentRequest doctorAppointmentRequest = new DoctorAppointmentRequest(
                            accountFirstName, accountLastName, accountHealthCardNumber, accountAddress,
                            accountPhoneNumber, accountEmail, appointment.getAppointmentDate(), appointment.getAppointmentTime(), appointment
                    );
                    callback.onCallback(doctorAppointmentRequest);
                } else {
                    Log.d("DoctorAppointmentRequest", "No such document");
                }
            } else {
                Log.d("DoctorAppointmentRequest", "get failed with ", task.getException());
            }
        });
    }

    public interface PatientInfoCallback {
        void onCallback(DoctorAppointmentRequest doctorAppointmentRequest);
    }

    // Getters
    public String getAppointmentDate() { return this.appointmentDate; }
    public String getAppointmentTime() { return this.appointmentTime; }
    public String getAccountFirstName() { return this.accountFirstName; }
    public String getAccountLastName() { return this.accountLastName; }
    public String getAccountHealthCardNumber() { return this.accountHealthCardNumber; }
    public String getAccountAddress() { return this.accountAddress; }
    public String getAccountEmail() { return this.accountEmail; }
    public String getAccountPhoneNumber() { return this.accountPhoneNumber; }

    public Appointment getAppointment() {
        return this.appointment;
    }
}

