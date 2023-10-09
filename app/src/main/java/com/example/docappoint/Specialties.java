package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Specialties extends AppCompatActivity {

    Button specialtiesBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialties);

        specialtiesBackBtn = findViewById(R.id.backToDoctorSignUpButton);

        // Back to doctor sign up page
        specialtiesBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



}