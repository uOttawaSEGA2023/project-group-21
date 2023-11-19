package com.example.docappoint.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.docappoint.R;
import com.example.docappoint.Settings;

public class PatientNavigation extends AppCompatActivity {

    Button patientSettingBtn, patientBookApptButton, patientUpcomingAppointmentsBtn, patientPastAppointmentsBtn;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_navigation);

        // Link UI to variable
        patientSettingBtn = findViewById(R.id.patientSettingButton);
        patientBookApptButton = findViewById(R.id.patientBookApptButton);
        patientPastAppointmentsBtn = findViewById(R.id.viewPatientPastAppointments);
        patientUpcomingAppointmentsBtn = findViewById(R.id.viewPatientUpcomingAppointments);

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
    }
}