package com.example.docappoint.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.docappoint.R;
import com.example.docappoint.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorNavigation extends AppCompatActivity {

    Button doctorSettingsBtn, doctorViewAppointmentHistoryBtn, doctorAddShiftButton, doctorViewAppointmentRequestsBtn;
    TextView doctorViewAllTextView, doctorNameTextView;
    Button viewShiftBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_navigation);

        doctorSettingsBtn = findViewById(R.id.doctorSettingButton);
        doctorViewAppointmentHistoryBtn = findViewById(R.id.doctorViewAppointmentHistoryButton);
        doctorViewAppointmentRequestsBtn = findViewById(R.id.doctorViewAppointmentRequestsButton);
        doctorAddShiftButton = findViewById(R.id.doctorAddShiftButton);
        doctorViewAllTextView = findViewById(R.id.clickableViewAllNextAppt);
        doctorNameTextView = findViewById(R.id.doctorNameTextView);


        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        String userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String firstName = document.getString("First Name");
                        doctorNameTextView.setText(firstName);
                        Log.d("Firestore", "First Name: " + firstName);
                        // Do something with the first name
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });

        doctorViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DoctorAppointments.class));
                finish();
            }
        });
      
        viewShiftBtn = findViewById(R.id.doctorViewHistoryBtn);


        // Redirect to settings screen when clicked
        doctorSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Settings.class));
                finish();
            }
        });

        doctorViewAppointmentHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DoctorApptHistory.class));
                finish();
            }
        });

        viewShiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ShiftHistory.class));
                finish();
            }
        });


        doctorViewAppointmentRequestsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), DoctorAppointments.class));
            }

        });

        doctorAddShiftButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), SetShift.class));
            }

        });

    }


}