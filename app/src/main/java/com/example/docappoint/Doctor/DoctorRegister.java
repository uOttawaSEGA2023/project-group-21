package com.example.docappoint.Doctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

// Import needed classes
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.example.docappoint.StatusPending;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.Random;


public class DoctorRegister extends AppCompatActivity {

    // Declare variables to link to xml files

    ArrayList<String> selectedSpecialties = new ArrayList<>();

    private static final int SPECIALTIES_REQUEST_CODE = 1;

    EditText regDoctorFirstName, regDoctorLastName, regDoctorEmployeeNumber, regDoctorAddress, regDoctorPhoneNumber, regDoctorEmail, regDoctorPassword, regDoctorConfirmPassword;
    Button createDoctorAccount, doctorBackToLogin, regDoctorSpecialties;

    Button  doctorAutoCreateBtn;

    // Add Firestore database (using Firestore to query user)
    FirebaseFirestore dStore;

    private String profilePicture = " ";

    //Check if phone number is valid using the libphonenumber API
    private boolean validPhoneNumberCheck(String number) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        try {
            Phonenumber.PhoneNumber NumberToCheck = phoneNumberUtil.parse(number, "CA");
            return phoneNumberUtil.isValidNumber(NumberToCheck);
        } catch (NumberParseException e) {
            return false;
        }

    }

    private boolean validAddressCheck(String address) {
        String regex = "^\\d+\\s+[a-zA-Z]+(\\s+[a-zA-Z]+)*(\\s+[a-zA-Z]+(\\s+[a-zA-Z]+)*)?$";
        return address.matches(regex);
    }

    private boolean validEmployeeNumCheck(String enumber) {
        System.out.println(enumber.length());

        if (enumber.length() != 4) {
            return false;
        }

        try {
            int i = Integer.parseInt(enumber);
        } catch (NumberFormatException nfe) {
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
        setContentView(R.layout.activity_doctor_register);

        // Assign variables to direct xml files
        regDoctorFirstName = findViewById(R.id.doctorFirstName);
        regDoctorLastName = findViewById(R.id.doctorLastName);
        regDoctorSpecialties = findViewById(R.id.doctorSpecialtiesButton);
        regDoctorEmployeeNumber = findViewById(R.id.doctorEmployeeNumber);
        regDoctorAddress = findViewById(R.id.doctorAddress);
        regDoctorPhoneNumber = findViewById(R.id.doctorPhoneNumber);
        regDoctorEmail = findViewById(R.id.doctorEmail);
        regDoctorPassword = findViewById(R.id.doctorPassword);
        regDoctorConfirmPassword = findViewById(R.id.confirmPasswordDoctor);
        createDoctorAccount = findViewById(R.id.createDoctorAccountButton);
        doctorBackToLogin = findViewById(R.id.doctorBackToLoginButton);
        doctorAutoCreateBtn = findViewById(R.id.doctorRegisterAutoCreateButton);

        // Initialize Firebase class
        dStore = FirebaseFirestore.getInstance();

        // Click events
        createDoctorAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the data fields from the user (save data to String variables)
                String doctorFirstName = regDoctorFirstName.getText().toString();
                String doctorLastName = regDoctorLastName.getText().toString();
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

                if (doctorEmployeeNumber.isEmpty()) {
                    regDoctorEmployeeNumber.setError("This Field is Required");
                    return;
                }
                if (!validEmployeeNumCheck(doctorEmployeeNumber)) {
                    regDoctorEmployeeNumber.setError("Invalid Employee Number (4 NUMBERS ONLY)");
                    return;
                }

                if (doctorAddress.isEmpty()) {
                    regDoctorAddress.setError("This Field is Required");
                    return;
                }

                if (!validAddressCheck(doctorAddress)) {
                    regDoctorAddress.setError("Invalid Address");
                    return;
                }

                if (doctorPhoneNumber.isEmpty()) {
                    regDoctorPhoneNumber.setError("This Field is Required");
                    return;
                }

                if (!validPhoneNumberCheck(doctorPhoneNumber)) {
                    regDoctorPhoneNumber.setError("Invalid Phone Number");
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

                if (!validPasswordCheck(doctorPassword)) {
                    regDoctorPassword.setError("Password is not valid (MUST HAVE 1 NUMBER AND 1 CHARACTER WITH 8 OR MORE CHARACTERS");
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

                if (selectedSpecialties.isEmpty()) {
                    Toast.makeText(DoctorRegister.this, "You Must Choose at least one Specialty", Toast.LENGTH_LONG).show();
                    return;
                }

                // Save user data to PendingUsers collection

                //Save the reference to collection
                DocumentReference dDoc = dStore.collection("PendingUsers").document();
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("First Name", regDoctorFirstName.getText().toString());
                userInfo.put("Last Name", regDoctorLastName.getText().toString());
                userInfo.put("Specialties", selectedSpecialties);
                userInfo.put("Employee Number", regDoctorEmployeeNumber.getText().toString());
                userInfo.put("Address", regDoctorAddress.getText().toString());
                userInfo.put("Phone Number", regDoctorPhoneNumber.getText().toString());
                userInfo.put("Email", regDoctorEmail.getText().toString());
                userInfo.put("Password", regDoctorPassword.getText().toString());
                userInfo.put("UID", dDoc.getId());
                userInfo.put("Profile Picture", profilePicture);

                //Adding additional Doctor info
                float rating = 0.0f;
                userInfo.put("Rating", rating);

                int numOfRating = 0;
                userInfo.put("numOfRatings", numOfRating);

                float avgRating = 0.0f;
                userInfo.put("AvgRating", avgRating);

                ArrayList<Appointment> l = new ArrayList<Appointment>();

                userInfo.put("docAppointments", l);

                // Specify the user is a doctor user
                userInfo.put("isDoctor", 1);

                //set the value of rejected to false
                userInfo.put("wasRejected", false);

                //Add autoAccept to false
                userInfo.put("autoAccept", false);

                // Save to Firestore database
                dDoc.set(userInfo)

                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DoctorRegister.this, "Success! Account Has Been Created!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), StatusPending.class));
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


        // Back to select account screen
        doctorBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Go to Specialities page
        regDoctorSpecialties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an intent to open the Specialties class
                Intent specialtiesIntent = new Intent(getApplicationContext(), Specialties.class);

                // Pass the list of specialties to the Specialties class
                specialtiesIntent.putStringArrayListExtra("selectedSpecialties", selectedSpecialties);

                // Start the Specialties screen with an expectation to receive a result (to save specialties array)
                startActivityForResult(specialtiesIntent, SPECIALTIES_REQUEST_CODE);
            }
        });

        doctorAutoCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillWithRandomData();
            }
        });
    }

    // Auto generates random patient data
    private void fillWithRandomData() {
        Random random = new Random();

        regDoctorFirstName.setText("Doctor" + random.nextInt(1000));
        regDoctorLastName.setText("Test" + random.nextInt(1000));
        regDoctorEmployeeNumber.setText("1234");
        regDoctorAddress.setText("93 Switch Grass Cres");
        regDoctorPhoneNumber.setText("6135016672");
        regDoctorEmail.setText("doctortest" + random.nextInt(1000) + "@gmail.com");
        regDoctorPassword.setText("password123");
        regDoctorConfirmPassword.setText("password123");
    }

    // Handle the result (request code) from the Specialties class
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Match the request code and valid condition of Specialties class
        if (requestCode == SPECIALTIES_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                selectedSpecialties = data.getStringArrayListExtra("selectedSpecialties");


                // Save the reference to Users field in the Firestore collection
                DocumentReference dDoc = dStore.collection("PendingUsers").document();

                Map<String, Object> userInfo = new HashMap<>();

                // Add specialties array to Specialties field
                userInfo.put("Specialties", selectedSpecialties);

                // Update the user information
                dDoc.set(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // After saving specialties, delete the document (containing only the specialties)
                                dDoc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        // Handle the error if document deletion fails
                                        Toast.makeText(DoctorRegister.this, "Error deleting document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

            }
        }
    }
}








