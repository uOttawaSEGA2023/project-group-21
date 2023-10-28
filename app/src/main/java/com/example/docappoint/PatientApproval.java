package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientApproval extends AppCompatActivity {

    // Initialize Firestore
    FirebaseFirestore fStore;
    EditText patientApprovalFirstNameTxt, patientApprovalLastNameTxt, patientApprovalHealthCardNumberTxt,
            patientApprovalAddressTxt, patientApprovalPhoneNumberTxt, patientApprovalEmailTxt;

    Button patientApprovalApproveRequestBtn, patientApprovalDenyRequestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_patient_request);

        // Link xml elements
        patientApprovalFirstNameTxt = findViewById(R.id.patientApprovalFirstNameText);
        patientApprovalLastNameTxt = findViewById(R.id.patientApprovalLastNameText);
        patientApprovalHealthCardNumberTxt = findViewById(R.id.patientApprovalHealthCardNumberText);
        patientApprovalAddressTxt = findViewById(R.id.patientApprovalAddressText);
        patientApprovalPhoneNumberTxt = findViewById(R.id.patientApprovalPhoneNumberText);
        patientApprovalEmailTxt = findViewById(R.id.patientApprovalEmailText);

        /*
        // Get Firestore instance to use
        fStore = FirebaseFirestore.getInstance();

        // Get the userId from the intent
        String userId = getIntent().getStringExtra("userId");

        // Use userId to fetch user data from Firestore
        DocumentReference patientDocRef = fStore.collection("PendingUsers").document(userId);

        patientDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Display user data in the EditText fields
                    patientApprovalFirstNameTxt.setText(documentSnapshot.getString("First Name"));
                    patientApprovalLastNameTxt.setText(documentSnapshot.getString("Last Name"));
                    patientApprovalHealthCardNumberTxt.setText(documentSnapshot.getString("Health Card Number"));
                    patientApprovalAddressTxt.setText(documentSnapshot.getString("Address"));
                    patientApprovalPhoneNumberTxt.setText(documentSnapshot.getString("Phone Number"));
                    patientApprovalEmailTxt.setText(documentSnapshot.getString("Email"));
                }
            }
        });
    }




*/
    }
}


        // BUTTON FUNCTIONALITY HERE
