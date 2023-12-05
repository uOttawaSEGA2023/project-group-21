package com.example.docappoint;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.material3.ProgressIndicatorDefaults;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Settings extends AppCompatActivity {

    TextView nameAccountSettings, emailAccountSettings, phoneAccountSettings;
    Button logOutAccount, settingsBackBtn, deleteAccountSettingsBtn, uploadProfilePictureBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    ImageView profilePictureSettings;
    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize variables with UI
        nameAccountSettings = findViewById(R.id.nameTextSettings);
        emailAccountSettings = findViewById(R.id.emailTextSettings);
        phoneAccountSettings = findViewById(R.id.phoneTextSettings);
        logOutAccount = findViewById(R.id.logOutSettingsButton);
        settingsBackBtn = findViewById(R.id.settingsBackButton);
        deleteAccountSettingsBtn = findViewById(R.id.deleteAccountSettingsButton);
        profilePictureSettings = findViewById(R.id.profilePicSettings);
        uploadProfilePictureBtn = findViewById(R.id.uploadProfilePictureButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        // Fetch data from Firestore database and display in appropriate fields
        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Settings", "Error fetching User data: " + error.getMessage());
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("First Name");
                    String lastName = documentSnapshot.getString("Last Name");

                    String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                    nameAccountSettings.setText(fullName);

                    emailAccountSettings.setText(documentSnapshot.getString("Email"));
                    phoneAccountSettings.setText(documentSnapshot.getString("Phone Number"));

                    // Load profile picture
                    String profilePictureUrl = documentSnapshot.getString("Profile Picture");
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        loadImageIntoImageView(profilePictureUrl);
                    }
                }
            }
        });

        profilePictureSettings.setOnClickListener(view -> openImageSelector());

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

        uploadProfilePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagePath = data.getData();

            // Now you have the URI of the selected image. You can do something with it, like display it in an ImageView.
            getImageInImageView();
        }
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void getImageInImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        profilePictureSettings.setImageBitmap(bitmap);
    }

    private void uploadImage() {

        if (imagePath == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        // Create a reference to the Storage location where the image will be stored
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString());

        // Put the selected image into Firebase Storage
        storageReference.putFile(imagePath)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Upon successful upload, get the download URL of the image
                        storageReference.getDownloadUrl().addOnCompleteListener(downloadUrlTask -> {
                            if (downloadUrlTask.isSuccessful()) {
                                // Call updateProfilePicture with the obtained image URL
                                String imageUrl = downloadUrlTask.getResult().toString();
                                updateProfilePicture(imageUrl);
                            } else {
                                Toast.makeText(Settings.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(Settings.this, "Image has been uploaded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Settings.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded: " + (int) progress + "%");
                });
    }

    private void updateProfilePicture(String url) {
        FirebaseUser currentUser = fAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Update the profile picture URL in Firebase Firestore
            DocumentReference userDocument = fStore.collection("Users").document(uid);
            userDocument
                    .update("Profile Picture", url)
                    .addOnSuccessListener(aVoid -> {
                        loadImageIntoImageView(url);
                        Toast.makeText(Settings.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(Settings.this, "Failed to update profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadImageIntoImageView(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(profilePictureSettings);
    }

    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your account?");

        builder.setPositiveButton("Delete Account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code to execute when "Delete Account" is clicked
                CollectionReference usersCollection = fStore.collection("Users");

                // Get the reference for the user with UserId
                DocumentReference userDocument = usersCollection.document(userId);
                userDocument.delete();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete();

                // After deleting the account, show a confirmation dialog
                AlertDialog innerDialog = createDeleteDialog();
                innerDialog.show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code to execute when "Cancel" is clicked
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    private AlertDialog createDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Account deleted. You will now return to the homepage");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code to execute when "Ok" is clicked
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), Homepage.class));
                finish();
            }
        });

        return builder.create();
    }

}