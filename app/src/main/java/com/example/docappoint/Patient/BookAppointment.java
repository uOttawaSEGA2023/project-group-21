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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CalendarView patientCalendarView;
    private TextView bookAppointmentEndTime;
    private Calendar selectedDate;
    private Button confirmButton, bookAppointmentBackBtn;
    private String selectedStartTime;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId, firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_book_appointment);

        confirmButton = findViewById(R.id.confirmPatientAppointment);
        bookAppointmentBackBtn = findViewById(R.id.bookAppointmentBackButton);
        bookAppointmentEndTime = findViewById(R.id.patientAppointmentEndTime);
        Spinner apptStartTime = findViewById(R.id.patientAppointmentStartTime);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.patientTimes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apptStartTime.setAdapter(adapter);
        apptStartTime.setOnItemSelectedListener(this);

        patientCalendarView = findViewById(R.id.patientAppointmentDate);
        selectedDate = Calendar.getInstance();
        patientCalendarView.setMinDate(System.currentTimeMillis());
        patientCalendarView.setDate(System.currentTimeMillis(), false, true);

        patientCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate.set(year, month, dayOfMonth);


            }
        });

      //Back button
        bookAppointmentBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SelectDoctor.class));
                finish();
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDateTimeInPast(selectedDate, selectedStartTime)) {
                    Toast.makeText(BookAppointment.this, "Cannot select date and time earlier than today", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("NOT WROKING", "NOT WORKING");

                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                userId = fAuth.getCurrentUser().getUid();

                // Get Doctor UID
                Intent intent = getIntent();
                String doctorUID = intent.getStringExtra("uid");
                Log.d("DocUID", "THIS IS DOCTOR UID" + doctorUID);

                checkDoctorAvailability(doctorUID, selectedDate, selectedStartTime, new AvailabilityCallback() {
                    @Override
                    public void onResult(boolean isAvailable) {
                        if (!isAvailable) {
                            Toast.makeText(BookAppointment.this, "Doctor is not available at the selected time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });


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

                            // Get Patient UID
                            fAuth = FirebaseAuth.getInstance();
                            userId = fAuth.getCurrentUser().getUid();

                            Appointment appointment = new Appointment(formattedDate, selectedStartTime,0.0f, false, userId, doctorUID, false, false);

                            //TODO: ADD APPOINTMENT TO DOCTORS APPOINTMENT & PATIENT APPOINTMENT

                            // Save appointment to doctor user
                            DocumentReference doctorRef = fStore.collection("Users").document(doctorUID);
                            doctorRef.update("Appointments", FieldValue.arrayUnion(appointment))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("BookAppointment", "Appointment added to doctor's document.");

                                            // Save appointment to patient user
                                            DocumentReference patientRef = fStore.collection("Users").document(userId);

                                            patientRef.update("Appointments", FieldValue.arrayUnion(appointment))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("BookAppointment", "Appointment added to patient's document.");

                                                            // Display result message and redirect user to nav page
                                                            Toast.makeText(BookAppointment.this, "Success! Appointment Booked!", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(getApplicationContext(), PatientNavigation.class));
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("BookAppointment", "Error adding appointment to patient's document", e);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("BookAppointment", "Error adding appointment to doctor's document", e);
                                        }
                                    });
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        try {
            Date startTime = dateFormat.parse(selectedStartTime);
            if (startTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTime);
                calendar.add(Calendar.MINUTE, 30);
                String endTimeString = dateFormat.format(calendar.getTime());
                bookAppointmentEndTime.setText(endTimeString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            bookAppointmentEndTime.setText("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // If nothing is selected you can set a default value or do nothing
    }

    private boolean isDateTimeInPast(Calendar selectedDate, String selectedStartTime) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        try {
            Date startTime = dateFormat.parse(selectedStartTime);
            Calendar selectedDateTime = (Calendar) selectedDate.clone();
            if (startTime != null) {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, startTime.getHours());
                selectedDateTime.set(Calendar.MINUTE, startTime.getMinutes());
            }
            return selectedDateTime.before(now);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void checkDoctorAvailability(String doctorUID, Calendar selectedDate, String selectedTime, AvailabilityCallback callback) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection("Users").document(doctorUID);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> appointments = (List<Map<String, Object>>) documentSnapshot.get("Appointments");
                if (appointments != null) {
                    for (Map<String, Object> appointmentMap : appointments) {
                        String appointmentDate = (String) appointmentMap.get("appointmentDate");
                        String appointmentTime = (String) appointmentMap.get("appointmentTime");

                        if (appointmentDate.equals(convertCalendarToDate(selectedDate)) && appointmentTime.equals(selectedTime)) {
                            callback.onResult(false); // Doctor is not available
                            return;
                        }
                    }
                    callback.onResult(true); // Doctor is available
                } else {
                    callback.onResult(true); // No appointments found, doctor is available
                }
            } else {
                Log.e("checkDoctorAvailability", "Doctor document does not exist.");
                callback.onResult(false); // Error case
            }
        }).addOnFailureListener(e -> {
            Log.e("checkDoctorAvailability", "Error getting doctor document.", e);
            callback.onResult(false); // Error case
        });
    }

    private String convertCalendarToDate(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public interface AvailabilityCallback {
        void onResult(boolean isAvailable);
    }


}


