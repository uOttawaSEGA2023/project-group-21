package com.example.docappoint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.ktx.Firebase;

public class Settings extends AppCompatActivity {

    TextView nameAccountSettings, emailAccountSettings, phoneAccountSettings;
    View profilePictureSettings;
    Button logOutAccount, settingsBackBtn, deleteAccountSettingsBtn;
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
        profilePictureSettings = findViewById(R.id.profilePicSettings);
        logOutAccount = findViewById(R.id.logOutSettingsButton);
        settingsBackBtn = findViewById(R.id.settingsBackButton);
        deleteAccountSettingsBtn = findViewById(R.id.deleteAccountSettingsButton);

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

        profilePictureSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        // Back to welcome <role> screen when back button is clicked
        // (it should lead to navigation screen so this is an error)
        settingsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        deleteAccountSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = createDialog();
                dialog.show();

            }
        });


    }

    AlertDialog createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your account?");

        builder.setPositiveButton("Delete Account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CollectionReference usersCollection = fStore.collection("Users");

                // Get the reference for the user with UserId
                DocumentReference userDocument = usersCollection.document(userId);
                userDocument.delete();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete();
                AlertDialog innerDialog = createDeleteDialog();
                innerDialog.show();


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });

        return builder.create();

    }

    AlertDialog createDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Account deleted. You will now return to the homepage");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), Homepage.class));
                finish();

            }
        });
        return builder.create();
    }






}