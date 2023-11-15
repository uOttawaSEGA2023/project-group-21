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
import com.example.docappoint.Patient.PatientRejectApproval;
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

public class DoctorRejectApproval extends AppCompatActivity {

    // Declare Firebase variables
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    EditText doctorApprovalFirstNameText, doctorApprovalLastNameText,
            doctorApprovalAddressText, doctorApprovalEmployeeNumberText, doctorApprovalPhoneNumberText, doctorApprovalEmailText;

    TextView doctorApprovalSpecialtiesText;
    Button doctorApprovalApproveRequestBtn, doctorApprovalBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approveonly_doctor_request);

        // Initialize Firebase and Firestore
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Link xml files
        doctorApprovalFirstNameText = findViewById(R.id.doctorApprovalOnlyFirstNameText);
        doctorApprovalLastNameText = findViewById(R.id.doctorApprovalOnlyLastNameText);
        doctorApprovalAddressText = findViewById(R.id.doctorApprovalOnlyAddressText);
        doctorApprovalEmployeeNumberText = findViewById(R.id.doctorApprovalOnlyEmployeeNumberText);
        doctorApprovalPhoneNumberText = findViewById(R.id.doctorApprovalOnlyPhoneNumberText);
        doctorApprovalEmailText = findViewById(R.id.doctorApprovalOnlyEmailText);
        doctorApprovalSpecialtiesText = findViewById(R.id.doctorApprovalOnlySpecialtiesText);


        // Link buttons
        doctorApprovalApproveRequestBtn = findViewById(R.id.doctorApprovalApproveRequestButton);
        doctorApprovalBackBtn = findViewById(R.id.doctorApprovalOnlyBackButton);

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


            // Approve button will copy RejectedUsers collection to Users
            doctorApprovalApproveRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Re-create the firebase auth account but using the Users collection information
                    deleteFirebaseAccount(uid, new OnDoctorDeleteAccountCompletedListener() {
                        @Override
                        public void onDoctorDeleteAccountCompleted() {
                            saveDoctorDataToUsers(firstName, lastName, employeeNumber, address, phoneNumber, email, password,specialties);

                            Log.d("Email Debug", "Email: " + email);
                            Log.d("UID Debug", "UID: " + uid);

                            deleteDoctorDataFromPendingUsers(uid);

                            startActivity(new Intent(getApplicationContext(), AdminNavigation.class));

                            // Sign admin back in after action is made to user request
                            signAdmin();
                            finish();
                        }
                    });
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
                        doctorData.put("UID", doctorUID);

                        doctorDocument.set(doctorData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // Sign admin back in after action is made to user request
                                        signAdmin();

                                        // If successful display success toast message
                                        Toast.makeText(DoctorRejectApproval.this, "User approved!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {

                                        // Sign admin back in after action is made to user request
                                        signAdmin();

                                        // If error occurs display error toast message
                                        Toast.makeText(DoctorRejectApproval.this, "Failed to approve user" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        ;

                    }

                });
    }


    private void deleteDoctorDataFromPendingUsers(String uid) {
        CollectionReference rejectedUsersCollection = fStore.collection("RejectedUsers");

        // Get the document reference for the user using the UID
        DocumentReference rejectedUserDocument = rejectedUsersCollection.document(uid);
        rejectedUserDocument.delete();

    }

    // Get admin to sign back in after every account creation (handling error)
    private void signAdmin() {
        String adminEmail = "admin_docappoint@gmail.com";
        String adminPassword = "Admin1@docappoint";

        mAuth.signInWithEmailAndPassword(adminEmail, adminPassword);
    }

    // Delete current firebaseAuth account (from RejectedUsers information)
    private void deleteFirebaseAccount(String uid, OnDoctorDeleteAccountCompletedListener listener) {
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

                                                listener.onDoctorDeleteAccountCompleted(); // Notify the listener
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
    public interface OnDoctorDeleteAccountCompletedListener {
        void onDoctorDeleteAccountCompleted();
    }

}
