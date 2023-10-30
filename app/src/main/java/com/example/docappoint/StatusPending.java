package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StatusPending extends AppCompatActivity {

    Button statusPendingContinueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_pending);

        // Link xml files
        statusPendingContinueBtn = findViewById(R.id.statusPendingContinueButton);

        // Show additional toast message
        Toast.makeText(StatusPending.this, "Please wait for admin approval", Toast.LENGTH_SHORT).show();

        // Continue button leads back to homepage
        statusPendingContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Homepage.class));
                finish();
            }
        });
    }
}