package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PatientHomepage extends AppCompatActivity {

    Button patientContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_homepage);

            //  Assign variables to direct xml files
            patientContinue = findViewById(R.id.patientContinueButton);

            // Continue button leading to doctor navigation page
            patientContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), PatientNavigation.class));
                }
            });
    }
}

