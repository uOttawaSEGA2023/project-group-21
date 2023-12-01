package com.example.docappoint.Doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.docappoint.R;

public class DoctorApprovedAppointments extends AppCompatActivity {

    Button doctorApprovedAppointmentsBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_pending_appt_requests);

        doctorApprovedAppointmentsBackBtn = findViewById(R.id.doctorApptRequestsBackButton);

        doctorApprovedAppointmentsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DoctorNavigation.class));
                finish();
            }
        });
    }
}