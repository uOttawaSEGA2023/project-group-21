package com.example.docappoint.Doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.docappoint.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorNextAppointmentPatientInfo extends AppCompatActivity {

    // Declare variables
    public static final String PATIENT_UID_KEY = "patientUID";
    TextView doctorNextAppointmentFirstNameTxt, doctorNextAppointmentLastNameTxt,  doctorNextAppointmentPhoneNumberTxt, doctorNextAppointmentHealthCardTxt, doctorNextAppointmentAddressTxt, doctorNextAppointmentEmailTxt;
    Button doctorNextAppointmentBackBtn, doctorNextAppointmentRejectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info_rejectbtnonly);

        // Link xml files
        doctorNextAppointmentFirstNameTxt = findViewById(R.id.patientApprovalFirstNameTextDoc);
        doctorNextAppointmentLastNameTxt = findViewById(R.id.patientApprovalLastNameTextDoc);
        doctorNextAppointmentHealthCardTxt = findViewById(R.id.patientApprovalHealthCardNumberTextDoc);
        doctorNextAppointmentAddressTxt = findViewById(R.id.patientApprovalAddressTextDoc);
        doctorNextAppointmentEmailTxt = findViewById(R.id.patientApprovalEmailTextDoc);
        doctorNextAppointmentPhoneNumberTxt = findViewById(R.id.patientApprovalPhoneNumberTextDoc);
        doctorNextAppointmentBackBtn = findViewById(R.id.patientApprovalBackButtonDoc);

       doctorNextAppointmentRejectBtn = findViewById(R.id.patientApprovalDenyRequestButtonDoc);
        doctorNextAppointmentRejectBtn.setVisibility(View.GONE);

        // Get intent for patientUID (next appointment patient)
        String patientUID = getIntent().getStringExtra(PATIENT_UID_KEY);
        Log.d("PATIENTUID INTENT", "THIS: " + patientUID);

        // Get patient details with passed patientUID
        receivePatientDetails(patientUID);

        // Back Button
        doctorNextAppointmentBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DoctorNavigation.class));
                finish();
            }
        });
    }

    // Method to extract "Users" data from patientUID
    private void receivePatientDetails(String patientUID) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference patientRef = fStore.collection("Users").document(patientUID);
        patientRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("First Name");
                String lastName = documentSnapshot.getString("Last Name");
                String healthCardNumber = documentSnapshot.getString("HealthCardNumber");
                String phoneNumber = documentSnapshot.getString("Phone Number");
                String address = documentSnapshot.getString("Address");
                String email = documentSnapshot.getString("Email");

                // Put data in textfields
                doctorNextAppointmentFirstNameTxt.setText(firstName);
                doctorNextAppointmentLastNameTxt.setText(lastName);
                doctorNextAppointmentHealthCardTxt.setText(healthCardNumber);
                doctorNextAppointmentAddressTxt.setText(address);
                doctorNextAppointmentEmailTxt.setText(email);
                doctorNextAppointmentPhoneNumberTxt.setText(phoneNumber);
            }
        });
    }
}