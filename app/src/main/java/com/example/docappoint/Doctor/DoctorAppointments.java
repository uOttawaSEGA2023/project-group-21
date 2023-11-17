package com.example.docappoint.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.Appointment;
import com.example.docappoint.Patient.PatientApproval;
import com.example.docappoint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DoctorAppointments extends AppCompatActivity {

    private Button backButton;
    private RecyclerView upcomingAppointments;
    public List<DoctorAppointmentRequest> appointmentRequests = new ArrayList<>();
    private DoctorAppointmentRequestAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    Switch autoAcceptSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appointmentRequests.clear();

        Log.d("IsEmpty", String.valueOf(appointmentRequests.isEmpty()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_upcoming_appointments);

        autoAcceptSwitch = findViewById(R.id.autoAcceptAppointmentSwitch);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean switchState = preferences.getBoolean("switchState", false); // false is the default value
        autoAcceptSwitch.setChecked(switchState);
        autoAcceptSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save state to SharedPreferences
                preferences.edit().putBoolean("switchState", isChecked).apply();
            }
        });

        backButton = findViewById(R.id.doctorUpcomingBackButton);
        upcomingAppointments = findViewById(R.id.viewDoctorUpcomingAppointments);
        upcomingAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorAppointmentRequestAdapter(appointmentRequests);
        upcomingAppointments.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String currentDoctorUID = auth.getCurrentUser().getUid();

        fetchAppointments(currentDoctorUID);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DoctorNavigation.class));
                finish();
            }
        });
    }

    private void fetchAppointments(String doctorUID) {
        db.collection("Users").document(doctorUID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                Object appointmentsObject = document.get("Appointments");
                                if (appointmentsObject instanceof List) {
                                    List<Map<String, Object>> rawAppointments = (List<Map<String, Object>>) appointmentsObject;
                                    processAppointments(rawAppointments);
                                } else {
                                    Log.e("DoctorAppointments", "Appointments field is not a List");
                                }
                            } else {
                                Log.d("DoctorAppointments", "No such document");
                            }
                        } else {
                            Log.d("DoctorAppointments", "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void processAppointments(List<Map<String, Object>> rawAppointments) {
        for (Map<String, Object> rawAppointment : rawAppointments) {
            Appointment appointment = convertMapToAppointment(rawAppointment);

            if (autoAcceptSwitch.isChecked()) {
                appointment.setIsAccepted(true);
                appointment.updateAppointmentField("Doctor", "isAccepted", true);
                appointment.updateAppointmentField("Patient", "isAccepted", true);

            } else {
                appointment.refreshFromFirestore(() -> {
                    Log.d("isAccepted", String.valueOf(appointment.getIsAccepted()));
                    Log.d("isRejected", String.valueOf(appointment.getIsRejected()));
                    if (!appointment.getIsAccepted() && !appointment.getIsRejected()) {
                        DoctorAppointmentRequest.createFromUID(appointment, doctorAppointmentRequest -> {
                            appointmentRequests.add(doctorAppointmentRequest);
                            adapter.notifyDataSetChanged();
                        });
                    }
                });
            }
        }

        // Notify the adapter that the data set has changed after clearing and adding new items
        adapter.notifyDataSetChanged();
    }

    private Appointment convertMapToAppointment(Map<String, Object> rawAppointment) {
        String appointmentDate = (String) rawAppointment.get("appointmentDate");
        String appointmentTime = (String) rawAppointment.get("appointmentTime");
        String patientUID = (String) rawAppointment.get("patientUID");
        String doctorUID = (String) rawAppointment.get("doctorUID");
        return new Appointment(appointmentDate, appointmentTime,0.0f,false, patientUID, doctorUID, false, false);
    }

    public  void removeAppointmentRequest(Appointment toRemove) {
        for (int i = 0; i < appointmentRequests.size(); i++) {
            DoctorAppointmentRequest request = appointmentRequests.get(i);
            Appointment appointment = request.getAppointment(); // Assuming DoctorAppointmentRequest has a method getAppointment()
            if (appointment.equals(toRemove)) {
                appointmentRequests.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
    }

}






