package com.example.docappoint.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.docappoint.R;

public class PatientPastAppointments extends AppCompatActivity {

    Button patientPastAppointmentBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment_history);

        // Link xml files to variable
        patientPastAppointmentBackBtn = findViewById(R.id.patientPastAppointmentsBackButton);

        // Back Button
        patientPastAppointmentBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientNavigation.class));
            }
        });
    }



}