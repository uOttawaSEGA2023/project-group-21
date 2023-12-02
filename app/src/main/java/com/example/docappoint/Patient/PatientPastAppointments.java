package com.example.docappoint.Patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatientPastAppointments extends AppCompatActivity {

    // Declare variables
    RecyclerView pAppointmentRecyclerView;
    List<Appointment> pAppointmentList = new ArrayList<>();
    FirebaseUser currentUser;
    Button patientPastAppointmentBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment_history);

        // Link xml files to variable
        patientPastAppointmentBackBtn = findViewById(R.id.patientPastAppointmentsBackButton);

        // Recycler view
        pAppointmentRecyclerView = findViewById(R.id.viewPatientPastAppointments);
        pAppointmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        PatientAppointmentAdapter pAdapter = new PatientAppointmentAdapter(pAppointmentList);
        pAppointmentRecyclerView.setAdapter(pAdapter);

        // Get current user to access "Users" collection
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = fStore.collection("Users").document(userId);

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        List<Appointment> retrievedPatientAppointments = new ArrayList<>();
                        List<HashMap<String, Object>> appointmentsData = (List<HashMap<String, Object>>) document.get("Appointments");

                        if (appointmentsData != null) {
                            for (HashMap<String, Object> appointmentData : appointmentsData) {
                                String appointmentDate = (String) appointmentData.get("appointmentDate");
                                String appointmentStartTime = (String) appointmentData.get("appointmentTime");
                                float appointmentRating = 0;
                                String patientUID = (String) appointmentData.get("patientUID");
                                String doctorUID = (String) appointmentData.get("doctorID");
                                Boolean hasHappened = (Boolean) appointmentData.get("hasHappened");
                                Boolean isAccepted = (Boolean) appointmentData.get("isAccepted");
                                Boolean isRejected = (Boolean) appointmentData.get("isRejected");


                                if (hasHappened!= null && hasHappened) {
                                    Appointment patientAppointment = new Appointment(appointmentDate, appointmentStartTime, appointmentRating, hasHappened, patientUID, doctorUID, isAccepted, isRejected);
                                    retrievedPatientAppointments.add(patientAppointment);
                                }
                            }

                            pAppointmentList.clear();
                            pAppointmentList.addAll(retrievedPatientAppointments);
                            pAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Log.d("ERROR DEBUG 1", "DOCUMENT NOT FOUND");
                    }
                } else {
                    Log.d("ERROR DEBUG 2", "TASK FAILURE");
                }
            });
        }

        // Back Button
        patientPastAppointmentBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientNavigation.class));
            }
        });
    }



}