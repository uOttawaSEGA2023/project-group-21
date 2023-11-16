package com.example.docappoint.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.docappoint.R;

public class ShiftHistory extends AppCompatActivity {

    Button addShiftBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_viewshifts);


        addShiftBtn = findViewById(R.id.doctorAddShift);

        addShiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), SetShift.class));
                finish();

            }
        });

    }
}
