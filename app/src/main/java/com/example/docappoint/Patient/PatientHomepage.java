package com.example.docappoint.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.docappoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PatientHomepage extends AppCompatActivity {

    Button patientContinue;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_homepage);

            //  Assign variables to direct xml files
            patientContinue = findViewById(R.id.patientContinueButton);

        // Initialize Firebase variables
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

            // Continue button leading to doctor navigation page
            patientContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAndUpdateAppointments();
                    startActivity(new Intent(getApplicationContext(), PatientNavigation.class));
                }
            });
    }

    private void checkAndUpdateAppointments() {
        String currentUID = fAuth.getCurrentUser().getUid();
        fStore.collection("Users").document(currentUID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document != null && document.exists()) {
                            List<Map<String, Object>> appointmentsList = (List<Map<String, Object>>) document.get("Appointments");

                            if (appointmentsList != null) {

                                long currentMillis = System.currentTimeMillis();
                                for (Map<String, Object> appointmentMap : appointmentsList) {

                                    // Get the date of the appointment
                                    String appointmentDate = (String) appointmentMap.get("appointmentDate");
                                    Log.d("Appointment Date", "Appointment Date: " + appointmentDate);

                                    // Get doctorUID
                                    String doctorUID = (String) appointmentMap.get("doctorUID");

                                    // Get the time of the appointment
                                    String appointmentTime = (String) appointmentMap.get("appointmentTime");
                                    Log.d("Appointment Time", "Appointment Time: " + appointmentTime);

                                    // Parse the appointmentTime into milliseconds
                                    long appointmentTimeInMillis = parseTimeToMillis(appointmentTime, appointmentDate);
                                    Log.d("Appointment Time IN MILLIS", "Appointment Time: " + appointmentTimeInMillis);

                                    if (appointmentTimeInMillis != -1) {

                                        // Add 30 minutes to appointment time
                                        appointmentTimeInMillis += 30 * 60 * 1000;

                                        Log.d("End Time", "End Time: " + appointmentTimeInMillis);

                                        Log.d("Current Time", "Current Time: " + currentMillis);

                                        // Check if appointment has passed (in terms of time passing end time)
                                        if (currentMillis > appointmentTimeInMillis) {

                                            // hasHappened field update
                                            appointmentMap.put("hasHappened", true);
                                            updateAppointmentInFirebase(currentUID, doctorUID, appointmentsList);
                                        }
                                    } else {
                                        Log.e("Time Parsing", "Error parsing appointment time.");
                                    }
                                }
                            }
                        }
                    }
                });
    }

    // Update hasHappened field for patients and doctors in Firebase
    private void updateAppointmentInFirebase(String currentUID, String doctorUID, List<Map<String, Object>> appointmentsList) {

        fStore.collection("Users").document(currentUID)
                .update("Appointments", appointmentsList)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Update Success", "Appointments updated successfully");
                    updateDoctorAppointmentInFirebase(doctorUID, appointmentsList);
                });

    }

    // Update doctor hasHappened
    private void updateDoctorAppointmentInFirebase(String doctorUID, List<Map<String, Object>> appointmentsList) {
        fStore.collection("Users").document(doctorUID)
                .update("Appointments", appointmentsList)
                .addOnSuccessListener(aVoid -> Log.d("Update Success", "Doctor's appointments updated successfully"));
    }

    // Method to parse time string into milliseconds
    private long parseTimeToMillis(String timeString, String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US);
            java.util.Date date = sdf.parse(dateString + " " + timeString);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }


}


