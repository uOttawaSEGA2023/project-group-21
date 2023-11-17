package com.example.docappoint.Doctor;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.docappoint.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SetShift extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Decalre firebase variables
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    // Declare variables
    CalendarView calendarView;
    Calendar calendar;

    private String selectedDate, startTime, endTime;

    Button setShiftBackBtn, confirmShiftBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_set_shift);

        // Initialize firebase variables
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //start and end time dropdown menu code
        Spinner startTimeSpinner = findViewById(R.id.doctorSelectShiftStartTime);
        Spinner endTimeSpinner = findViewById(R.id.doctorSelectShiftEndTime);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startTimeSpinner.setAdapter(adapter);
        startTimeSpinner.setOnItemSelectedListener(this);
        endTimeSpinner.setAdapter(adapter);
        endTimeSpinner.setOnItemSelectedListener(this);

        //date picker code
        calendarView = findViewById(R.id.doctorSelectShiftDate);
        calendar = Calendar.getInstance();

        //set the calendar date to current date
        calendarView.setDate(System.currentTimeMillis());
        getDate();

        setShiftBackBtn = findViewById(R.id.doctorUpcomingBackButton);
        confirmShiftBtn = findViewById(R.id.confirmDoctorShift);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int day) {

                // Save selected date to calendar
                calendar.set(year, month, day);

                // Format date
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                selectedDate = sdf.format(calendar.getTime());
            }
        });

        setShiftBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), ShiftHistory.class));
                finish();

            }
        });

        confirmShiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get current date
                Calendar currentDate = Calendar.getInstance();

                // Get Firebase Users collection for the current doctor and add the fields
                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String userId = fAuth.getCurrentUser().getUid();

                // ADD SHIFTS OBJECTS TO NEW FIELD IN "USERS" COLLECTION IN FIREBASE FOR THE CURRENT DOCTOR (FAUTH ACCOUNT)
                Shifts shift = new Shifts(selectedDate, getStartTime(), getEndTime(), false, false);

                DocumentReference doctorRef = fStore.collection("Users").document(userId);

                // Check if the selected date is past current date
                if (calendar.before(currentDate)) {
                    Log.d("CALENDAR DATE, ", "calendar date " + calendar);
                    Log.d("CURRENT DATE", "currentDate " + currentDate);
                    Toast.makeText(SetShift.this, "Cannot book past dates", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    //check doctor document to see if shift is already booked and if there is a conflict
                    doctorRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            //create an arraylist for the current booked shifts
                            if(documentSnapshot.exists()){
                                List<HashMap<String, Object>> bookedShifts = (List<HashMap<String, Object>>) documentSnapshot.get("Shifts");
                                if(bookedShifts != null){
                                    //for every shift check if there is overlap, if there is display message and return
                                    for(HashMap<String, Object> s : bookedShifts){

                                        String date = (String) s.get("shiftDate");

                                        if(selectedDate.equals(date) && overlappingShift((String) s.get("shiftStartTime"), (String) s.get("shiftEndTime"), getStartTime(), getEndTime())){
                                            Toast.makeText(SetShift.this, "There is a shift overlap. Could not book new shift", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }

                                    Log.d("CALENDAR DATE, ", "calendar date " + calendar);
                                    Log.d("CURRENT DATE", "currentDate " + currentDate);

                                    // Make new Shifts field in the doctor's document with new shift object
                                    doctorRef.update("Shifts", FieldValue.arrayUnion(shift))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SetShift.this, "Success! Shift Added!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(), DoctorNavigation.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("SetShift", "Error adding shift to doctor's document", e);
                                                }
                                            });
                                }
                            }

                        }

                    });

                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Check which dropdown is used to set startime or endtime
        if (parent.getId() == R.id.doctorSelectShiftStartTime) {
            startTime = parent.getItemAtPosition(position).toString();
        } else if (parent.getId() == R.id.doctorSelectShiftEndTime) {
            endTime = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public String getDate() {
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        calendar.setTimeInMillis(date);
        selectedDate = simpleDateFormat.format(calendar.getTime());

        return selectedDate;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public boolean overlappingShift(String currentStartTime, String currentEndTime, String newStartTime, String newEndTime){
        boolean overlap = false;

        try{
            //format shift times
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

            //convert start time and end times to dates to compare fields
            Date startTime = format.parse(currentStartTime);
            Date endTime = format.parse(currentEndTime);
            Date startTime2 = format.parse(newStartTime);
            Date endTime2 = format.parse(newEndTime);

            //compare shift times to see if there is any overlap
            if(startTime2.before(endTime) || endTime2.after(startTime) || startTime2.equals(endTime) || endTime2.equals(startTime)){
                overlap = true;
            }

            return overlap;
        }
        catch(ParseException e){
            e.printStackTrace();
            return overlap;
        }

    }

}