package com.example.docappoint.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.example.docappoint.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SelectDoctor extends AppCompatActivity {
    RecyclerView selectionOfDoctors;
    Button settingsButton, patientSelectDoctorBackbtn;

    ArrayList<DoctorChip> doctorChips = new ArrayList<>();
    private SearchView searchView;
    private DoctorSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_choose_doctor);

        settingsButton = findViewById(R.id.doctorSelectSettingButton);
        patientSelectDoctorBackbtn = findViewById(R.id.patientChooseDoctorBackButton);
        selectionOfDoctors = findViewById(R.id.selectDoctorRecyclerView);
        selectionOfDoctors.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorSearchAdapter(doctorChips); // Initialize the adapter
        selectionOfDoctors.setAdapter(adapter);

        searchView = findViewById(R.id.selectDoctorSearchView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int isDoctor = document.getLong("isDoctor") != null ? Math.toIntExact(document.getLong("isDoctor")) : 0;
                                if (isDoctor == 1) {
                                    String firstName = document.getString("First Name");
                                    String lastName = document.getString("Last Name");
                                    String uid = document.getString("UID");

                                    double rating = document.getDouble("Rating") != null ? document.getDouble("Rating") : 0;
                                    float doctorRating = (float)rating;
                                    long numRating = document.getLong("numOfRatings") != null ? document.getLong("numOfRatings") : 0;
                                    int numOfRating = (int)numRating;

                                    ArrayList<String> specialties = (ArrayList<String>) document.get("Specialties");

                                    ArrayList<Appointment> l = (ArrayList<Appointment>)document.get("docAppointments") ;


                                    // Create a new DoctorChip with the retrieved data
                                    DoctorChip doctorChip = new DoctorChip(firstName, lastName, specialties,doctorRating, numOfRating, l, uid);
                                    doctorChips.add(doctorChip);
                                }
                            }
                            adapter.notifyDataSetChanged(); // Notify the adapter once data is fetched
                        } else {
                            Log.d("Yo", "Yo") ;
                        }
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus(); // Clear focus from the search view when query submitted
                adapter.filter(s); // Filter the adapter
                return true; // Return true to indicate the query was handled
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.filter(s); // Filter the adapter
                return true; // Return true since the callback has handled the event
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Settings.class));
                finish();
            }
        });

        // Back button
        patientSelectDoctorBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientNavigation.class));
                finish();
            }
        });
    }
}


