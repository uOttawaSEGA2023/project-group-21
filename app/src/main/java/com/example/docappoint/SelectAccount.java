package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectAccount extends AppCompatActivity {

    Button specifyPatientButton;
    Button specifyDoctorButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_account);

        // Connect UI elements to backend
       specifyPatientButton = findViewById(R.id.specifyPatientButton);
       specifyDoctorButton = findViewById(R.id.specifyDoctorButton);

        //Handle click for the button
        specifyPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientRegister.class));
            }
        });

        specifyDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DoctorRegister.class));
            }
        });
    }
}