package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StatusRejected extends AppCompatActivity {

    Button statusRejectedContinueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_rejected);

        // Link xml files
        statusRejectedContinueBtn = findViewById(R.id.statusRejectedContinueButton);

        // Show additional toast message
        Toast.makeText(StatusRejected.this, "Account has been rejected from admin approval", Toast.LENGTH_SHORT).show();

        // Continue button leads back to homepage
        statusRejectedContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Homepage.class));
                finish();
            }
        });
    }



}