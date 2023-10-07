package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DoctorHomepage extends AppCompatActivity {

    Button doctorContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_homepage);

        //  Assign variables to direct xml files
        doctorContinue = findViewById(R.id.doctorContinueButton);

        // Continue button leading to doctor navigation page
        doctorContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DoctorNavigation.class));
            }
        });
    }

}
