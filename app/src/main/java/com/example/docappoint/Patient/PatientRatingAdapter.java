package com.example.docappoint.Patient;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.Appointment;
import com.example.docappoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PatientRatingAdapter extends RecyclerView.Adapter<PatientRatingAdapter.ViewHolder> {

    private List<Appointment> pAppointmentList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Declare variables
        public TextView patientAdapterDoctorFirstNameTxt, patientAdapterDoctorLastNameTxt, patientAdapterDateLabelTxt, patientAdapterCardStartTimeTxt, patientAdapterCardEndTimeTxt;
        public Button patientAdapterRateShiftButton;

        // Link to xml files
        public ViewHolder(View view) {
            super(view);
            patientAdapterDateLabelTxt = view.findViewById(R.id.datePatientAppointmentCard);
            patientAdapterCardStartTimeTxt = view.findViewById(R.id.patientAppointmentCardStartTime);
            patientAdapterCardEndTimeTxt = view.findViewById(R.id.patientCardEndTime);
            patientAdapterRateShiftButton = view.findViewById(R.id.cancelPatientAppointment);

            // Shows doctor name
            patientAdapterDoctorFirstNameTxt = view.findViewById(R.id.patientAppointmentFirstNameRecycle);
            patientAdapterDoctorLastNameTxt = view.findViewById(R.id.patientAppointmentLastNameRecycle);
        }
    }

    public PatientRatingAdapter(List<Appointment> pAppointmentList) {
        this.pAppointmentList = pAppointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_patient_appointment, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment currentAppointment = pAppointmentList.get(position);

        // Retrieve date and time from the current appointment
        String appointmentDate = currentAppointment.getAppointmentDate();
        String appointmentTime = currentAppointment.getAppointmentTime();

        // Find the appointment matching the date and time
        findAppointment(appointmentDate, appointmentTime, holder);

        holder.patientAdapterDateLabelTxt.setText(currentAppointment.getAppointmentDate());
        holder.patientAdapterCardStartTimeTxt.setText(currentAppointment.getAppointmentTime());
        holder.patientAdapterRateShiftButton.setText("Rate");

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
        holder.patientAdapterRateShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), PatientRateDoctor.class);
                holder.itemView.getContext().startActivity(intent);
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
}
