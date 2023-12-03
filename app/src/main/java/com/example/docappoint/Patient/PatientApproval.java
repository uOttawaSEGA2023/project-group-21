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
import com.example.docappoint.Appointment;
import com.example.docappoint.Doctor.DoctorAppointments;
import com.example.docappoint.Doctor.DoctorNavigation;
import com.example.docappoint.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientApproval extends AppCompatActivity {

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

        // Buttons
        patientApprovalBackBtn = findViewById(R.id.patientApprovalBackButton);
        patientApprovalApproveRequestbtn = findViewById(R.id.patientApprovalApproveRequestButton);
        patientApprovalDenyRequestBtn = findViewById(R.id.patientApprovalDenyRequestButton);


        // Retrieve user data from the intent extras
        Intent intent = getIntent();
        if (intent != null && !intent.getBooleanExtra("isAppointRequest", false)) {
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");
            String healthCardNumber = intent.getStringExtra("healthCardNumber");
            String address = intent.getStringExtra("address");
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");
            String uid = intent.getStringExtra("uid");
            boolean rejected = intent.getBooleanExtra("rejected", false);



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
                    savePatientDataToUsers(firstName, lastName, healthCardNumber, address, phoneNumber, email, password);

                    // ADD DELETE PENDINGUSERS COLLECTION FUNCTIONALITY HERE AND DELETE CHIP FUNCTIONALITY HERE
                    Log.d("Email Debug", "Email: " + email);
                    Log.d("UID Debug", "UID: " + uid);

                    deletePatientDataFromPendingUsers(uid);

                    // Sign admin back in after action is made to user request
                    signAdmin();

                    startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                    finish();
                }
            });

            //Deny button will navigate to the history page
            patientApprovalDenyRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    savePatientDataToRejectedUsers(firstName, lastName, healthCardNumber, address, phoneNumber, email, password, rejected);

                    CollectionReference pendingUsersCollection = fStore.collection("PendingUsers");

                    // Get the document reference for the user using the UID
                    DocumentReference pendingUserDocument = pendingUsersCollection.document(uid);


                    pendingUserDocument
                            .update("wasRejected", true);

                    deletePatientDataFromPendingUsers(uid);

                    // Sign admin back in after action is made to user request
                    signAdmin();

                    startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                    finish();

                }
            });

            // Button functionality to navigate back
            patientApprovalBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                    finish();
                }
            });

        }
        else if(intent != null && intent.getBooleanExtra("isAppointRequest", false)){
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

            // Approve button will copy PendingUsers collection to Users
            patientApprovalApproveRequestbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Appointment currAppointment = (Appointment) getIntent().getSerializableExtra("appointment");

                    currAppointment.setIsAccepted(true);
                    currAppointment.updateAppointmentField("Doctor", "isAccepted", true);
                    currAppointment.updateAppointmentField("Patient", "isAccepted", true);

                    startActivity(new Intent(getApplicationContext(), DoctorAppointments.class));
                    finish();

                }
            });

            //Deny button will navigate to the history page
            patientApprovalDenyRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Appointment currAppointment = (Appointment) getIntent().getSerializableExtra("appointment");


                    currAppointment.setIsRejected(true);
                    currAppointment.updateAppointmentField("Doctor", "isRejected", true);
                    currAppointment.updateAppointmentField("Patient", "isRejected", true);

                    startActivity(new Intent(getApplicationContext(), DoctorAppointments.class));
                    finish();
                }
            });

            // Button functionality to navigate back
            patientApprovalBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), DoctorAppointments.class));
                    finish();
                }
            });


        }



    }

    // Using the PendingUsers information, save it on a new colection called Users in firebase
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

                        patientDocument.set(patientData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // Sign admin back in after action is made to user request
                                        signAdmin();

                                        // If successful display success toast message
                                        Toast.makeText(PatientApproval.this, "User approved!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {

                                        // Sign admin back in after action is made to user request
                                        signAdmin();

                                        // If error occurs display error toast message
                                        Toast.makeText(PatientApproval.this, "Failed to approve user" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                });

    }

    private void savePatientDataToRejectedUsers(String firstName, String lastName, String healthCardNumber, String address, String phoneNumber, String email, String password, boolean rejected) {

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
                        DocumentReference patientDocument = fStore.collection("RejectedUsers").document(patientUID);


                        Map<String, Object> patientData = new HashMap<>();
                        patientData.put("First Name", firstName);
                        patientData.put("Last Name", lastName);
                        patientData.put("Health Card Number", healthCardNumber);
                        patientData.put("Address", address);
                        patientData.put("Phone Number", phoneNumber);
                        patientData.put("Email", email);
                        patientData.put("Password", password);
                        patientData.put("isPatient", 1);
                        patientData.put("isApproved", false);
                        patientData.put("UID", patientUID);
                        patientData.put("wasRejected", true);


                        patientDocument.set(patientData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // Sign admin back in after action is made to user request
                                        signAdmin();

                                        // If successful display success toast message
                                        Toast.makeText(PatientApproval.this, "User denied!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {

                                        // Sign admin back in after action is made to user request
                                        signAdmin();

                                        // If error occurs display error toast message
                                        Toast.makeText(PatientApproval.this, "Failed to deny user" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                });
    }

    private void deletePatientDataFromPendingUsers(String uid) {
        CollectionReference pendingUsersCollection = fStore.collection("PendingUsers");

        // Get the document reference for the user using the UID
        DocumentReference pendingUserDocument = pendingUsersCollection.document(uid);
        pendingUserDocument.delete();

    }

    // Get admin to sign back in after every account creation (handling error)
    private void signAdmin() {
            String adminEmail = "admin_docappoint@gmail.com";
            String adminPassword = "Admin1@docappoint";

        mAuth.signInWithEmailAndPassword(adminEmail, adminPassword);
    }

}


