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
import androidx.appcompat.app.AppCompatActivity;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Spinner apptStartTime;
    private String selectedStartTime;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private ArrayAdapter<CharSequence> timeAdapter;
    private List<CharSequence> availableTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_book_appointment);

        initializeViews();
        initializeFirebase();
        initializeSpinner();
        setupListeners();

        String doctorUID = getIntent().getStringExtra("uid");
        if (doctorUID != null) {
            findUnavailableTimes(doctorUID, selectedDate);
        } else {
            Log.e("BookAppointment", "Doctor UID is null.");
        }

    }

    private void initializeViews() {
        patientCalendarView = findViewById(R.id.patientAppointmentDate);
        bookAppointmentEndTime = findViewById(R.id.patientAppointmentEndTime);
        confirmButton = findViewById(R.id.confirmPatientAppointment);
        bookAppointmentBackBtn = findViewById(R.id.bookAppointmentBackButton);
        apptStartTime = findViewById(R.id.patientAppointmentStartTime);

        selectedDate = Calendar.getInstance();


    }

    private void initializeFirebase() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
    }

    private void initializeSpinner() {
        availableTimes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.patientTimes)));
        timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, availableTimes);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apptStartTime.setAdapter(timeAdapter);
        apptStartTime.setOnItemSelectedListener(this);
    }

    private void setupListeners() {
        patientCalendarView.setMinDate(System.currentTimeMillis());
        patientCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
            String doctorUID = getIntent().getStringExtra("uid");
            if (doctorUID != null) {
                findUnavailableTimes(doctorUID, selectedDate);
            } else {
                Log.e("BookAppointment", "Doctor UID is null.");
            }
        });

        bookAppointmentBackBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SelectDoctor.class));
            finish();
        });

        confirmButton.setOnClickListener(v -> {
            if (isDateTimeInPast(selectedDate, selectedStartTime)) {
                Toast.makeText(BookAppointment.this, "Cannot select date and time earlier than today", Toast.LENGTH_SHORT).show();
                return;
            }

            String doctorUID = getIntent().getStringExtra("uid");
            if (doctorUID != null) {
                saveAppointment(doctorUID);
            } else {
                Log.e("BookAppointment", "Doctor UID is null.");
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedStartTime = parent.getItemAtPosition(position).toString();
        Log.d("Selected Start Time:", selectedStartTime);
        updateEndTime();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void updateEndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        try {
            Date startTime = dateFormat.parse(selectedStartTime);
            if (startTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTime);
                calendar.add(Calendar.MINUTE, 30);
                bookAppointmentEndTime.setText(dateFormat.format(calendar.getTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            bookAppointmentEndTime.setText("");
        }
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

    private void findUnavailableTimes(String doctorUID, final Calendar selectedDate) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection("Users").document(doctorUID);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        final String selectedDateString = dateFormat.format(selectedDate.getTime());

        final List<CharSequence> defaultTimes = generateDefaultTimes();

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> appointments = (List<Map<String, Object>>) documentSnapshot.get("Appointments");
                if (appointments != null) {
                    for (Map<String, Object> appointmentMap : appointments) {
                        String appointmentDate = (String) appointmentMap.get("appointmentDate");
                        if (selectedDateString.equals(appointmentDate)) {
                            String appointmentTime = (String) appointmentMap.get("appointmentTime");
                            defaultTimes.remove(appointmentTime);
                        }
                    }
                }
            }

            runOnUiThread(() -> {
                timeAdapter.clear();
                timeAdapter.addAll(defaultTimes);
                timeAdapter.notifyDataSetChanged();

                if (!defaultTimes.isEmpty()) {
                    apptStartTime.setSelection(0);
                    selectedStartTime = defaultTimes.get(0).toString();
                    updateEndTime();
                }
            });
        }).addOnFailureListener(e -> {
            Log.e("BookAppointment", "Error getting document.", e);
            runOnUiThread(() -> {
                timeAdapter.clear();
                timeAdapter.addAll(defaultTimes);
                timeAdapter.notifyDataSetChanged();
            });
        });
    }

    private void saveAppointment(final String doctorUID) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        final String formattedDate = simpleDateFormat.format(selectedDate.getTime());
        userId = fAuth.getCurrentUser().getUid();

        DocumentReference doctorRef = fStore.collection("Users").document(doctorUID);
        doctorRef.get().addOnSuccessListener(doctorSnapshot -> {
            boolean autoAccept = doctorSnapshot.exists() && Boolean.TRUE.equals(doctorSnapshot.getBoolean("autoAccept"));

            final Appointment appointment = new Appointment(formattedDate, selectedStartTime, 0.0f, false, userId, doctorUID, autoAccept, false);

            updateAppointmentInFirestore(appointment, doctorUID);
        }).addOnFailureListener(e -> Log.e("BookAppointment", "Error fetching doctor's autoAccept setting", e));
    }

    private void updateAppointmentInFirestore(Appointment appointment, String doctorUID) {
        DocumentReference doctorRef = fStore.collection("Users").document(doctorUID);
        doctorRef.update("Appointments", FieldValue.arrayUnion(appointment))
                .addOnSuccessListener(aVoid -> {
                    DocumentReference patientRef = fStore.collection("Users").document(userId);
                    patientRef.update("Appointments", FieldValue.arrayUnion(appointment))
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(BookAppointment.this, "Success! Appointment Booked!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), PatientNavigation.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Log.w("BookAppointment", "Error adding appointment to patient's document", e));
                })
                .addOnFailureListener(e -> Log.w("BookAppointment", "Error adding appointment to doctor's document", e));
    }


    private List<CharSequence> generateDefaultTimes() {
        List<CharSequence> times = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

        for (int i = 0; i < 17; i++) { // Adjust the number of iterations to match your time range
            times.add(timeFormat.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }

        return times;
    }


}
