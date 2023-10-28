package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class AdminNavigation extends AppCompatActivity {

    Button adminSettingsBtn, adminViewHistoryBtn;
    RecyclerView accountApprovalList;
    List<ListRequest> accountRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_navigation);

        //TO BE EDITED LATER BECUASE IT NEEDS TO INCREASE DYNAMICALLY
        accountRequests.add(new ListRequest("Kyle", "Tran", "Doctor", false));
        accountRequests.add(new ListRequest("Nahi", "Ishti", "Patient", false));

        adminSettingsBtn = findViewById(R.id.adminSettingButton);
        adminViewHistoryBtn = findViewById(R.id.viewApprovalHistoryButton);
        accountApprovalList = findViewById(R.id.accountApprovals);

        accountApprovalList = findViewById(R.id.accountApprovals);
        accountApprovalList.setLayoutManager(new LinearLayoutManager(this));
        RequestAdapter adapter = new RequestAdapter(accountRequests);
        accountApprovalList.setAdapter(adapter);

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