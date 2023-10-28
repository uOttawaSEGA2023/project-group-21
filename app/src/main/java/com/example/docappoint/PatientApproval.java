package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientApproval extends AppCompatActivity {

    // Initialize Firestore
    FirebaseFirestore fStore;

    // Initialize Firebase Auth
    FirebaseAuth mAuth;

    EditText patientApprovalFirstNameTxt, patientApprovalLastNameTxt, patientApprovalHealthCardNumberTxt,
            patientApprovalAddressTxt, patientApprovalPhoneNumberTxt, patientApprovalEmailTxt;

    Button patientApprovalBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_patient_request);

        // Initialize Firebase Firestore and Firebase Auth
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Link xml elements
        patientApprovalFirstNameTxt = findViewById(R.id.patientApprovalFirstNameText);
        patientApprovalLastNameTxt = findViewById(R.id.patientApprovalLastNameText);
        patientApprovalHealthCardNumberTxt = findViewById(R.id.patientApprovalHealthCardNumberText);
        patientApprovalAddressTxt = findViewById(R.id.patientApprovalAddressText);
        patientApprovalPhoneNumberTxt = findViewById(R.id.patientApprovalPhoneNumberText);
        patientApprovalEmailTxt = findViewById(R.id.patientApprovalEmailText);

        // Button to navigate back
        patientApprovalBackBtn = findViewById(R.id.patientApprovalBackButton);

        FirebaseAuth mAuth;

                   // Retrieve user data from the intent extras
                    Intent intent = getIntent();
                    if (intent != null) {
                        String firstName = intent.getStringExtra("firstName");
                        String lastName = intent.getStringExtra("lastName");
                        String healthCardNumber = intent.getStringExtra("healthCardNumber");
                        String address = intent.getStringExtra("address");
                        String phoneNumber = intent.getStringExtra("phoneNumber");
                        String email = intent.getStringExtra("email");

                        // Update the EditText fields with the retrieved data
                        patientApprovalFirstNameTxt.setText(firstName);
                        patientApprovalLastNameTxt.setText(lastName);
                        patientApprovalHealthCardNumberTxt.setText(healthCardNumber);
                        patientApprovalAddressTxt.setText(address);
                        patientApprovalPhoneNumberTxt.setText(phoneNumber);
                        patientApprovalEmailTxt.setText(email);
                    }


        // Button functionality to navigate back
        patientApprovalBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                finish();
            }
        });
    }
}

