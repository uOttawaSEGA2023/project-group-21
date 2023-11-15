package com.example.docappoint.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.example.docappoint.Administrator.AdminNavigation;
import com.example.docappoint.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientRejectApproval extends AppCompatActivity {

    // Initialize Firestore
    FirebaseFirestore fStore;

    // Initialize Firebase Auth
    FirebaseAuth mAuth;

    EditText patientApprovalFirstNameTxt, patientApprovalLastNameTxt, patientApprovalHealthCardNumberTxt,
            patientApprovalAddressTxt, patientApprovalPhoneNumberTxt, patientApprovalEmailTxt;

    Button patientApprovalBackBtn, patientApprovalApproveRequestbtn, patientApprovalDenyRequestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approveonly_patient_request);

        // Initialize Firebase Firestore and Firebase Auth
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Link xml elements
        patientApprovalFirstNameTxt = findViewById(R.id.patientApprovalOnlyFirstNameText);
        patientApprovalLastNameTxt = findViewById(R.id.patientApprovalOnlyLastNameText);
        patientApprovalHealthCardNumberTxt = findViewById(R.id.patientApprovalOnlyHealthCardNumberText);
        patientApprovalAddressTxt = findViewById(R.id.patientApprovalOnlyAddressText);
        patientApprovalPhoneNumberTxt = findViewById(R.id.patientApprovalOnlyPhoneNumberText);
        patientApprovalEmailTxt = findViewById(R.id.patientApprovalOnlyEmailText);

        // Buttons
        patientApprovalBackBtn = findViewById(R.id.patientApprovalOnlyBackButton);
        patientApprovalApproveRequestbtn = findViewById(R.id.patientApprovalOnlyApproveRequestButton);

        // Retrieve user data from the intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");
            String healthCardNumber = intent.getStringExtra("healthCardNumber");
            String address = intent.getStringExtra("address");
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");
            String uid = intent.getStringExtra("uid");

            // Update the EditText fields with the retrieved data
            patientApprovalFirstNameTxt.setText(firstName);
            patientApprovalLastNameTxt.setText(lastName);
            patientApprovalHealthCardNumberTxt.setText(healthCardNumber);
            patientApprovalAddressTxt.setText(address);
            patientApprovalPhoneNumberTxt.setText(phoneNumber);
            patientApprovalEmailTxt.setText(email);

            // Approve button will copy PendingUsers collection to Users
            patientApprovalApproveRequestbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Re-create the firebase auth account but using the Users collection information
                    deleteFirebaseAccount(uid, new OnDeleteAccountCompletedListener() {
                        @Override
                        public void onDeleteAccountCompleted() {
                            savePatientDataToUsers(firstName, lastName, healthCardNumber, address, phoneNumber, email, password);

                            Log.d("Email Debug", "Email: " + email);
                            Log.d("UID Debug", "UID: " + uid);

                            deletePatientDataFromPendingUsers(uid);

                            startActivity(new Intent(getApplicationContext(), AdminNavigation.class));

                            // Sign admin back in after action is made to user request
                            signAdmin();
                            finish();
                        }
                    });
                }
            });

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

    // Using the RejectedUsers information, save it on a new colection called Users in firebase
    private void savePatientDataToUsers(String firstName, String lastName, String healthCardNumber, String address, String phoneNumber, String email, String password) {

        // Create authentication using Firebase Auth for Users collection
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        // Using email and password
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Get the user data that is created
                        FirebaseUser user = fAuth.getCurrentUser();

                        // Get the user's UID
                        String patientUID = user.getUid();

                        // Save the reference to the "Users" collection
                        DocumentReference patientDocument = fStore.collection("Users").document(patientUID);

                        Map<String, Object> patientData = new HashMap<>();
                        patientData.put("First Name", firstName);
                        patientData.put("Last Name", lastName);
                        patientData.put("Health Card Number", healthCardNumber);
                        patientData.put("Address", address);
                        patientData.put("Phone Number", phoneNumber);
                        patientData.put("Email", email);
                        patientData.put("Password", password);
                        patientData.put("isPatient", 1);
                        patientData.put("isApproved", true);
                        patientData.put("UID", patientUID);

                        Log.d("Email Debug 1", "Email: " + email);

                        patientDocument.set(patientData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // Sign admin back in after action is made to user request
                                        signAdmin();

                                        // If successful display success toast message
                                        Toast.makeText(PatientRejectApproval.this, "User approved!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {

                                        // Sign admin back in after action is made to user request
                                        signAdmin();

                                        // If error occurs display error toast message
                                        Toast.makeText(PatientRejectApproval.this, "Failed to approve user" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        ;

                    }

                });

    }

    private void deletePatientDataFromPendingUsers(String uid) {
        CollectionReference rejectedUsersCollection = fStore.collection("RejectedUsers");

        // Get the document reference for the user using the UID
        DocumentReference rejectedUserDocument = rejectedUsersCollection.document(uid);
        rejectedUserDocument.delete();

    }

    // Get admin to sign back in after every account creation (handling error)
    private void signAdmin() {
        String adminEmail = "admin_docappoint@gmail.com";
        String adminPassword = "Admin1@docappoint";

        // Add error handling
        mAuth.signInWithEmailAndPassword(adminEmail, adminPassword);

    }

    // Delete current firebaseAuth account (from RejectedUsers information)
    private void deleteFirebaseAccount(String uid, OnDeleteAccountCompletedListener listener) {
        Log.d("uid debug", "Attempting to delete account for uid: " + uid);

        // Use provided uid to get user reference from RejectedUsers collection
        DocumentReference userRef = fStore.collection("RejectedUsers").document(uid);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        // Get email associated with the uid
                        String email = documentSnapshot.getString("Email");
                        String password = documentSnapshot.getString("Password");

                        // TESTS
                        Log.d("EMAIL DEBUGGING", "THIS IS THE EMAIL " + email);
                        Log.d("PASSWORD DEBUGGING", "THIS IS THE PASSWORD " + password);

                        // Sign out the current admin
                        mAuth.signOut();

                        // Sign in as user with the provided email and password
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> {
                                    FirebaseUser user = authResult.getUser();

                                    // Delete user account from  FirebaseAuth
                                    user.delete()
                                            .addOnSuccessListener(aVoid -> {

                                                // Sign back in as the admin
                                                signAdmin();

                                                listener.onDeleteAccountCompleted(); // Notify the listener
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Account Deletion", "Account Deletion failure", e);
                                            });
                                })
                                .addOnFailureListener(e -> {

                                    Log.e("Sign in Failure", "Sign In failure", e);
                                });

                    }
                });
    }

    // Interface to allow the deleteFirebaseAccount method to finish first before starting other methods
    public interface OnDeleteAccountCompletedListener {
        void onDeleteAccountCompleted();
    }
}


