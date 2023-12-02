package com.example.docappoint.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.docappoint.R;
import com.example.docappoint.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientNavigation extends AppCompatActivity {

    Button patientBookApptButton, patientUpcomingAppointmentsBtn, patientPastAppointmentsBtn;
    ImageButton patientSettingBtn;

    TextView patientNavigationNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_navigation);

        // Link UI to variable
        patientSettingBtn = findViewById(R.id.patientSettingButton);
        patientBookApptButton = findViewById(R.id.patientBookApptButton);
        patientPastAppointmentsBtn = findViewById(R.id.viewPatientPastAppointments);
        patientUpcomingAppointmentsBtn = findViewById(R.id.viewPatientUpcomingAppointments);
        patientNavigationNameTxt = findViewById(R.id.patientNameTextView);

        // Redirect to settings screen when clicked
        patientSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Settings.class));
                finish();
            }

        });

        patientBookApptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SelectDoctor.class));
                finish();
            }
        });

        // Redirect to PatientUpcomingAppointments screen when clicked
        patientUpcomingAppointmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientUpcomingAppointments.class));
                finish();
            }

        });

        // Redirect to PatientPastAppointments screen when clicked
        patientPastAppointmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientPastAppointments.class));
                finish();
            }

        });

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            String currentUserID = fAuth.getCurrentUser().getUid();

            //Get patient name
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            DocumentReference userRef = fStore.collection("Users").document(currentUserID);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("First Name");

                    if (firstName != null) {
                        patientNavigationNameTxt.setText(firstName);
                    }
                }


            });
        }
    }
}