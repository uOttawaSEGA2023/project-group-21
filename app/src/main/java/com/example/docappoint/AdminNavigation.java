package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminNavigation extends AppCompatActivity {

    Button adminSettingsBtn, adminViewHistoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_navigation);

        adminSettingsBtn = findViewById(R.id.adminSettingButton);
        adminViewHistoryBtn = findViewById(R.id.viewApprovalHistoryButton);

        // Redirects to AdminHistory class to view approval history
        adminViewHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminHistory.class));
                finish();
            }
        });

        // Redirects to settings (for now since admin might have specific settings [need to log out])
        adminSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Settings.class));
                finish();
            }
        });
    }
}