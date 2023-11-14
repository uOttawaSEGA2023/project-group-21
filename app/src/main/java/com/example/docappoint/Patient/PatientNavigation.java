package com.example.docappoint.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.docappoint.R;
import com.example.docappoint.Settings;

public class PatientNavigation extends AppCompatActivity {

    Button patientSettingBtn, patientBookApptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_navigation);

        // Link UI to variable
        patientSettingBtn = findViewById(R.id.patientSettingButton);
        patientBookApptButton = findViewById(R.id.patientBookApptButton);

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
                startActivity(new Intent(getApplicationContext(), BookAppointment.class));
                finish();
            }

        });


    }
}