package com.example.docappoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


/*
    private static final String TAG = "Homepage";

    // Declare FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



        // Allows the use of Firebase Emulators
        ConfigureFirebaseServices();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    // Check to see if the user is currently signed in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
 */

public class Homepage extends AppCompatActivity {

    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Connect UI elements to backend
        createAccountButton = findViewById(R.id.createAccountButton);

        //Handle click for account creation
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SelectAccount.class));
            }
        });

    }

}