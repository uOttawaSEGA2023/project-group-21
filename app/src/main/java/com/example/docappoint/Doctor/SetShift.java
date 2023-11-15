package com.example.docappoint.Doctor;

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
        Calendar.getInstance();

        //set the calendar date to current date
        calendarView.setDate(System.currentTimeMillis());
        getDate();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int day) {
                //create label that displays the date chosen
                //DONT FORGET TO ADD 1 TO MONTH IN THIS METHOD
            }
        });

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getDate(){
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
        calendar.setTimeInMillis(date);
        String selectedDate = simpleDateFormat.format(calendar.getTime());
    }

}