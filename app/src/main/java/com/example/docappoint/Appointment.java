package com.example.docappoint;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Appointment implements Serializable {
    private final String appointmentDate;
    private final String appointmentTime;
    private float appointmentRating;
    private final String patientUID;
    private final String doctorUID;
    private boolean hasHappened;
    private boolean isAccepted;
    private boolean isRejected;

    public Appointment(String appointmentDate, String appointmentTime, float rating, boolean hasHappened, String patientUID, String doctorUID, boolean isAccepted, boolean isRejected) {
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.appointmentRating = rating;
        this.hasHappened = hasHappened;
        this.patientUID = patientUID;
        this.doctorUID = doctorUID;
        this.isAccepted = isAccepted;
        this.isRejected = isRejected;
    }



    public String getAppointmentDate() {
        return this.appointmentDate;
    }

    public String getAppointmentTime() {
        return this.appointmentTime;
    }

    public float getAppointmentRating() {
        return this.appointmentRating;
    }

    public void setAppointmentRating(float appointmentRating) {
        this.appointmentRating = appointmentRating;
    }

    public String getPatientUID() {
        return patientUID;
    }

    public String getDoctorUID() {
        return doctorUID;
    }

    public boolean getHasHappened() {
        return hasHappened;
    }

    public boolean getIsRejected(){return isRejected;}

    public void setIsRejected(boolean isRejected){ this.isRejected = isRejected;}

    public void setHasHappened(boolean hasHappened) {
        this.hasHappened = hasHappened;

    }

    public boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public void updateAppointmentField(String DocOrPatient, String fieldName, Object fieldValue) {
        String parentDocumentId;

        if (DocOrPatient.equals("Doctor")){
            parentDocumentId = getDoctorUID();
        }
        else {
            parentDocumentId = getPatientUID();
        }

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection("Users").document(parentDocumentId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Object appointmentsObject = documentSnapshot.get("Appointments"); // Make sure this field name matches exactly with what's in Firestore
                if (appointmentsObject instanceof List) {
                    List<Map<String, Object>> appointments = (List<Map<String, Object>>) appointmentsObject;

                    // Iterate over the appointments to find the matching appointment and update the field
                    for (int i = 0; i < appointments.size(); i++) {
                        Map<String, Object> appointment = appointments.get(i);
                        if (this.appointmentDate.equals(appointment.get("appointmentDate")) &&
                                this.appointmentTime.equals(appointment.get("appointmentTime")) &&
                                this.patientUID.equals(appointment.get("patientUID")) &&
                                this.doctorUID.equals(appointment.get("doctorUID"))) {

                            // Update the field for the matching appointment
                            appointment.put(fieldName, fieldValue);

                            // Prepare the update for Firestore
                            Map<String, Object> update = new HashMap<>();
                            update.put("Appointments", appointments); // This should match the field name in Firestore

                            // Perform the update in Firestore
                            docRef.update(update)
                                    .addOnSuccessListener(aVoid -> {
                                        // Handle success
                                        Log.d("updateAppointmentField", "Appointment field updated successfully.");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure
                                        Log.e("updateAppointmentField", "Error updating appointment field.", e);
                                    });
                            break; // Break out of the loop once we've found and updated the matching appointment
                        }
                    }
                } else {
                    // The 'Appointments' field is not a List
                    Log.e("updateAppointmentField", "The 'Appointments' field is not a List.");
                }
            } else {
                Log.e("updateAppointmentField", "Document does not exist.");
            }
        }).addOnFailureListener(e -> {
            Log.e("updateAppointmentField", "Error getting document.", e);
        });
    }

    public void refreshFromFirestore(Runnable onCompletion) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = fStore.collection("Users").document(doctorUID);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> appointments = (List<Map<String, Object>>) documentSnapshot.get("Appointments");
                if (appointments != null) {
                    for (Map<String, Object> appointmentMap : appointments) {
                        if (this.appointmentDate.equals(appointmentMap.get("appointmentDate")) &&
                                this.appointmentTime.equals(appointmentMap.get("appointmentTime")) &&
                                this.patientUID.equals(appointmentMap.get("patientUID"))) {
                            // Update local variables
                            this.appointmentRating = appointmentMap.containsKey("appointmentRating") ? ((Number) appointmentMap.get("appointmentRating")).floatValue() : 0.0f;
                            this.hasHappened = (boolean) appointmentMap.get("hasHappened");
                            this.isAccepted = (boolean) appointmentMap.get("isAccepted");
                            this.isRejected = (boolean) appointmentMap.get("isRejected");
                            // Notify that the object has been updated
                            if (onCompletion != null) {
                                onCompletion.run();
                            }
                            return; // Exit the method after updating
                        }
                    }
                }
            }
            // If we reach this point, the appointment was not found or the document does not exist
            Log.e("refreshFromFirestore", "Appointment not found or User document does not exist.");
            if (onCompletion != null) {
                onCompletion.run();
            }
        }).addOnFailureListener(e -> {
            Log.e("refreshFromFirestore", "Error getting user document.", e);
            if (onCompletion != null) {
                onCompletion.run();
            }
        });
    }


}

