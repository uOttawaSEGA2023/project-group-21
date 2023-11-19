package com.example.docappoint.Patient;

import android.app.AlertDialog;
import android.content.Context;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PatientAppointmentAdapter extends RecyclerView.Adapter<PatientAppointmentAdapter.ViewHolder> {

    private List<Appointment> pAppointmentList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Declare variables
        public TextView patientAdapterDoctorFirstNameTxt, patientAdapterDoctorLastNameTxt, patientAdapterDateLabelTxt, patientAdapterCardStartTimeTxt, patientAdapterCardEndTimeTxt;
        public Button patientAdapterCancelShiftButton;

        // Link to xml files
        public ViewHolder(View view) {
            super(view);
            patientAdapterDateLabelTxt = view.findViewById(R.id.datePatientAppointmentCard);
            patientAdapterCardStartTimeTxt = view.findViewById(R.id.patientAppointmentCardStartTime);
            patientAdapterCardEndTimeTxt = view.findViewById(R.id.patientCardEndTime);
            patientAdapterCancelShiftButton = view.findViewById(R.id.cancelPatientAppointment);

            // Shows doctor name
            patientAdapterDoctorFirstNameTxt = view.findViewById(R.id.patientAppointmentFirstNameRecycle);
            patientAdapterDoctorLastNameTxt = view.findViewById(R.id.patientAppointmentLastNameRecycle);
        }
    }

    public PatientAppointmentAdapter(List<Appointment> pAppointmentList) {
        this.pAppointmentList = pAppointmentList;
    }

    @NonNull
    @Override
    public PatientAppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_patient_appointment, parent, false);
        return new PatientAppointmentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientAppointmentAdapter.ViewHolder holder, int position) {
        Appointment currentAppointment = pAppointmentList.get(position);

        // Retrieve date and time from the current appointment
        String appointmentDate = currentAppointment.getAppointmentDate();
        String appointmentTime = currentAppointment.getAppointmentTime();

        // Find the appointment matching the date and time
        findAppointment(appointmentDate, appointmentTime, holder);

        holder.patientAdapterDateLabelTxt.setText(currentAppointment.getAppointmentDate());
        holder.patientAdapterCardStartTimeTxt.setText(currentAppointment.getAppointmentTime());

        // THIS SHOULD CHANGE AND CONVERTED TO SDF FORM TO +30 MINUTES AND CONVERT BACK TO STRING
        holder.patientAdapterCardEndTimeTxt.setText(currentAppointment.getAppointmentTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        try {
            Date startTime = dateFormat.parse(currentAppointment.getAppointmentTime());
            if (startTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTime);
                calendar.add(Calendar.MINUTE, 30);
                String endTimeString = dateFormat.format(calendar.getTime());
                holder.patientAdapterCardEndTimeTxt.setText(endTimeString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.patientAdapterCardEndTimeTxt.setText("");
        }
        holder.patientAdapterDateLabelTxt.setText(currentAppointment.getAppointmentDate());

        // Implement cancel appointment button (only allowed if scheduled booking is before the 60 minutes)
        holder.patientAdapterCancelShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an AlertDialog Object
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setTitle("Cancel Appointment");
                alertDialogBuilder.setMessage("Are you sure you want to cancel this appointment?");

                // Yes button pressed
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Get the appointment time in SDF format
                        String appointmentTime = currentAppointment.getAppointmentDate() + " " + currentAppointment.getAppointmentTime(); // Include date for parsing
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.getDefault());
                        Log.d("APPOINTMENT TIME BEFORE PARSING", "Unparsed Time: " + appointmentTime);

                        // Calculate time difference of start and end appointment time
                        try {
                            Date startTime = sdf.parse(appointmentTime);
                            if (startTime != null) {

                                // Calculate time difference in minutes
                                long currentTimeInMillis = System.currentTimeMillis();
                                long appointmentTimeInMillis = startTime.getTime();
                                long timeDifference = appointmentTimeInMillis - currentTimeInMillis;
                                long minutesDifference = timeDifference / (60 * 1000);

                                Log.d("CURRENT TIME", "Current time: " + new Date(currentTimeInMillis));
                                Log.d("APPOINTMENT TIME", "Appointment time: " + startTime);
                                Log.d("MINUTE DIFFERENCE", "Difference in minutes: " + minutesDifference);

                                // If current time >= 60 minutes of appointment starting time,
                                if (minutesDifference >= 60) {

                                    // Delete appointment from the Appointment field and show toast message
                                    findAndDeleteAppointment(currentAppointment.getAppointmentDate(), currentAppointment.getAppointmentTime(), holder,  currentAppointment);

                                } else {
                                    Toast.makeText(v.getContext(), "Cancellation not allowed. Less than 60 minutes before the appointment.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // No button pressed
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Show alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return pAppointmentList.size();
    }

    // Gets the first name for doctor
    private void getDoctorFirstName(String doctorUID, ViewHolder holder) {
        FirebaseFirestore.getInstance().collection("Users")
                .document(doctorUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("First Name");
                        if (firstName != null) {
                            holder.patientAdapterDoctorFirstNameTxt.setText(firstName);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Get Doctor First Name", "Error getting first name: " + e.getMessage()));
    }

    // Gets the last name for doctor
    private void getDoctorLastName(String doctorUID, ViewHolder holder) {
        FirebaseFirestore.getInstance().collection("Users")
                .document(doctorUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String lastName = documentSnapshot.getString("Last Name");
                        if (lastName != null) {
                            holder.patientAdapterDoctorLastNameTxt.setText(lastName);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Get Doctor Last Name", "Error getting last name: " + e.getMessage()));
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
                                String doctorUID = (String) appointment.get("doctorUID");

                                // Check if date and time match to get specific doctorUID
                                if (appointmentDateFromList.equals(appointmentDate) && appointmentTimeFromList.equals(appointmentTime)) {

                                    getDoctorFirstName(doctorUID, holder);
                                    getDoctorLastName(doctorUID, holder);
                                    break;
                                }
                            }
                        }
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                }
            });
        }
    }

    private void findAndDeleteAppointment(String appointmentDate, String appointmentTime, ViewHolder holder, Appointment currentAppointment) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        // Get current patient user
        if (fAuth.getCurrentUser() != null) {
            String currentUserID = fAuth.getCurrentUser().getUid();

            DocumentReference userRef = fStore.collection("Users").document(currentUserID);
            userRef.get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        List<HashMap<String, Object>> appointments = (List<HashMap<String, Object>>) document.get("Appointments");

                        if (appointments != null) {

                            // Go through all appointments to find the appointment with the matching time
                            for (HashMap<String, Object> appointment : appointments) {
                                String appointmentDateFromList = (String) appointment.get("appointmentDate");
                                String appointmentTimeFromList = (String) appointment.get("appointmentTime");
                                String doctorUID = (String) appointment.get("doctorUID");

                                // Check if date and time match to get specific doctorUID
                                if (appointmentDateFromList.equals(appointmentDate) && appointmentTimeFromList.equals(appointmentTime)) {

                                    // Delete matching appointment
                                    userRef.update("Appointments", FieldValue.arrayRemove(appointment))
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(holder.itemView.getContext(), "Appointment canceled.", Toast.LENGTH_SHORT).show();

                                                // Remove the Appointment object from the list
                                                pAppointmentList.remove(currentAppointment);
                                                notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("FIREBASE ERROR", "ERROR: " + e.getMessage());
                                            });

                                    // Delete the appointment for the doctor
                                    DocumentReference doctorRef = fStore.collection("Users").document(doctorUID);
                                    doctorRef.get().addOnCompleteListener(doctorTask -> {

                                        if (doctorTask.isSuccessful()) {
                                            DocumentSnapshot doctorDocument = doctorTask.getResult();
                                            if (doctorDocument != null && doctorDocument.exists()) {
                                                List<HashMap<String, Object>> doctorAppointments = (List<HashMap<String, Object>>) doctorDocument.get("Appointments");

                                                // Go through all appointments to find the appointment with the matching time
                                                if (doctorAppointments != null) {
                                                    for (HashMap<String, Object> docAppointment : doctorAppointments) {
                                                        String docAppointmentDate = (String) docAppointment.get("appointmentDate");
                                                        String docAppointmentTime = (String) docAppointment.get("appointmentTime");
                                                        String patientUID = (String) docAppointment.get("patientUID");

                                                        // Check if date and time match for doctor and patient using patientUID
                                                        if (docAppointmentDate.equals(appointmentDate) && docAppointmentTime.equals(appointmentTime) && patientUID.equals(currentUserID)) {
                                                            doctorRef.update("Appointments", FieldValue.arrayRemove(docAppointment));
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.d("Firestore", "Document does not exist");
                                        }
                                    });

                                    break;
                                }

                            }
                        }
                    } else {
                        Log.d("Firestore", "Document does not exist");
                    }
                }
            });
        }
    }

}
