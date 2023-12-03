package com.example.docappoint.Doctor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class DoctorApprovedAdapter extends RecyclerView.Adapter<DoctorApprovedAdapter.ViewHolder> {

    private final List<Appointment> appointmentList;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView patientFirstNameTxt, patientLastNameTxt, dateLabelTxt, cardStartTimeTxt, cardEndTimeTxt, drApprovedLabelTxt;
        public Button cancelAppointmentBtn;

        public ViewHolder(View view) {
            super(view);
            dateLabelTxt = view.findViewById(R.id.datePatientAppointmentCard);
            cardStartTimeTxt = view.findViewById(R.id.patientAppointmentCardStartTime);
            cardEndTimeTxt = view.findViewById(R.id.patientCardEndTime);
            patientFirstNameTxt = view.findViewById(R.id.patientAppointmentFirstNameRecycle);
            patientLastNameTxt = view.findViewById(R.id.patientAppointmentLastNameRecycle);
            cancelAppointmentBtn = view.findViewById(R.id.cancelPatientAppointment);
            drApprovedLabelTxt = view.findViewById(R.id.drLabel);
            drApprovedLabelTxt.setVisibility(View.GONE);
        }
    }

    public DoctorApprovedAdapter(List<Appointment> appointmentList, Context context) {
        this.appointmentList = appointmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_patient_appointment, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment currentAppointment = appointmentList.get(position);

        String appointmentDate = currentAppointment.getAppointmentDate();
        String appointmentTime = currentAppointment.getAppointmentTime();

        findAppointment(appointmentDate, appointmentTime, holder);

        holder.dateLabelTxt.setText(currentAppointment.getAppointmentDate());
        holder.cardStartTimeTxt.setText(currentAppointment.getAppointmentTime());
        holder.cardEndTimeTxt.setText(currentAppointment.getAppointmentTime());
        holder.cancelAppointmentBtn.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
            alertDialogBuilder.setTitle("Cancel Appointment");
            alertDialogBuilder.setMessage("Are you sure you want to cancel this appointment?");

            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelAppointment(currentAppointment);
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    private void cancelAppointment(Appointment appointment) {
        String appointmentDate = appointment.getAppointmentDate();
        String appointmentTime = appointment.getAppointmentTime();

        findAndDeleteAppointment(appointmentDate, appointmentTime, appointment);
    }

    private void findAndDeleteAppointment(String appointmentDate, String appointmentTime, Appointment currentAppointment) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            String currentUserID = fAuth.getCurrentUser().getUid();

            DocumentReference userRef = fStore.collection("Users").document(currentUserID);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        List<HashMap<String, Object>> appointments = (List<HashMap<String, Object>>) document.get("Appointments");

                        if (appointments != null) {
                            for (HashMap<String, Object> appointment : appointments) {
                                String appointmentDateFromList = (String) appointment.get("appointmentDate");
                                String appointmentTimeFromList = (String) appointment.get("appointmentTime");
                                String patientUID = (String) appointment.get("patientUID");

                                if (appointmentDateFromList.equals(appointmentDate) && appointmentTimeFromList.equals(appointmentTime)) {

                                    // Delete appointment for doctor
                                    userRef.update("Appointments", FieldValue.arrayRemove(appointment))
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(context, "Appointment canceled.", Toast.LENGTH_SHORT).show();
                                                appointmentList.remove(currentAppointment);
                                                notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Firebase Error", "Error deleting appointment: " + e.getMessage());
                                            });

                                    // Delete appointment for patient
                                    DocumentReference patientRef = fStore.collection("Users").document(patientUID);
                                    patientRef.get().addOnCompleteListener(patientTask -> {

                                        if (patientTask.isSuccessful()) {
                                            DocumentSnapshot patientDocument = patientTask.getResult();
                                            if (patientDocument != null && patientDocument.exists()) {
                                                List<HashMap<String, Object>> patientAppointments = (List<HashMap<String, Object>>) patientDocument.get("Appointments");

                                                if (patientAppointments != null) {
                                                    for (HashMap<String, Object> patAppointment : patientAppointments) {
                                                        String patAppointmentDateFromList = (String) patAppointment.get("appointmentDate");
                                                        String patAppointmentTimeFromList = (String) patAppointment.get("appointmentTime");
                                                        String docUID = (String) patAppointment.get("doctorUID");

                                                        if (patAppointmentDateFromList.equals(appointmentDate) && patAppointmentTimeFromList.equals(appointmentTime) && docUID.equals(currentUserID)) {
                                                            patientRef.update("Appointments", FieldValue.arrayRemove(patAppointment))
                                                                    .addOnSuccessListener(voidDoc -> {

                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Log.e("Firebase Error", "Error deleting patient appointment: " + e.getMessage());
                                                                    });
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });

                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void findAppointment(String appointmentDate, String appointmentTime, ViewHolder holder) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            String currentUserID = fAuth.getCurrentUser().getUid();

            DocumentReference userRef = fStore.collection("Users").document(currentUserID);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        List<HashMap<String, Object>> appointments = (List<HashMap<String, Object>>) document.get("Appointments");

                        if (appointments != null) {
                            for (HashMap<String, Object> appointment : appointments) {
                                String appointmentDateFromList = (String) appointment.get("appointmentDate");
                                String appointmentTimeFromList = (String) appointment.get("appointmentTime");
                                String patientUID = (String) appointment.get("patientUID");

                                if (appointmentDateFromList.equals(appointmentDate) && appointmentTimeFromList.equals(appointmentTime)) {
                                    getPatientFirstName(patientUID, holder);
                                    getPatientLastName(patientUID, holder);
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void getPatientFirstName(String patientUID, ViewHolder holder) {
        FirebaseFirestore.getInstance().collection("Users")
                .document(patientUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("First Name");
                        if (firstName != null) {
                            holder.patientFirstNameTxt.setText(firstName);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Get Patient First Name", "Error getting first name: " + e.getMessage()));
    }

    private void getPatientLastName(String patientUID, ViewHolder holder) {
        FirebaseFirestore.getInstance().collection("Users")
                .document(patientUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String lastName = documentSnapshot.getString("Last Name");
                        if (lastName != null) {
                            holder.patientLastNameTxt.setText(lastName);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Get Patient Last Name", "Error getting last name: " + e.getMessage()));
    }
}