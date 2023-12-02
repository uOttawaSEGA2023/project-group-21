package com.example.docappoint.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.docappoint.R;

public class PatientRateDoctor extends AppCompatActivity {

    Button patientRateDoctorBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_rate_doctor);

        // Link xml files
        patientRateDoctorBackBtn = findViewById(R.id.patientRatingBackButton);

        // Back Button
        patientRateDoctorBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientPastAppointments.class));
                finish();
            }
        });
    }
}