package com.example.docappoint.Doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.docappoint.R;
import com.example.docappoint.Settings;

public class DoctorNavigation extends AppCompatActivity {

    Button doctorSettingsBtn, doctorViewAppointmentHistoryBtn, doctorAddShiftButton, doctorViewAppointmentRequestsBtn;
    TextView doctorViewAllTextView;
    Button viewShiftBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_navigation);

        doctorSettingsBtn = findViewById(R.id.doctorSettingButton);
        doctorViewAppointmentHistoryBtn = findViewById(R.id.doctorViewAppointmentHistoryButton);
        doctorViewAppointmentRequestsBtn = findViewById(R.id.doctorViewAppointmentRequestsButton);
        doctorAddShiftButton = findViewById(R.id.doctorAddShiftButton);
        doctorViewAllTextView = findViewById(R.id.clickableViewAllNextAppt);

        doctorViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DoctorAppointments.class));
                finish();
            }
        });
      
        viewShiftBtn = findViewById(R.id.doctorViewHistoryBtn);


        // Redirect to settings screen when clicked
        doctorSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Settings.class));
                finish();
            }
        });

        doctorViewAppointmentHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DoctorApptHistory.class));
                finish();
            }
        });

        viewShiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ShiftHistory.class));
                finish();
            }
        });


        doctorViewAppointmentRequestsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), DoctorAppointments.class));
            }

        });

    }


}