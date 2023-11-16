package com.example.docappoint.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.docappoint.R;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SetShift extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CalendarView calendarView;
    Calendar calendar;

    private String selectedDate, startTime, endTime;

    Button setShiftBackBtn, confirmShiftBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_set_shift);

        //start and end time dropdown menu code
        Spinner startTime = findViewById(R.id.doctorSelectShiftStartTime);
        Spinner endTime = findViewById(R.id.doctorSelectShiftEndTime);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startTime.setAdapter(adapter);
        startTime.setOnItemSelectedListener(this);
        endTime.setAdapter(adapter);
        endTime.setOnItemSelectedListener(this);

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
                //create label that displays the date chosen
                //DONT FORGET TO ADD 1 TO MONTH IN THIS METHOD
            }
        });

        setShiftBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), ShiftHistory.class));
                finish();

            }
        });

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        startTime = parent.getItemAtPosition(position).toString();
        //endTime = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public String getDate(){
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
        calendar.setTimeInMillis(date);
        selectedDate = simpleDateFormat.format(calendar.getTime());

        return selectedDate;
    }

    public String getStartTime(){
        return this.startTime;
    }

    public String getEndTime(){
        return this.endTime;
    }

}