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

import com.example.docappoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ViewHolder> {

    private List<Shifts> shiftList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView shiftAdapterDateLabelTxt, shiftAdapterCardStartTimeTxt, shiftAdapterCardEndTimeTxt;
        public Button shiftAdapterCancelShiftButton, shiftAdapterChangeShiftButton;

        // Link to xml files
        public ViewHolder(View view) {
            super(view);
            shiftAdapterDateLabelTxt = view.findViewById(R.id.doctorShiftDateLabel);
            shiftAdapterCardStartTimeTxt = view.findViewById(R.id.doctorShiftCardStartTime2);
            shiftAdapterCardEndTimeTxt = view.findViewById(R.id.doctorShiftCardEndTime);
            shiftAdapterCancelShiftButton = view.findViewById(R.id.doctorCancelSheiftButton);
        }
    }

    public ShiftAdapter(List<Shifts> shiftList) {
        this.shiftList = shiftList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_doctor_shift, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shifts currentShift = shiftList.get(position);
        holder.shiftAdapterDateLabelTxt.setText(currentShift.getShiftDate());
        holder.shiftAdapterCardStartTimeTxt.setText(currentShift.getShiftStartTime());
        holder.shiftAdapterCardEndTimeTxt.setText(currentShift.getShiftEndTime());

        boolean shiftCompleted = currentShift.getShiftCompleted();
        boolean isBooked = currentShift.getIsBooked();

        // IMPLEMENT LATER SO IF THE isBOOKED (IF PATIENT BOOKED AN APPOINTMENT IN THE SHIFT)
        // VARIABLE IS SET TO FALSE, IT WILL CANCEL, IF NOT IT WILL NOT CANCEL
        holder.shiftAdapterCancelShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog Object
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setTitle("Cancel Shift");
                alertDialogBuilder.setMessage("Are you sure you want to cancel this shift?");

                // Yes button pressed
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Delete appointment from the Appointment field and show toast message
                        findAndDeleteShift(currentShift.getShiftDate(), currentShift.getShiftStartTime(), currentShift.getShiftEndTime(), holder,  currentShift);
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
        return shiftList.size();
    }

    public void findAndDeleteShift(String shiftDate, String shiftStartTime, String shiftEndTime, ShiftAdapter.ViewHolder holder, Shifts currentShifts){
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        AtomicBoolean isChecked = new AtomicBoolean(false);

        // Get current doctor user
        if (fAuth.getCurrentUser() != null) {
            String currentUserID = fAuth.getCurrentUser().getUid();

            DocumentReference userRef = fStore.collection("Users").document(currentUserID);
            userRef.get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        List<HashMap<String, Object>> shifts = (List<HashMap<String, Object>>) document.get("Shifts");
                        List<HashMap<String, Object>> appointments = (List<HashMap<String, Object>>) document.get("Appointments");

                        if (shifts != null) {

                            // Go through all appointments to find the shift with the matching time
                            for (HashMap<String, Object> shift : shifts) {
                                String shiftDateFromList = (String) shift.get("shiftDate");
                                String shiftStartTimeFromList = (String) shift.get("shiftStartTime");
                                String shiftEndTimeFromList = (String) shift.get("shiftEndTime");

                                // Check if date and time match
                                if (shiftDateFromList.equals(shiftDate) && shiftStartTimeFromList.equals(shiftStartTime) && shiftEndTimeFromList.equals(shiftEndTime)) {


                                    for(HashMap<String, Object> appointment : appointments ){
                                        String appointmentDateFromList = (String) appointment.get("appointmentDate");
                                        String appointmentTimeFromList = (String) appointment.get("appointmentTime");


                                        try {
                                            if(shiftDateFromList.equals(appointmentDateFromList) && isTimeBetween(shiftStartTimeFromList, shiftEndTimeFromList, appointmentTimeFromList)) {

                                                Toast.makeText(holder.itemView.getContext(), "Error - Shift during appointment!", Toast.LENGTH_SHORT).show();
                                                isChecked.set(true);
                                                Log.d("Appointment during shift", String.valueOf(isChecked));
                                                break;
                                            }
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }


                                    if(isChecked.get() == false){
                                        // Delete matching appointment
                                        userRef.update("Shifts", FieldValue.arrayRemove(shift))
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(holder.itemView.getContext(), "Shift canceled.", Toast.LENGTH_SHORT).show();

                                                    // Remove the Appointment object from the list
                                                    shiftList.remove(currentShifts);
                                                    notifyDataSetChanged();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("FIREBASE ERROR", "ERROR: " + e.getMessage());
                                                });
                                    }

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

    public static boolean isTimeBetween(String startTime, String endTime, String checkTime) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");

        Date start = format.parse(startTime);
        Date end = format.parse(endTime);
        Date check = format.parse(checkTime);

        return check.compareTo(start) >= 0 && check.compareTo(end) <= 0;

    }
}
