package com.example.docappoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

// Import needed classes
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class PatientRegister extends AppCompatActivity {

    // Declare variables to link to xml files

    EditText regPatientFirstName,regPatientLastName,regPatientHealthCardNum,regPatientAddress,regPatientPhoneNumber,regPatientEmail,regPatientPassword,regPatientConfirmPassword;
    Button createPatientAccount, patientBackToLogin;

    // Add Firebase Integration (using Firebase Auth to query user)
    FirebaseAuth pAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        //  Assign variables to direct xml files
        regPatientFirstName = findViewById(R.id.patientFirstName);
        regPatientLastName = findViewById(R.id.patientLastName);
        regPatientHealthCardNum = findViewById(R.id.healthCardNumber);
        regPatientAddress = findViewById(R.id.patientAddress);
        regPatientPhoneNumber = findViewById(R.id.patientPhoneNumber);
        regPatientEmail = findViewById(R.id.patientEmail);
        regPatientPassword = findViewById(R.id.patientPassword);
        regPatientConfirmPassword = findViewById(R.id.confirmPasswordPatient);
        createPatientAccount = findViewById(R.id.createPatientAccountButton);
        patientBackToLogin = findViewById(R.id.patientBackToLoginButton);

        // Initialize Firebase class
        pAuth = FirebaseAuth.getInstance();

        // Click events
        createPatientAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the data fields from the user (save data to String variable)

                String patientFirstName = regPatientFirstName.getText().toString();
                String patientLastName = regPatientLastName.getText().toString();
                String patientHealthCardNum = regPatientHealthCardNum.getText().toString();
                String patientAddress = regPatientAddress.getText().toString();
                String patientPhoneNumber = regPatientPhoneNumber.getText().toString();
                String patientEmail = regPatientEmail.getText().toString();
                String patientPassword = regPatientPassword.getText().toString();
                String patientConfirmPassword = regPatientConfirmPassword.getText().toString();

                // Validate the data if field is empty

                if(patientFirstName.isEmpty()){
                    regPatientFirstName.setError("This Field is Required");
                    return;
                }
                if (patientLastName.isEmpty()) {
                    regPatientLastName.setError("This Field is Required");
                    return;
                }

                if (patientHealthCardNum.isEmpty()) {
                    regPatientHealthCardNum.setError("This Field is Required");
                    return;
                }

                if (patientAddress.isEmpty()) {
                    regPatientAddress.setError("This Field is Required");
                    return;
                }

                if (patientPhoneNumber.isEmpty()) {
                    regPatientPhoneNumber.setError("This Field is Required");
                    return;
                }

                if (patientEmail.isEmpty()) {
                    regPatientEmail.setError("This Field is Required");
                    return;
                }

                if (patientPassword.isEmpty()) {
                    regPatientPassword.setError("This Field is Required");
                    return;
                }

                if (patientConfirmPassword.isEmpty()) {
                    regPatientConfirmPassword.setError("This Field is Required");
                    return;
                }

                // Checks if the password and confirmPassword is the same
                if (!patientPassword.equals(patientConfirmPassword)){
                    regPatientConfirmPassword.setError("Password Do Not Match");
                    return;
                }

                // Success message if account is successfully created (using Toast data)
                Toast.makeText(PatientRegister.this, "Success! Account Has Been Created!", Toast.LENGTH_SHORT).show();

                // Create user when provided the email and password (if authentication is successful call addOnSuccessListener

                pAuth.createUserWithEmailAndPassword(patientEmail, patientPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                // Send the user to the patient homepage
                                startActivity(new Intent(getApplicationContext(), PatientHomepage.class));

                                // Remove all the previous activity
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PatientRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Back to homepage/login screen button
        patientBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Homepage.class));
            }
        });
    }
}