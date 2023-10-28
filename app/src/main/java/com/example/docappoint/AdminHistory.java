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

public class AdminHistory extends AppCompatActivity {

    Button adminHistoryBackBtn;
    RecyclerView rejectionList;
    List<ListRequest> rejectionRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approval_history);


        rejectionRequests.add(new ListRequest("Kyle", "Tran", "Ugly"));


        rejectionList = findViewById(R.id.accountApprovalHistory);
        rejectionList.setLayoutManager(new LinearLayoutManager(this));
        RequestAdapter adapter = new RequestAdapter(rejectionRequests);
        rejectionList.setAdapter(adapter);

        // Link xml files
         adminHistoryBackBtn = findViewById(R.id.patientRegBackButton);

        // Back to admin navigation class
         adminHistoryBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                finish();
            }
            });
    }
    }