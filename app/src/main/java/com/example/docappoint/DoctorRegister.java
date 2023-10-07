package com.example.docappoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

// Import needed classes
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

public class DoctorRegister extends AppCompatActivity {

    // Declare variables to link to xml files

    EditText regDoctorFirstName, regDoctorLastName, regDoctorSpecialties, regDoctorEmployeeNumber, regDoctorAddress, regDoctorPhoneNumber, regDoctorEmail, regDoctorPassword, regDoctorConfirmPassword;
    Button createDoctorAccount, doctorBackToLogin;

    // Add Firebase Integration (using Firebase Auth to query user)
    FirebaseAuth dAuth;

    // Add Firestore database (using Firestore to query user)
    FirebaseFirestore dStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        // Assign variables to direct xml files
        regDoctorFirstName = findViewById(R.id.doctorFirstName);
        regDoctorLastName = findViewById(R.id.doctorLastName);
        regDoctorSpecialties = findViewById(R.id.doctorSpecialties);
        regDoctorEmployeeNumber = findViewById(R.id.doctorEmployeeNumber);
        regDoctorAddress = findViewById(R.id.doctorAddress);
        regDoctorPhoneNumber = findViewById(R.id.doctorPhoneNumber);
        regDoctorEmail = findViewById(R.id.doctorEmail);
        regDoctorPassword = findViewById(R.id.doctorPassword);
        regDoctorConfirmPassword = findViewById(R.id.confirmPasswordDoctor);
        createDoctorAccount = findViewById(R.id.createDoctorAccountButton);
        doctorBackToLogin = findViewById(R.id.doctorBackToLoginButton);

        // Initialize Firebase class
        dAuth = FirebaseAuth.getInstance();
        dStore = FirebaseFirestore.getInstance();


        // Click events
        createDoctorAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the data fields from the user (save data to String variables)

                String doctorFirstName = regDoctorFirstName.getText().toString();
                String doctorLastName = regDoctorLastName.getText().toString();
                String doctorSpecialties = regDoctorSpecialties.getText().toString();
                String doctorEmployeeNumber = regDoctorEmployeeNumber.getText().toString();
                String doctorAddress = regDoctorAddress.getText().toString();
                String doctorPhoneNumber = regDoctorPhoneNumber.getText().toString();
                String doctorEmail = regDoctorEmail.getText().toString();
                String doctorPassword = regDoctorPassword.getText().toString();
                String doctorConfirmPassword = regDoctorConfirmPassword.getText().toString();

                // Validate the data if a field is empty

                if (doctorFirstName.isEmpty()) {
                    regDoctorFirstName.setError("This Field is Required");
                    return;
                }
                if (doctorLastName.isEmpty()) {
                    regDoctorLastName.setError("This Field is Required");
                    return;
                }

                if (doctorSpecialties.isEmpty()) {
                    regDoctorSpecialties.setError("This Field is Required");
                    return;
                }

                if (doctorEmployeeNumber.isEmpty()) {
                    regDoctorEmployeeNumber.setError("This Field is Required");
                    return;
                }

                if (doctorAddress.isEmpty()) {
                    regDoctorAddress.setError("This Field is Required");
                    return;
                }

                if (doctorPhoneNumber.isEmpty()) {
                    regDoctorPhoneNumber.setError("This Field is Required");
                    return;
                }

                if (doctorEmail.isEmpty()) {
                    regDoctorEmail.setError("This Field is Required");
                    return;
                }

                if (doctorPassword.isEmpty()) {
                    regDoctorPassword.setError("This Field is Required");
                    return;
                }

                if (doctorConfirmPassword.isEmpty()) {
                    regDoctorConfirmPassword.setError("This Field is Required");
                    return;
                }

                // Checks if the password and confirmPassword match
                if (!doctorPassword.equals(doctorConfirmPassword)) {
                    regDoctorConfirmPassword.setError("Password Does Not Match");
                    return;
                }


                // Create user when provided the email and password (if authentication is successful call addOnSuccessListener

                dAuth.createUserWithEmailAndPassword(doctorEmail, doctorPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                // Success message if the account is successfully created (using Toast data)
                                Toast.makeText(DoctorRegister.this, "Success! Account Has Been Created!", Toast.LENGTH_SHORT).show();

                                // Get user data that is created
                                FirebaseUser user = dAuth.getCurrentUser();

                                //Save the reference to collection
                                DocumentReference dDoc  = dStore.collection("Users").document(user.getUid());
                                Map<String,Object> userInfo = new HashMap<>();
                                userInfo.put("First Name", regDoctorFirstName.getText().toString());
                                userInfo.put("Last Name", regDoctorLastName.getText().toString());
                                userInfo.put("Specialties", regDoctorSpecialties.getText().toString());
                                userInfo.put("Employee Number", regDoctorEmployeeNumber.getText().toString());
                                userInfo.put("Address", regDoctorAddress.getText().toString());
                                userInfo.put("Phone Number", regDoctorPhoneNumber.getText().toString());
                                userInfo.put("Email", regDoctorEmail.getText().toString());
                                userInfo.put("Password", regDoctorPassword.getText().toString());

                                // Specify the user is a doctor user
                                userInfo.put("isDoctor", 1);

                                // Save to Firestore database
                                dDoc.set(userInfo);

                                // Send the user to the doctor homepage
                                startActivity(new Intent(getApplicationContext(),DoctorHomepage.class));

                                // Remove all the previous activity
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DoctorRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Back to homepage/login screen button
        doctorBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Homepage.class));
                finish();
            }
        });
    }
}

