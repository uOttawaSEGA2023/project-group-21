package com.example.docappoint.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.R;
import com.example.docappoint.RequestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShiftHistory extends AppCompatActivity {

    RecyclerView shiftRecyclerView;
    List<Shifts> shiftList = new ArrayList<>();

    Button addShiftBtn, shiftHistoryBackBtn;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_viewshifts);

        addShiftBtn = findViewById(R.id.doctorAddShift);
        shiftHistoryBackBtn = findViewById(R.id.viewShiftsBackButton);


        shiftRecyclerView = findViewById(R.id.viewDoctorShifts);
        shiftRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShiftAdapter sAdapter = new ShiftAdapter(shiftList);
        shiftRecyclerView.setAdapter(sAdapter);

        // GET DATA FROM USERS IN THE NEW "SHIFTS" FIELD
        //GO THRU USER IN FIREBASE AND GET SHIFTS
        //FOR EACH SHIFT CREATE NEW SHIFTS
        //PUT SHIFTS INSIDE LIST OF SHIFTS

        // Get current user to access "Users" collection
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = fStore.collection("Users").document(userId);

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        List<Shifts> retrievedShifts = new ArrayList<>();
                        List<HashMap<String, Object>> shiftsData = (List<HashMap<String, Object>>) document.get("Shifts");

                        if (shiftsData != null) {
                            for (HashMap<String, Object> shiftData : shiftsData) {
                                String shiftDate = (String) shiftData.get("shiftDate");
                                String shiftStartTime = (String) shiftData.get("shiftStartTime");
                                String shiftEndTime = (String) shiftData.get("shiftEndTime");
                                Boolean shiftCompleted = (Boolean) shiftData.get("shiftCompleted");
                                Boolean isBooked = (Boolean) shiftData.get("isBooked");

                                Shifts shift = new Shifts(shiftDate, shiftStartTime, shiftEndTime, shiftCompleted, isBooked);
                                retrievedShifts.add(shift);
                            }

                            shiftList.clear();
                            shiftList.addAll(retrievedShifts);
                            sAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Log.d("ERROR DEBUG 1", "DOCUMENT NOT FOUND");
                    }
                } else {
                    Log.d("ERROR DEBUG 2", "TASK FAILURE");
                }
            });
        }


        addShiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), SetShift.class));
                finish();

            }
        });

        shiftHistoryBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), DoctorNavigation.class));
                finish();

            }
        });

    }

}
