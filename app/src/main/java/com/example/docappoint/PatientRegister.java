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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.HashMap;
import java.util.Map;

public class PatientRegister extends AppCompatActivity {

    // Declare variables to link to xml files

    EditText regPatientFirstName,regPatientLastName,regPatientHealthCardNum,regPatientAddress,regPatientPhoneNumber,regPatientEmail,regPatientPassword,regPatientConfirmPassword;
    Button createPatientAccount, patientBackToLogin;

    // Add Firebase Integration (using Firebase Auth to query user)
    FirebaseAuth pAuth;
    // Add Firestore database (using Firestore to query user)
    FirebaseFirestore pStore;

    private boolean validPhoneNumberCheck(String number) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        try{
            Phonenumber.PhoneNumber NumberToCheck = phoneNumberUtil.parse(number, "CA");
            return phoneNumberUtil.isValidNumber(NumberToCheck);
        }
        catch (NumberParseException e){
            return false;
        }

    }
    private boolean validAddressCheck(String address) {
        String regex = "^\\d+\\s+[a-zA-Z]+(\\s+[a-zA-Z]+)*(\\s+[a-zA-Z]+(\\s+[a-zA-Z]+)*)?$";
        return address.matches(regex);
    }

    private boolean validHealthCardCheck(String enumber){
        if (enumber.length() != 10){
            return false;
        }

        try {
            int i  = Integer.parseInt(enumber);
        }
        catch (NumberFormatException nfe){
            return false;
        }

        return true;
    }

    private boolean validPasswordCheck(String password) {
        if (password.length() < 8) {
            return false;
        }

        String checkDig = ".*\\d.*";
        String checkLetter = ".*[a-zA-Z].*";

        return password.matches(checkDig) && password.matches(checkLetter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        //  Assign variables to direct xml files
        regPatientFirstName = findViewById(R.id.patientRegisterFirstNameText);
        regPatientLastName = findViewById(R.id.patientRegisterLastNameText);
        regPatientHealthCardNum = findViewById(R.id.patientRegisterHealthCardNumberText);
        regPatientAddress = findViewById(R.id.patientRegisterAddressText);
        regPatientPhoneNumber = findViewById(R.id.patientRegisterPhoneNumberText);
        regPatientEmail = findViewById(R.id.patientRegisterEmailText);
        regPatientPassword = findViewById(R.id.patientPassword);
        regPatientConfirmPassword = findViewById(R.id.confirmPasswordPatient);
        createPatientAccount = findViewById(R.id.createPatientAccountButton);
        patientBackToLogin = findViewById(R.id.patientRegBackButton);

        // Initialize Firebase class
        pAuth = FirebaseAuth.getInstance();
        pStore = FirebaseFirestore.getInstance();

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

                if (!validHealthCardCheck(patientHealthCardNum)){
                    regPatientHealthCardNum.setError("Invalid Health Card Number (Must be 10 numbers)");
                    return;
                }

                if (patientAddress.isEmpty()) {
                    regPatientAddress.setError("This Field is Required");
                    return;
                }

                if (!validAddressCheck(patientAddress)){
                    regPatientAddress.setError("Invalid Address");
                    return;
                }

                if (patientPhoneNumber.isEmpty()) {
                    regPatientPhoneNumber.setError("This Field is Required");
                    return;
                }

                if (!validPhoneNumberCheck(patientPhoneNumber)){
                    regPatientPhoneNumber.setError("Invalid Phone Number");
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

                if (!validPasswordCheck(patientPassword)){
                    regPatientPassword.setError("Password is not valid (MUST HAVE 1 NUMBER AND 1 CHARACTER WITH 8 OR MORE CHARACTERS");
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


                // Create user when provided the email and password (if authentication is successful call addOnSuccessListener

                pAuth.createUserWithEmailAndPassword(patientEmail, patientPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                // Get user data that is created
                                FirebaseUser user = pAuth.getCurrentUser();

                                // Success message if account is successfully created (using Toast data)
                                Toast.makeText(PatientRegister.this, "Success! Account Has Been Created!", Toast.LENGTH_SHORT).show();

                                //Save the reference to collection
                                DocumentReference pDoc  = pStore.collection("Users").document(user.getUid());
                                Map<String,Object> userInfo = new HashMap<>();
                                userInfo.put("First Name",regPatientFirstName.getText().toString());
                                userInfo.put("Last Name",regPatientLastName.getText().toString());
                                userInfo.put("Health Card Number",regPatientHealthCardNum.getText().toString());
                                userInfo.put("Address",regPatientAddress.getText().toString());
                                userInfo.put("Phone Number",regPatientPhoneNumber.getText().toString());
                                userInfo.put("Email",regPatientEmail.getText().toString());
                                userInfo.put("Password",regPatientPassword.getText().toString());

                                // Specify the user is a patient user
                                userInfo.put("isPatient", 1);

                                // Save to Firestore database
                                pDoc.set(userInfo);

                                // Send the user to the patient homepgae
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
                finish();
            }
        });
    }
}