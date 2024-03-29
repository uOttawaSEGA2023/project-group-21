package com.example.docappoint.Doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorApproval extends AppCompatActivity {

    // Declare Firebase variables
    FirebaseFirestore fStore;

    FirebaseAuth mAuth;

    EditText doctorApprovalFirstNameText, doctorApprovalLastNameText,
            doctorApprovalAddressText, doctorApprovalEmployeeNumberText, doctorApprovalPhoneNumberText, doctorApprovalEmailText;
    TextView doctorApprovalSpecialtiesText;
    Button doctorApprovalApproveRequestBtn, doctorApprovalDenyRequestBtn, doctorApprovalBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_doctor_request);


        // Initialize Firebase and Firestore
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Link xml files
        doctorApprovalFirstNameText = findViewById(R.id.doctorApprovalFirstNameText);
        doctorApprovalLastNameText = findViewById(R.id.doctorApprovalLastNameText);
        doctorApprovalAddressText = findViewById(R.id.doctorApprovalAddressText);
        doctorApprovalEmployeeNumberText = findViewById(R.id.doctorApprovalEmployeeNumberText);
        doctorApprovalPhoneNumberText = findViewById(R.id.doctorApprovalPhoneNumberText);
        doctorApprovalEmailText = findViewById(R.id.doctorApprovalEmailText);
        doctorApprovalSpecialtiesText = findViewById(R.id.doctorApprovalSpecialtiesText);

        // Link buttons
        doctorApprovalApproveRequestBtn = findViewById(R.id.doctorApprovalApproveRequestButton);
        doctorApprovalDenyRequestBtn = findViewById(R.id.doctorApprovalDenyRequestButton);
        doctorApprovalBackBtn = findViewById(R.id.doctorApprovalBackButton);

        FirebaseAuth mAuth;

        // Retrieve user data from the intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");
            String address = intent.getStringExtra("address");
            String employeeNumber = intent.getStringExtra("employeeNumber");
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");
            String uid = intent.getStringExtra("uid");
            boolean rejected = intent.getBooleanExtra("rejected", false);
            ArrayList<String> specialties = intent.getStringArrayListExtra("specialties");

            // Update the EditText fields with the retrieved data
            doctorApprovalFirstNameText.setText(firstName);
            doctorApprovalLastNameText.setText(lastName);
            doctorApprovalAddressText.setText(address);
            doctorApprovalEmployeeNumberText.setText(employeeNumber);
            doctorApprovalPhoneNumberText.setText(phoneNumber);
            doctorApprovalEmailText.setText(email);
            if (specialties != null) {
                doctorApprovalSpecialtiesText.setText(TextUtils.join(", ", specialties));
            }

            // Approve button will copy PendingUsers collection to Users
            doctorApprovalApproveRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDoctorDataToUsers(firstName, lastName, employeeNumber, address, phoneNumber, email, password, specialties);

                    // ADD DELETE PENDINGUSERS COLLECTION FUNCTIONALITY HERE AND DELETE CHIP FUNCTIONALITY HERE
                    Log.d("Email Debug", "Email: " + email);
                    Log.d("UID Debug", "UID: " + uid);

                    deleteDoctorDataFromPendingUsers(uid);

                    // Sign admin back in after action is made to user request
                    signAdmin();

                    startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                    finish();
                }
            });

            //Deny button will navigate to the history page
            doctorApprovalDenyRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDoctorDataToRejectedUsers(firstName, lastName, employeeNumber, address, phoneNumber, email, password, specialties, rejected);

                    CollectionReference pendingUsersCollection = fStore.collection("PendingUsers");

                    // Get the document reference for the user using the UID
                    DocumentReference pendingUserDocument = pendingUsersCollection.document(uid);

                    pendingUserDocument
                            .update("wasRejected", true);

                    deleteDoctorDataFromPendingUsers(uid);

                    // Sign admin back in after action is made to user request
                    signAdmin();

                    startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                    finish();

                }
            });
        }

        // Back button
        doctorApprovalBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                finish();

            }
        });

    }

    // Using the PendingUsers information, save it on a new collection called Users in firebase
    private void saveDoctorDataToUsers(String firstName, String lastName, String employeeNumber, String address, String phoneNumber, String email, String password, ArrayList<String> specialties) {

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
                        String doctorUID = user.getUid();

                        // Save the reference to the "Users" collection
                        DocumentReference doctorDocument = fStore.collection("Users").document(doctorUID);

                        Map<String, Object> doctorData = new HashMap<>();

                        doctorData.put("First Name", firstName);
                        doctorData.put("Last Name", lastName);
                        doctorData.put("Employee Number", employeeNumber);
                        doctorData.put("Address", address);
                        doctorData.put("Phone Number", phoneNumber);
                        doctorData.put("Email", email);
                        doctorData.put("Password", password);
                        doctorData.put("isDoctor", 1);
                        doctorData.put("isApproved", true);
                        doctorData.put("Specialties", specialties);
                        doctorData.put("UID",doctorUID);


                        doctorDocument.set(doctorData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        signAdmin();

                                        // If successful display success toast message
                                        Toast.makeText(DoctorApproval.this, "User approved!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {

                                        signAdmin();

                                        // If error occurs display error toast message
                                        Toast.makeText(DoctorApproval.this, "Failed to approve user" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                });
    }

    private void saveDoctorDataToRejectedUsers(String firstName, String lastName, String
            employeeNumber, String address, String phoneNumber, String email, String password, ArrayList<String> specialties, boolean rejected) {


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
                        String doctorUID = user.getUid();

                        // Save the reference to the "Users" collection
                        DocumentReference doctorDocument = fStore.collection("RejectedUsers").document(doctorUID);


                        Map<String, Object> doctorData = new HashMap<>();
                        doctorData.put("First Name", firstName);
                        doctorData.put("Last Name", lastName);
                        doctorData.put("Employee Number", employeeNumber);
                        doctorData.put("Address", address);
                        doctorData.put("Phone Number", phoneNumber);
                        doctorData.put("Email", email);
                        doctorData.put("Password", password);
                        doctorData.put("isDoctor", 1);
                        doctorData.put("isApproved", false);
                        doctorData.put("Specialties", specialties);
                        doctorData.put("wasRejected", true);
                        doctorData.put("UID",doctorUID);

                        doctorDocument.set(doctorData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        signAdmin();

                                        // If successful display success toast message
                                        Toast.makeText(DoctorApproval.this, "User denied!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {

                                        signAdmin();

                                        // If error occurs display error toast message
                                        Toast.makeText(DoctorApproval.this, "Failed to deny user" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
    }


    private void deleteDoctorDataFromPendingUsers(String uid) {
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
