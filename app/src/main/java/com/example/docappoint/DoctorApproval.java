package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class DoctorApproval extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    EditText doctorApprovalFirstNameText, doctorApprovalLastNameText,
            doctorApprovalAddressText, doctorApprovalEmployeeNumberText, doctorApprovalPhoneNumberText, doctorApprovalEmailText;
    Button doctorApprovalApproveRequestButton, doctorApprovalDenyRequestButton, doctorApprovalBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_doctor_request);

        // Initialize Firebase and Firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        // Link xml files
        doctorApprovalApproveRequestButton = findViewById(R.id.doctorApprovalApproveRequestButton);
        doctorApprovalDenyRequestButton = findViewById(R.id.doctorApprovalDenyRequestButton);
        doctorApprovalBackButton = findViewById(R.id.doctorApprovalBackButton);
        doctorApprovalFirstNameText = findViewById(R.id.doctorApprovalFirstNameText);
        doctorApprovalLastNameText =findViewById(R.id.doctorApprovalLastNameText);
        doctorApprovalAddressText = findViewById(R.id.doctorApprovalAddressText);
        doctorApprovalEmployeeNumberText = findViewById(R.id.doctorApprovalEmployeeNumberText);
        doctorApprovalPhoneNumberText = findViewById(R.id.doctorApprovalPhoneNumberText);
        doctorApprovalEmailText = findViewById(R.id.doctorApprovalEmailText);

        // Fetch data from Firestore database for pending users waiting to get approved
        DocumentReference pendingUsersDocRef = fStore.collection("PendingUsers").document(userId);

        // Retrieve user data from the intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");
            String address = intent.getStringExtra("address");
            String employeeNumber = intent.getStringExtra("employeeNumber");
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String email = intent.getStringExtra("email");

            // Update the EditText fields with the retrieved data
            doctorApprovalFirstNameText.setText(firstName);
            doctorApprovalLastNameText.setText(lastName);
            doctorApprovalAddressText.setText(address);
            doctorApprovalEmployeeNumberText.setText(employeeNumber);
            doctorApprovalPhoneNumberText.setText(phoneNumber);
            doctorApprovalEmailText.setText(email);
        }

        doctorApprovalApproveRequestButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pendingUsersDocRef.get().addOnSuccessListener(DocumentSnapshot ->{

                    if(DocumentSnapshot.exists()){
                        //Fetch the data for approved users collection
                        CollectionReference approvedColRef = fStore.collection("Users");

                        //Create map for data recieved
                        Map<String, Object> user = DocumentSnapshot.getData();

                        //add user to approved users collection
                        approvedColRef.add(user).addOnSuccessListener(success ->{
                            //Display toast if user is approved
                            Toast.makeText(DoctorApproval.this, "User approved", Toast.LENGTH_SHORT).show();
                        });

                    }
                    else{
                        //Handle exception that it does not exist
                        Toast.makeText(DoctorApproval.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                });

                //Go back to admin homepage
                startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                finish();
            }
        });
        doctorApprovalBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), AdminNavigation.class));
                finish();
            }
        });


    }


}