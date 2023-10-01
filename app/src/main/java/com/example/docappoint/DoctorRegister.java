package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

// Import needed classes
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class DoctorRegister extends AppCompatActivity {

    // Declare variables to link to xml files

    EditText regDoctorFirstName, regDoctorLastName, regDoctorSpecialties, regDoctorAddress, regDoctorPhoneNumber, regDoctorEmail, regDoctorPassword, regDoctorConfirmPassword;
    Button createDoctorAccount, doctorBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        // Assign variables to direct xml files
        regDoctorFirstName = findViewById(R.id.doctorFirstName);
        regDoctorLastName = findViewById(R.id.doctorLastName);
        regDoctorSpecialties = findViewById(R.id.doctorSpecialties);
        regDoctorAddress = findViewById(R.id.doctorAddress);
        regDoctorPhoneNumber = findViewById(R.id.doctorPhoneNumber);
        regDoctorEmail = findViewById(R.id.doctorEmail);
        regDoctorPassword = findViewById(R.id.doctorPassword);
        regDoctorConfirmPassword = findViewById(R.id.confirmPasswordDoctor);
        createDoctorAccount = findViewById(R.id.createDoctorAccountButton);
        doctorBackToLogin = findViewById(R.id.doctorBackToLoginButton);


        // Click events
        createDoctorAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the data fields from the user (save data to String variables)

                String doctorFirstName = regDoctorFirstName.getText().toString();
                String doctorLastName = regDoctorLastName.getText().toString();
                String doctorSpecialties = regDoctorSpecialties.getText().toString();
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

                // Success message if the account is successfully created (using Toast data)
                Toast.makeText(DoctorRegister.this, "Success! Account Has Been Created!", Toast.LENGTH_SHORT).show();
            }
        });

        // Back to homepage/login screen button
        doctorBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Homepage.class));
            }
        });
    }
}
