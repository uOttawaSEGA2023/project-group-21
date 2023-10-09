package com.example.docappoint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.ktx.Firebase;

public class Settings extends AppCompatActivity {

    EditText nameAccountSettings, emailAccountSettings, phoneAccountSettings;

    Button logOutAccount;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize variables with UI
        nameAccountSettings = findViewById(R.id.nameTextSettings);
        emailAccountSettings = findViewById(R.id.emailTextSettings);
        phoneAccountSettings = findViewById(R.id.phoneTextSettings);
        logOutAccount = findViewById(R.id.logOutSettingsButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        // Fetch data from Firestore database and display in appropriate fields
        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    Log.e("Settings", "Error fetching User data: " + error.getMessage());
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("First Name");
                    String lastName = documentSnapshot.getString("Last Name");

                    // Concatenate the first name and last name with a space in between
                    String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                    nameAccountSettings.setText(fullName);

                    emailAccountSettings.setText(documentSnapshot.getString("Email"));
                    phoneAccountSettings.setText(documentSnapshot.getString("Phone Number"));
                }
            }
        });

        // Logs the user out of account when clicked
        logOutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                // Sign out message if the account is successfully signed out (using Toast data)
                Toast.makeText(Settings.this, "You have signed out", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getApplicationContext(), Homepage.class));
                finish();
            }
        });
    }






}