package com.example.docappoint.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.docappoint.R;

public class PatientRateDoctor extends AppCompatActivity {

    Button patientRateDoctorBackBtn, patientRateConfirmBtn;
    EditText patientRateDoctorNum;
    RatingBar patientRateRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_rate_doctor);

        // Link xml files
        patientRateDoctorBackBtn = findViewById(R.id.patientRatingBackButton);
        patientRateDoctorNum = findViewById(R.id.patientRatingNum);
        patientRateRatingBar = findViewById(R.id.patientRatingBar);
        patientRateConfirmBtn = findViewById(R.id.patientConfirmRatingButton);

        addListeners();
    }

    private void addListeners(){
        patientRateDoctorNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String num = s.toString();
                if (!num.isEmpty()) {
                    float rating = Float.parseFloat(num);
                    patientRateRatingBar.setRating(rating);
                }
            }
        });

        patientRateRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    patientRateDoctorNum.setText(String.valueOf(rating));
                }
            }
        });

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