package com.example.docappoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Homepage extends AppCompatActivity {

    Button loginAppButton, createAccountButton;

    EditText loginAppEmail, loginAppPassword;

    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Initialize with instance
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Connect UI elements to backend
        loginAppButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        loginAppEmail = findViewById(R.id.loginEmail);
        loginAppPassword = findViewById(R.id.loginPassword);


        // Handle click and conditions for user login
        loginAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(loginAppEmail);
                checkField((loginAppPassword));


                if (valid) {

                    fAuth.signInWithEmailAndPassword(loginAppEmail.getText().toString(), loginAppPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            String uid = authResult.getUser().getUid();

                            DocumentReference userRef = fStore.collection("Users").document(uid);
                            DocumentReference rejectedUserRef = fStore.collection("RejectedUsers").document(uid);

                            // Check if the user is approved in the Users collection and log them in
                            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        Boolean isApproved = userSnapshot.getBoolean("isApproved");

                                        if (isApproved) {
                                            Toast.makeText(Homepage.this, "You are now logged in!", Toast.LENGTH_SHORT).show();
                                            checkAccountLevel(uid);

                                        }
                                    }
                                }
                            });

                            // Check if the user is approved in the RejectedUsers collection
                            rejectedUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot rejectedUserSnapshot) {
                                    if (rejectedUserSnapshot.exists()) {
                                        Boolean isApproved = rejectedUserSnapshot.getBoolean("isApproved");

                                        if (isApproved != null && !isApproved) {
                                            // User is explicitly rejected in the RejectedUsers collection
                                            Toast.makeText(Homepage.this, "Account has been rejected. Contact administrator (613-562-5700)", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Homepage.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        //Handle click for account creation
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SelectAccount.class));
            }
        });

    }


    // Check if the login fields are empty or not
    public boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("Field is required");
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }

    private void checkAccountLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        // Extract data from the Users document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                // Contains data of the particular document
                Log.d("TAG", "onSuccess: " + documentSnapshot.getData());

                // Identify the account type
                if (documentSnapshot.getLong("isAdmin") != null) {

                    startActivity(new Intent(getApplicationContext(), AdminHomepage.class));
                    finish();
                }
                if (documentSnapshot.getLong("isPatient") != null) {
                    startActivity(new Intent(getApplicationContext(), PatientHomepage.class));
                    finish();
                }

                if (documentSnapshot.getLong("isDoctor") != null) {
                    startActivity(new Intent(getApplicationContext(), DoctorHomepage.class));
                    finish();
                }

            }
        });
    }

    // If the user is logged in already, redirect to the account's correct homepage
    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // Check the user's role and redirect accordingly
            checkUserRoleAndRedirect();

        } else {
            //TESTING
            Toast.makeText(Homepage.this, "All registrations must be approved by admin", Toast.LENGTH_LONG).show();
        }
    }


    // Method to check the user account type to redirect to correct homepage (for auto-log in)
    private void checkUserRoleAndRedirect() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference df = fStore.collection("Users").document(uid);

        // Fetch from database

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.getLong("isAdmin") != null) {
                        startActivity(new Intent(getApplicationContext(), AdminHomepage.class));
                    } else if (documentSnapshot.getLong("isPatient") != null) {
                        startActivity(new Intent(getApplicationContext(), PatientHomepage.class));
                    } else if (documentSnapshot.getLong("isDoctor") != null) {
                        startActivity(new Intent(getApplicationContext(), DoctorHomepage.class));
                    } else {
                        // Error message if the account is not defined (using Toast data)
                        Toast.makeText(Homepage.this, "Error! No user data found", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }
        });
    }
}
