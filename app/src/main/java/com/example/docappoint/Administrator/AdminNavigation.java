package com.example.docappoint.Administrator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import com.example.docappoint.ListRequest;
import com.example.docappoint.R;
import com.example.docappoint.RequestAdapter;
import com.example.docappoint.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminNavigation extends AppCompatActivity {

    Button adminViewHistoryBtn, viewMoreInfoBtn;
    RecyclerView accountApprovalList;

    ImageButton adminSettingsBtn;
    List<ListRequest> accountRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_navigation);

        adminSettingsBtn = findViewById(R.id.doctorSelectSettingButton);
        adminViewHistoryBtn = findViewById(R.id.viewApprovalHistoryButton);
        accountApprovalList = findViewById(R.id.accountApprovals);

        accountApprovalList = findViewById(R.id.accountApprovals);
        accountApprovalList.setLayoutManager(new LinearLayoutManager(this));
        RequestAdapter adapter = new RequestAdapter(accountRequests);
        accountApprovalList.setAdapter(adapter);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get user data from firestore (PendingUsers Collection)
        db.collection("PendingUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // PendingUsers data extraction
                                String firstName = document.getString("First Name");
                                String lastName = document.getString("Last Name");

                                // Check if user type is doctor or patient and assign it to userType variable
                                int isDoctor = document.getLong("isDoctor") != null ? Math.toIntExact(document.getLong("isDoctor")) : 0;
                                int isPatient = document.getLong("isPatient") != null ? Math.toIntExact(document.getLong("isPatient")) : 0;

                                String userType = "N/A";

                                if (isDoctor == 1 ) {
                                    userType = "Doctor";
                                    String address = document.getString("Address");
                                    String phoneNumber = document.getString("Phone Number");
                                    String email = document.getString("Email");
                                    String password = document.getString("Password");
                                    String uid = document.getString("UID");
                                    String employeeNumber = document.getString("Employee Number");
                                    ArrayList<String> specialties = (ArrayList<String>) document.get("Specialties");
                                    boolean rejected = document.getBoolean("wasRejected");
                                    ListRequest request = new ListRequest(firstName, lastName, userType, address, phoneNumber, email, password, uid, employeeNumber, specialties, rejected);
                                    accountRequests.add(request);


                                } else if (isPatient == 1) {
                                    userType = "Patient";

                                    // Create ListRequest for patients
                                    String address = document.getString("Address");
                                    String phoneNumber = document.getString("Phone Number");
                                    String email = document.getString("Email");
                                    String password = document.getString("Password");
                                    String uid = document.getString("UID");
                                    String healthCardNumber = document.getString("Health Card Number");
                                    boolean rejected = document.getBoolean("wasRejected");
                                    ListRequest request = new ListRequest(firstName, lastName, userType, address, phoneNumber, email, password, uid, healthCardNumber, rejected);
                                    accountRequests.add(request);
                                }

                            }

                            RequestAdapter adapter = new RequestAdapter(accountRequests);
                            accountApprovalList.setAdapter(adapter);
                        }
                    }
                });

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