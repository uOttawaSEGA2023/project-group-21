package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
public class Specialties extends AppCompatActivity {
    ArrayList<String> selectedSpecialties = new ArrayList<>();
    Button specialtiesBackBtn, confirmSpecialtiesBtn;

    CheckBox checkBoxFamilyMedicine, checkBoxInternalMedicine, checkBoxPedicatrics,
            checkBoxObstetrics, checkBoxGynecology, checkBoxDermatology, checkBoxPsychiatry, checkBoxNeurology;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialties);

        // Link variables to UI elements

        specialtiesBackBtn = findViewById(R.id.backToDoctorSignUpButton);
        confirmSpecialtiesBtn = findViewById(R.id.confirmSpecialties);

        checkBoxFamilyMedicine = findViewById(R.id.checkFamilyMedicine);
        checkBoxInternalMedicine = findViewById(R.id.checkInternalMedicine);
        checkBoxPedicatrics = findViewById(R.id.checkPediatrics);
        checkBoxObstetrics = findViewById(R.id.checkObstetrics);
        checkBoxGynecology = findViewById(R.id.checkGynecology);
        checkBoxDermatology = findViewById(R.id.checkDermatology);
        checkBoxPsychiatry = findViewById(R.id.checkPsychiatry);
        checkBoxNeurology = findViewById(R.id.checkNeurology);

        // Back to doctor sign up page
        specialtiesBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Message that tells the user to choose at least one specialty when page is first visited
        if (!checkBoxFamilyMedicine.isChecked() &&
                !checkBoxInternalMedicine.isChecked() &&
                !checkBoxPedicatrics.isChecked() &&
                !checkBoxObstetrics.isChecked() &&
                !checkBoxGynecology.isChecked() &&
                !checkBoxDermatology.isChecked() &&
                !checkBoxPsychiatry.isChecked() &&
                !checkBoxNeurology.isChecked()) {

            // Display message when none of the checkboxes are checked
            Toast.makeText(Specialties.this, "Please select at least one specialty", Toast.LENGTH_LONG).show();
        }

        // Set the click listener for confirming specialties
        confirmSpecialtiesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check which checkboxes are selected and add them to the ArrayList
                selectedSpecialties.clear();

                if (checkBoxFamilyMedicine.isChecked()) {
                    selectedSpecialties.add("Family Medicine");
                }
                if (checkBoxInternalMedicine.isChecked()) {
                    selectedSpecialties.add("Internal Medicine");
                }
                if (checkBoxPedicatrics.isChecked()) {
                    selectedSpecialties.add("Pedicatrics");
                }
                if (checkBoxObstetrics.isChecked()) {
                    selectedSpecialties.add("Obstetrics");
                }
                if (checkBoxGynecology.isChecked()) {
                    selectedSpecialties.add("Gynecology");
                }
                if (checkBoxDermatology.isChecked()) {
                    selectedSpecialties.add("Dermatology");
                }
                if (checkBoxPsychiatry.isChecked()) {
                    selectedSpecialties.add("Psychiatry");
                }
                if (checkBoxNeurology.isChecked()) {
                    selectedSpecialties.add("Neurology");
                }

                // Check if at least one specialty is selected
                if (selectedSpecialties.isEmpty()) {

                    // Display message if none of the checkboxes are checked (and dont redirect to register page)
                    Toast.makeText(Specialties.this, "Please select at least one specialty", Toast.LENGTH_LONG).show();

                } else {
                    // When specialties are selected, show a "Specialties saved" toast
                    Toast.makeText(Specialties.this, "Specialties saved!", Toast.LENGTH_SHORT).show();

                    // Create an Intent to pass data back to the DoctorRegister class
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("selectedSpecialties", selectedSpecialties);

                    // Set a result code and send the intent back to the DoctorRegister class
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
