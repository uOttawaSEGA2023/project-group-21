package com.example.docappoint.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docappoint.Doctor.DoctorNavigation;
import com.example.docappoint.Doctor.SetShift;
import com.example.docappoint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientRateDoctor extends AppCompatActivity {

    Button patientRateDoctorBackBtn, patientRateConfirmBtn;
    EditText patientRateDoctorNum;
    RatingBar patientRateRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_rate_doctor);

        // Link xml files
        patientRateDoctorBackBtn = findViewById(R.id.patientRatingBackButton);
        patientRateDoctorNum = findViewById(R.id.patientRatingNum);
        patientRateRatingBar = findViewById(R.id.patientRatingBar);
        patientRateConfirmBtn = findViewById(R.id.patientConfirmRatingButton);

        addListeners();
    }

    private void addListeners(){
        patientRateDoctorNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String num = s.toString();
                if (!num.isEmpty()) {
                    float rating = Float.parseFloat(num);
                    patientRateRatingBar.setRating(rating);
                }
            }
        });

        patientRateRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    patientRateDoctorNum.setText(String.valueOf(rating));
                }
            }
        });

        // Back Button
        patientRateDoctorBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientPastAppointments.class));
                finish();
            }
        });

        patientRateConfirmBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();

                Intent intent = getIntent();
                String doctorID = intent.getStringExtra("doctorToRateID");

                float userRating = patientRateRatingBar.getRating();

                if (fAuth.getCurrentUser() != null) {

                    DocumentReference userRef = fStore.collection("Users").document(doctorID);
                    userRef.get().addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {

                                Map<String, Object> rating = new HashMap<>();
                                rating.put("Rating", String.valueOf(rating));

                                // Update the field in the document
                                userRef.update("Rating", FieldValue.arrayUnion(userRating))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PatientRateDoctor.this, "Gave rating of " + userRating, Toast.LENGTH_SHORT).show();
                                                List<Double> ratings = (List<Double>) document.get("Rating");
                                                float average = 0.0f;

                                                if(ratings != null){
                                                    //tally the average rating
                                                    for(Double r : ratings){
                                                        average += r;
                                                    }

                                                    average = average / ratings.size();
                                                }

                                                userRef.update("AvgRating", average);

                                                userRef.update("numOfRatings", ratings.size() +1);

                                                startActivity(new Intent(getApplicationContext(), PatientPastAppointments.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Rate doctor", "Error rating doctor", e);
                                            }
                                        });


                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        }
                    });
                }
            }
        });


    }
}