package com.example.docappoint.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CalendarView patientCalendarView;
    private Calendar selectedDate;
    private Button confirmButton;
    private String selectedStartTime;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId, firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_book_appointment);

        confirmButton = findViewById(R.id.confirmPatientAppointment);
        Spinner apptStartTime = findViewById(R.id.patientAppointmentStartTime);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.patientTimes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apptStartTime.setAdapter(adapter);
        apptStartTime.setOnItemSelectedListener(this);

        patientCalendarView = findViewById(R.id.patientAppointmentDate);
        selectedDate = Calendar.getInstance();
        patientCalendarView.setDate(System.currentTimeMillis(), false, true);

        patientCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate.set(year, month, dayOfMonth);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NOT WROKING", "NOT WORKING");
                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                userId = fAuth.getCurrentUser().getUid();

                DocumentReference documentReference = fStore.collection("Users").document(userId);
                documentReference.addSnapshotListener(BookAppointment.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("BookAppointment", "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Log.d("BookAppointment", "Current data: " + documentSnapshot.getData());
                            firstName = documentSnapshot.getString("First Name");
                            lastName = documentSnapshot.getString("Last Name");

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                            String formattedDate = simpleDateFormat.format(selectedDate.getTime());

                            Intent intent = new Intent();
                            String doctorUID = intent.getStringExtra("uid");
                            Log.d("DocUID", doctorUID);

                            Appointment appointment = new Appointment(formattedDate, selectedStartTime, userId, doctorUID);

                            //TODO: ADD APPOINTMENT TO DOCTORS APPOINTMENT & PATIENT APPOINTMENT

                        } else {
                            Log.d("BookAppointment", "Current data: null");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedStartTime = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // If nothing is selected you can set a default value or do nothing
    }
}


