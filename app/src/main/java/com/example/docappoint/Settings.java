package com.example.docappoint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.ktx.Firebase;

public class Settings extends AppCompatActivity {

    EditText nameAccountSettings, emailAccountSettings, phoneAccountSettings;
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
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        // Fetch data from Firestore database
        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                String firstName = documentSnapshot.getString("First Name");
                String lastName = documentSnapshot.getString("Last Name");

                // Concatenate the first name and last name with a space in between
                String fullName = firstName + " " + lastName;
                nameAccountSettings.setText(fullName);

                emailAccountSettings.setText(documentSnapshot.getString("Email"));
                phoneAccountSettings.setText(documentSnapshot.getString("Phone Number"));

         }
        });

    }
}