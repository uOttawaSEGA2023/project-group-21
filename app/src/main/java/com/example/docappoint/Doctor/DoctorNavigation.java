package com.example.docappoint.Doctor;

import static com.example.docappoint.Doctor.ShiftAdapter.isTimeBetween;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docappoint.R;
import com.example.docappoint.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DoctorNavigation extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button doctorViewAppointmentHistoryBtn, doctorAddShiftButton, doctorViewAppointmentRequestsBtn;
    TextView doctorViewAllTextView, doctorNameTextView;
    Button viewShiftBtn, viewNextAppointmentBtn, doctorCancelShiftBtn;

    ImageButton doctorSettingsBtn;

    TextView doctorNavigationDateTxt, doctorNavigationStartTimeTxt,doctorNavigationEndTimeTxt, doctorNavigationApptFirstNameTxt,
            doctorNavigationApptLastNameTxt,doctorNavigationApptDateTxt, doctorNavigationApptStartTimeTxt, doctorNavigationApptEndTimeTxt, doctorNavigationApptEndTimeLabel;


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

        // xml variables for the Appointment (next appointment)
        doctorNavigationApptStartTimeTxt = findViewById(R.id.doctorAppointmentCardStartTime);
        doctorNavigationApptEndTimeTxt = findViewById(R.id.doctorAppointmentCardEndTime);
        doctorNavigationApptDateTxt = findViewById(R.id.dateDoctorAppointmentCard);
        doctorNavigationApptFirstNameTxt = findViewById(R.id.DoctorAppointmentfirstNameRecycle);
        doctorNavigationApptLastNameTxt = findViewById(R.id.DoctorAppointmentlastNameRecycle);
        doctorNavigationApptEndTimeLabel = findViewById(R.id.doctorAppointmentCardEndLabel);
        viewNextAppointmentBtn = findViewById(R.id.seeMoreButton7);

        // Link xml files to display next shift (next shift)
        doctorNavigationDateTxt = findViewById(R.id.doctorShiftDateLabel);
        doctorNavigationStartTimeTxt = findViewById(R.id.doctorShiftCardStartTime2);
        doctorNavigationEndTimeTxt = findViewById(R.id.doctorShiftCardEndTime);
        doctorCancelShiftBtn = findViewById(R.id.doctorCancelShiftButton);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userId = fAuth.getCurrentUser().getUid();

        // Find closest appointment and shifts to display the next
        findNextAppointment();
        findNextShift();

        // Display the name of the current user
        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String firstName = document.getString("First Name");
                        doctorNameTextView.setText(firstName);

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
                startActivity(new Intent(getApplicationContext(), DoctorApprovedAppointments.class));
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


        doctorViewAppointmentRequestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DoctorAppointments.class));
            }

        });

        doctorAddShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SetShift.class));
            }

        });

        viewNextAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPatientUIDForNextAppointment(new PatientUIDCallback() {
                    @Override
                    public void onPatientUIDReceived(String patientUID) {
                        Log.d("PATIENTUID INTENT", "THIS: " + patientUID);
                        if (patientUID != null && !patientUID.isEmpty()) {
                            Intent intent = new Intent(getApplicationContext(), DoctorNextAppointmentPatientInfo.class);
                            intent.putExtra(DoctorNextAppointmentPatientInfo.PATIENT_UID_KEY, patientUID);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(DoctorNavigation.this, "No Upcoming Appointment.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        });

        doctorCancelShiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DoctorNavigation.this);
                alertDialogBuilder.setTitle("Cancel Shift");
                alertDialogBuilder.setMessage("Are you sure you want to cancel this shift?");

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelShift();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    private void cancelShift() {
        // Get the shift details to cancel
        String shiftDate = doctorNavigationDateTxt.getText().toString();
        String shiftStartTime = doctorNavigationStartTimeTxt.getText().toString();
        String shiftEndTime = doctorNavigationEndTimeTxt.getText().toString();

        // Call the method to delete shift
        findAndDeleteShift(shiftDate, shiftStartTime, shiftEndTime);
    }

    // Delete shift
    private void findAndDeleteShift(String shiftDate, String shiftStartTime, String shiftEndTime) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            String currentUserID = fAuth.getCurrentUser().getUid();

            DocumentReference userRef = fStore.collection("Users").document(currentUserID);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        List<Map<String, Object>> shifts = (List<Map<String, Object>>) document.get("Shifts");

                        if (shifts != null) {
                            boolean isAppointmentDuringShift = false;

                            for (Map<String, Object> shift : shifts) {
                                String shiftDateFromList = (String) shift.get("shiftDate");
                                String shiftStartTimeFromList = (String) shift.get("shiftStartTime");
                                String shiftEndTimeFromList = (String) shift.get("shiftEndTime");

                                if (shiftDateFromList.equals(shiftDate) && shiftStartTimeFromList.equals(shiftStartTime) && shiftEndTimeFromList.equals(shiftEndTime)) {

                                    // Loop through appointments to see if within shift time
                                    List<Map<String, Object>> appointments = (List<Map<String, Object>>) document.get("Appointments");
                                    if (appointments != null) {
                                        for (Map<String, Object> appointment : appointments) {
                                            String appointmentDateFromList = (String) appointment.get("appointmentDate");
                                            String appointmentTimeFromList = (String) appointment.get("appointmentTime");

                                            try {
                                                if (shiftDateFromList.equals(appointmentDateFromList) && isTimeBetween(shiftStartTimeFromList, shiftEndTimeFromList, appointmentTimeFromList)) {
                                                    isAppointmentDuringShift = true;
                                                    break;
                                                }
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }

                                    if (!isAppointmentDuringShift) {
                                        userRef.update("Shifts", FieldValue.arrayRemove(shift))
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(DoctorNavigation.this, "Shift canceled.", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("FIREBASE ERROR", "ERROR: " + e.getMessage());
                                                });
                                    } else {
                                        Toast.makeText(DoctorNavigation.this, "Error - Shift during appointment!", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        Log.d("Firestore", "Document does not exist");
                    }
                }
            });
        }
    }

    // Same as findAppointment method but this returns the patientUID
    private void getPatientUIDForNextAppointment(PatientUIDCallback callback) {
        String currentUID = fAuth.getCurrentUser().getUid();
        final FirebaseFirestore fStoreFinal = fStore;
        AtomicReference<String> patientUID = new AtomicReference<>("");

        fStore.collection("Users").document(currentUID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document != null && document.exists()) {
                            List<Map<String, Object>> appointmentsList = (List<Map<String, Object>>) document.get("Appointments");

                            if (appointmentsList != null) {
                                long currentMillis = System.currentTimeMillis();
                                long closestAppointmentTime = Long.MAX_VALUE;
                                Map<String, Object> closestAppointment = null;

                                for (Map<String, Object> appointmentMap : appointmentsList) {
                                    boolean isAccepted = (boolean) appointmentMap.get("isAccepted");
                                    boolean isRejected = (boolean) appointmentMap.get("isRejected");
                                    boolean hasHappened = (boolean) appointmentMap.get("hasHappened");

                                    if (isAccepted && !isRejected && !hasHappened) {
                                        String appointmentDate = (String) appointmentMap.get("appointmentDate");
                                        String appointmentTime = (String) appointmentMap.get("appointmentTime");

                                        long appointmentTimeInMillis = parseTimeToMillis(appointmentTime, appointmentDate);

                                        if (appointmentTimeInMillis != -1) {
                                            if (appointmentTimeInMillis > currentMillis && appointmentTimeInMillis < closestAppointmentTime) {
                                                closestAppointmentTime = appointmentTimeInMillis;
                                                closestAppointment = appointmentMap;
                                            }
                                        }
                                    }
                                }

                                if (closestAppointment != null && patientUID.get() != null) {
                                    patientUID.set((String) closestAppointment.get("patientUID"));
                                    if (callback != null) {
                                        callback.onPatientUIDReceived(patientUID.get());
                                    }
                                }
                            }
                        }
                    }
                });
    }


    // Find next appointment
    private void findNextAppointment() {
        String currentUID = fAuth.getCurrentUser().getUid();
        final FirebaseFirestore fStoreFinal = fStore;

        fStore.collection("Users").document(currentUID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document != null && document.exists()) {
                            List<Map<String, Object>> appointmentsList = (List<Map<String, Object>>) document.get("Appointments");

                            if (appointmentsList != null) {
                                long currentMillis = System.currentTimeMillis();
                                long closestAppointmentTime = Long.MAX_VALUE;
                                Map<String, Object> closestAppointment = null;

                                for (Map<String, Object> appointmentMap : appointmentsList) {

                                    // Conditions to display the next appointment
                                    boolean isAccepted = (boolean) appointmentMap.get("isAccepted");
                                    boolean isRejected = (boolean) appointmentMap.get("isRejected");
                                    boolean hasHappened = (boolean) appointmentMap.get("hasHappened");

                                    if (isAccepted && !isRejected && !hasHappened) {
                                        String appointmentDate = (String) appointmentMap.get("appointmentDate");
                                        String appointmentTime = (String) appointmentMap.get("appointmentTime");

                                        long appointmentTimeInMillis = parseTimeToMillis(appointmentTime, appointmentDate);

                                        if (appointmentTimeInMillis != -1) {
                                            if (appointmentTimeInMillis > currentMillis && appointmentTimeInMillis < closestAppointmentTime) {
                                                closestAppointmentTime = appointmentTimeInMillis;
                                                closestAppointment = appointmentMap;
                                            }
                                        }
                                    }
                                }

                                if (closestAppointment != null) {
                                    String patientUID = (String) closestAppointment.get("patientUID");
                                    if (patientUID != null) {
                                        Map<String, Object> finalClosestAppointment = closestAppointment;
                                        fStoreFinal.collection("Users").document(patientUID).get()
                                                .addOnCompleteListener(userTask -> {
                                                    if (userTask.isSuccessful()) {
                                                        DocumentSnapshot userDocument = userTask.getResult();

                                                        if (userDocument != null && userDocument.exists()) {
                                                            String firstName = userDocument.getString("First Name");
                                                            String lastName = userDocument.getString("Last Name");

                                                            if (firstName != null && lastName != null) {
                                                                String nextAppointmentDate = (String) finalClosestAppointment.get("appointmentDate");
                                                                String nextAppointmentTime = (String) finalClosestAppointment.get("appointmentTime");

                                                                updateUIWithNextAppointment(nextAppointmentDate, nextAppointmentTime, firstName, lastName);
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    }
                });
    }



    // Parse time string into milliseconds
    private long parseTimeToMillis(String timeString, String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US);
            java.util.Date date = sdf.parse(dateString + " " + timeString);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Find the next shift
    private void findNextShift() {
        String currentUID = fAuth.getCurrentUser().getUid();
        final FirebaseFirestore fStoreFinal = fStore;

        fStore.collection("Users").document(currentUID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document != null && document.exists()) {
                            List<Map<String, Object>> shiftsList = (List<Map<String, Object>>) document.get("Shifts");

                            if (shiftsList != null) {
                                long currentMillis = System.currentTimeMillis();
                                long closestShiftStartTime = Long.MAX_VALUE;
                                Map<String, Object> closestShift = null;

                                for (Map<String, Object> shiftMap : shiftsList) {
                                    String shiftDate = (String) shiftMap.get("shiftDate");
                                    String shiftStartTime = (String) shiftMap.get("shiftStartTime");

                                    long shiftStartTimeInMillis = parseTimeToMillis(shiftStartTime, shiftDate);

                                    if (shiftStartTimeInMillis != -1) {
                                        if (shiftStartTimeInMillis > currentMillis && shiftStartTimeInMillis < closestShiftStartTime) {
                                            closestShiftStartTime = shiftStartTimeInMillis;
                                            closestShift = shiftMap;
                                        }
                                    }
                                }

                                if (closestShift != null) {
                                    String shiftStartTime = (String) closestShift.get("shiftStartTime");
                                    String shiftEndTime = (String) closestShift.get("shiftEndTime");
                                    String shiftDate = (String) closestShift.get("shiftDate");

                                    updateUIWithNextShift(shiftDate, shiftStartTime, shiftEndTime);
                                }
                            }
                        }
                    }
                });
    }

    // Update UI for next shift
    private void updateUIWithNextShift(String date, String startTime, String endTime) {
        doctorNavigationDateTxt.setText(date);
        doctorNavigationStartTimeTxt.setText(startTime);
        doctorNavigationEndTimeTxt.setText(endTime);
    }

    // Update UI for next appointment
    private void updateUIWithNextAppointment(String date, String time, String firstName, String lastName) {
        doctorNavigationApptDateTxt.setText(date);
        doctorNavigationApptStartTimeTxt.setText(time);
       // doctorNavigationApptEndTimeTxt.setVisibility(View.GONE);
        doctorNavigationApptFirstNameTxt.setText(firstName);
        doctorNavigationApptLastNameTxt.setText(lastName);
        //doctorNavigationApptEndTimeLabel.setVisibility(View.GONE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        try {
            Date startTime = dateFormat.parse(time);

            if (startTime != null) {

                // Add 30 minutes to the start time
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTime);
                calendar.add(Calendar.MINUTE, 30);

                String endTimeString = dateFormat.format(calendar.getTime());

                doctorNavigationApptEndTimeTxt.setVisibility(View.VISIBLE);
                doctorNavigationApptEndTimeTxt.setText(endTimeString);
                doctorNavigationApptEndTimeLabel.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            doctorNavigationApptEndTimeTxt.setVisibility(View.GONE);
            doctorNavigationApptEndTimeLabel.setVisibility(View.GONE);
        }
    }

    // Callback interface
    public interface PatientUIDCallback {
        void onPatientUIDReceived(String patientUID);
    }
}