package com.example.docappoint.Patient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.example.docappoint.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PatientNavigation extends AppCompatActivity {

    private TextView patientNavigationNameTxt, patientNextAppointmentFirstName, patientNextAppointmentLastName,
            patientNextAppointmentDate, patientNextAppointmentStartTime, patientNextAppointmentEndTime,
            patientLatestDoctorFirstName, patientLatestDoctorLastName, patientLatestDoctorRating,
            patientLatestDoctorAmountOfRatings;
    private Button patientBookApptButton, patientUpcomingAppointmentsBtn, patientPastAppointmentsBtn;
    private ImageButton patientSettingBtn;
    private RatingBar patientLatestDoctorRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_navigation);

        // Initialize UI Components
        initializeViews();

        // Fetch Patient Data
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            String patientId = fAuth.getCurrentUser().getUid();
            fetchPatientData(patientId);
        }
    }

    private void initializeViews() {
        patientSettingBtn = findViewById(R.id.patientSettingButton);
        patientBookApptButton = findViewById(R.id.patientBookApptButton);
        patientPastAppointmentsBtn = findViewById(R.id.viewPatientPastAppointments);
        patientUpcomingAppointmentsBtn = findViewById(R.id.viewPatientUpcomingAppointments);
        patientNavigationNameTxt = findViewById(R.id.patientNameTextView);

        patientNextAppointmentFirstName = findViewById(R.id.doctorAppointmentfirstNameRecycle);
        patientNextAppointmentLastName = findViewById(R.id.doctorAppointmentlastNameRecycle);
        patientNextAppointmentDate = findViewById(R.id.dateDoctorAppointmentCard2);
        patientNextAppointmentStartTime = findViewById(R.id.patientAppointmentCardStartTime);
        patientNextAppointmentEndTime = findViewById(R.id.patientAppointmentCardEndTime);

        patientLatestDoctorFirstName = findViewById(R.id.doctorAppointmentfirstNameRecycle2);
        patientLatestDoctorLastName = findViewById(R.id.doctorAppointmentlastNameRecycle2);
        patientLatestDoctorRating = findViewById(R.id.numberRatingTextPatientNav);
        patientLatestDoctorAmountOfRatings = findViewById(R.id.numRatingsPatientNav);
        patientLatestDoctorRatingBar = findViewById(R.id.ratingBarRecyclePatientNav);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        patientSettingBtn.setOnClickListener(v -> navigateTo(Settings.class));
        patientBookApptButton.setOnClickListener(v -> navigateTo(SelectDoctor.class));
        patientUpcomingAppointmentsBtn.setOnClickListener(v -> navigateTo(PatientUpcomingAppointments.class));
        patientPastAppointmentsBtn.setOnClickListener(v -> navigateTo(PatientPastAppointments.class));
    }

    private void fetchPatientData(String patientId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference patientRef = db.collection("Users").document(patientId);
        patientRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Process Patient Name
                String patientName = documentSnapshot.getString("First Name");
                patientNavigationNameTxt.setText(patientName != null ? patientName : "N/A");

                // Process Appointments
                List<Map<String, Object>> appointments = (List<Map<String, Object>>) documentSnapshot.get("Appointments");
                if (appointments != null && !appointments.isEmpty()) {
                    processNextAppointment(appointments);
                    processLatestDoctor(appointments);
                }
            }
        }).addOnFailureListener(e -> Log.e("PatientNavigation", "Error fetching patient data", e));
    }

    private void processNextAppointment(List<Map<String, Object>> appointments) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.getDefault());
        Date earliestDate = null;
        Map<String, Object> nextAppointment = null;

        for (Map<String, Object> appointment : appointments) {
            boolean isAccepted = Boolean.TRUE.equals(appointment.get("isAccepted"));
            boolean hasHappened = Boolean.FALSE.equals(appointment.get("hasHappened"));
            if (isAccepted && hasHappened) {
                try {
                    Date appointmentDate = format.parse(appointment.get("appointmentDate") + " " + appointment.get("appointmentTime"));
                    if (appointmentDate != null && (earliestDate == null || appointmentDate.before(earliestDate))) {
                        earliestDate = appointmentDate;
                        nextAppointment = appointment;
                    }
                } catch (ParseException e) {
                    Log.e("PatientNavigation", "Date parsing error", e);
                }
            }
        }

        if (nextAppointment != null) {
            updateNextAppointmentUI(nextAppointment);
        }
    }

    private void processLatestDoctor(List<Map<String, Object>> appointments) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.getDefault());
        Date latestDate = null;
        Map<String, Object> latestDoctorAppointment = null;

        for (Map<String, Object> appointment : appointments) {
            boolean hasHappened = Boolean.TRUE.equals(appointment.get("hasHappened"));
            if (hasHappened) {
                try {
                    Date appointmentDate = format.parse(appointment.get("appointmentDate") + " " + appointment.get("appointmentTime"));
                    if (appointmentDate != null && (latestDate == null || appointmentDate.after(latestDate))) {
                        latestDate = appointmentDate;
                        latestDoctorAppointment = appointment;
                    }
                } catch (ParseException e) {
                    Log.e("PatientNavigation", "Date parsing error", e);
                }
            }
        }

        if (latestDoctorAppointment != null) {
            updateLatestDoctorUI(latestDoctorAppointment);
        }
    }
    private void updateNextAppointmentUI(Map<String, Object> appointment) {
        String doctorUID = (String) appointment.get("doctorUID");
        fetchDoctorDetails(doctorUID, (firstName, lastName, rating, numOfRatings) -> {
            patientNextAppointmentFirstName.setText(firstName != null ? firstName : "N/A");
            patientNextAppointmentLastName.setText(lastName != null ? lastName : "N/A");
        });

        String date = (String) appointment.get("appointmentDate");
        String startTime = (String) appointment.get("appointmentTime");
        String endTime = calculateEndTime(startTime);

        patientNextAppointmentDate.setText(date != null ? date : "N/A");
        patientNextAppointmentStartTime.setText(startTime != null ? startTime : "N/A");
        patientNextAppointmentEndTime.setText(endTime != null ? endTime : "N/A");
    }

    private String calculateEndTime(String startTime) {
        // Define the format for the input and output time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

        try {
            // Parse the start time into a Date object
            Date startTimeDate = timeFormat.parse(startTime);

            // Check if the startTimeDate is not null
            if (startTimeDate != null) {
                // Create a Calendar instance and set the parsed start time
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTimeDate);

                // Add 30 minutes to the start time
                calendar.add(Calendar.MINUTE, 30);

                // Format and return the end time as a string
                return timeFormat.format(calendar.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the input startTime if parsing fails or startTimeDate is null
        return startTime;
    }



    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
        finish();
    }

    private void updateLatestDoctorUI(Map<String, Object> appointment) {
        String doctorUID = (String) appointment.get("doctorUID");
        fetchDoctorDetails(doctorUID, (firstName, lastName, rating, numOfRatings) -> {
            patientLatestDoctorFirstName.setText(firstName != null ? firstName : "N/A");
            patientLatestDoctorLastName.setText(lastName != null ? lastName : "N/A");
            patientLatestDoctorRating.setText(rating != null ? String.format(Locale.getDefault(), "%.1f", rating) : "N/A");
            patientLatestDoctorRatingBar.setRating(rating != null ? rating : 0);
            patientLatestDoctorAmountOfRatings.setText(numOfRatings != null ? String.valueOf(numOfRatings) : "N/A");
        });
    }

    private void fetchDoctorDetails(String doctorUID, OnDoctorDetailsFetched listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(doctorUID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String doctorFirstName = documentSnapshot.getString("First Name");
                String doctorLastName = documentSnapshot.getString("Last Name");
                Double doctorRating = documentSnapshot.getDouble("AvgRating"); // Assuming rating is stored as a Double
                Long doctorNumOfRatings = documentSnapshot.getLong("numOfRatings"); // Assuming number of ratings is stored as a Long
                if (listener != null) {
                    listener.onFetched(doctorFirstName, doctorLastName,
                            doctorRating != null ? doctorRating.floatValue() : null,
                            doctorNumOfRatings != null ? doctorNumOfRatings.intValue() : null);
                }
            } else {
                Log.d("PatientNavigation", "Doctor document does not exist");
                if (listener != null) {
                    listener.onFetched(null, null, null, null);
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("PatientNavigation", "Error getting doctor details", e);
            if (listener != null) {
                listener.onFetched(null, null, null, null);
            }
        });
    }

    // Updated interface to include number of ratings
    interface OnDoctorDetailsFetched {
        void onFetched(@Nullable String firstName, @Nullable String lastName,
                       @Nullable Float rating, @Nullable Integer numOfRatings);
    }


}
