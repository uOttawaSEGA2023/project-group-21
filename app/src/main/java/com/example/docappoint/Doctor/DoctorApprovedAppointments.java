package com.example.docappoint.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.docappoint.Appointment;
import com.example.docappoint.Patient.PatientAppointmentAdapter;
import com.example.docappoint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorApprovedAppointments extends AppCompatActivity {

    // Declare variables
    public List<Appointment> dAppointmentList = new ArrayList<>();
    RecyclerView dAppointmentRecyclerView;
    FirebaseUser currentUser;
    Button doctorApprovedAppointmentsBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_pending_appt_requests);

        doctorApprovedAppointmentsBackBtn = findViewById(R.id.doctorApptRequestsBackButton);

        // Recycler view
        dAppointmentRecyclerView = findViewById(R.id.viewDoctorAppointmentsRequests);
        dAppointmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        PatientAppointmentAdapter dAdapter = new PatientAppointmentAdapter(dAppointmentList);
        dAppointmentRecyclerView.setAdapter(dAdapter);

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
                                Log.d("ISACCEPTED CHECK", " THIS: " + isAccepted);

                                if (hasHappened == false && isAccepted) {
                                    Appointment patientAppointment = new Appointment(appointmentDate, appointmentStartTime, appointmentRating, hasHappened, patientUID, doctorUID, isAccepted, isRejected);
                                    retrievedPatientAppointments.add(patientAppointment);
                                }
                            }

                            dAppointmentList.clear();
                            dAppointmentList.addAll(retrievedPatientAppointments);
                            dAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Log.d("ERROR DEBUG 1", "DOCUMENT NOT FOUND");
                    }
                } else {
                    Log.d("ERROR DEBUG 2", "TASK FAILURE");
                }
            });
        }

        // Back button
        doctorApprovedAppointmentsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DoctorNavigation.class));
                finish();
            }
        });
    }


}