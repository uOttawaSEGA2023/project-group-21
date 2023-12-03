package com.example.docappoint.Doctor;

import static android.content.ContentValues.TAG;

import java.text.DateFormat;

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

                startActivity(new Intent(getApplicationContext(), DoctorNavigation.class));
                finish();

            }
        });

        confirmShiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Firebase Users collection for the current doctor and add the fields
                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String userId = fAuth.getCurrentUser().getUid();

                // Get current date
                Calendar currentDate = Calendar.getInstance();

                DocumentReference doctorRef = fStore.collection("Users").document(userId);

                // Check if the selected date is past current date
                if (calendar.before(currentDate)) {

                    Log.d("CALENDAR DATE, ", "calendar date " + calendar);
                    Log.d("CURRENT DATE", "currentDate " + currentDate);
                    Toast.makeText(SetShift.this, "Cannot book past dates", Toast.LENGTH_SHORT).show();
                }
                else{
                    //check doctor document to see if shift is already booked and if there is a conflict
                    doctorRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            //create an arraylist for the current booked shifts
                            if(document.exists()){
                                List<HashMap<String, Object>> bookedShifts = (List<HashMap<String, Object>>) document.get("Shifts");
                                if(bookedShifts != null){
                                    //for every shift check if there is overlap, if there is display message and return
                                    for(HashMap<String, Object> s : bookedShifts){

                                        Log.d("Success", "overlapping time 1");

                                        String date = (String) s.get("shiftDate");

                                        if(selectedDate.equals(date) && (overlappingShift(calendar, (String) s.get("shiftStartTime"), (String) s.get("shiftEndTime"), getStartTime(), getEndTime()))){
                                            Toast.makeText(SetShift.this, "There is a shift overlap. Could not book new shift", Toast.LENGTH_SHORT).show();
                                            Log.d("Success", "overlapping time");
                                            return;
                                        }
                                    }
                                }

                                if(getStartTime().equals(getEndTime())){
                                    Toast.makeText(SetShift.this, "Shift start time and end time can not be the same", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    // ADD SHIFTS OBJECTS TO NEW FIELD IN "USERS" COLLECTION IN FIREBASE FOR THE CURRENT DOCTOR (FAUTH ACCOUNT)
                                    Shifts shift = new Shifts(selectedDate, getStartTime(), getEndTime(), false, false);

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

    public boolean overlappingShift(Calendar selectedDate, String currentStartTime, String currentEndTime, String newStartTime, String newEndTime){

        try{

            //format shift times
            SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.getDefault());

            Calendar firstStartTime = (Calendar) selectedDate.clone();
            Calendar firstEndTime = (Calendar) selectedDate.clone();
            Calendar secondStartTime = (Calendar) selectedDate.clone();
            Calendar secondEndTime = (Calendar) selectedDate.clone();

            Date startTime = format.parse(getStartTime());
            Date endTime = format.parse(currentEndTime);
            Date startTime2 = format.parse(newStartTime);
            Date endTime2 = format.parse(newEndTime);

            firstStartTime.set(Calendar.HOUR_OF_DAY, startTime.getHours());
            firstStartTime.set(Calendar.MINUTE, startTime.getMinutes());

            firstEndTime.set(Calendar.HOUR_OF_DAY, endTime.getHours());
            firstEndTime.set(Calendar.MINUTE, endTime.getMinutes());

            secondStartTime.set(Calendar.HOUR_OF_DAY, startTime2.getHours());
            secondStartTime.set(Calendar.MINUTE, startTime2.getMinutes());

            secondEndTime.set(Calendar.HOUR_OF_DAY, endTime2.getHours());
            secondEndTime.set(Calendar.MINUTE, endTime2.getMinutes());

            Log.d("Start time", String.valueOf(format.parse(currentStartTime)));
            Log.d("End time", String.valueOf(format.parse(currentEndTime)));
            Log.d("Proposed start time", String.valueOf(format.parse(newStartTime)));
            Log.d("Proposed end time", String.valueOf(format.parse(newEndTime)));

            //compare shift times to see if there is any overlap
            if((secondStartTime.before(firstEndTime) && !secondStartTime.before(firstEndTime)) || (secondEndTime.after(firstStartTime) && !secondEndTime.after(firstEndTime))|| secondStartTime.equals(firstEndTime) || secondEndTime.equals(firstStartTime)){
                Log.d("Successful overlap" , "There is overlap" );
                return true;
            }

        }
        catch(ParseException e){
            e.printStackTrace();
        }
        return false;

    }

}