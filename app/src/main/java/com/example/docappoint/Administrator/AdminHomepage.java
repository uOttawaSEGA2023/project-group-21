package com.example.docappoint.Administrator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.docappoint.R;

public class AdminHomepage extends AppCompatActivity {

    Button adminContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        // Assign variables to direct xml files
        adminContinue = findViewById(R.id.adminContinueButton);

        // Continue button leading to doctor navigation page
        adminContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
            }
        });
    }
}