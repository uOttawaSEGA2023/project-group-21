package com.example.docappoint.Patient;

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

public class BookAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CalendarView patientCalendarView;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_book_appointment);

        //start and end time dropdown menu code
        Spinner apptStartTime = findViewById(R.id.patientAppointmentStartTime);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.patientTimes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        apptStartTime.setAdapter(adapter);
        apptStartTime.setOnItemSelectedListener(this);

        //date picker code
        patientCalendarView = findViewById(R.id.patientAppointmentDate);
        Calendar.getInstance();

        //set the calendar date to current date
        patientCalendarView.setDate(System.currentTimeMillis());
        patientCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
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

        /*
    public void getDate(){
        long date = patientCalendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
        calendar.setTimeInMillis(date);
        String selectedDate = simpleDateFormat.format(calendar.getTime());
    }*/

}

