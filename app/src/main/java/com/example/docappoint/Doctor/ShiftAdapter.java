package com.example.docappoint.Doctor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ViewHolder> {

    private List<Shifts> shiftList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView shiftAdapterDateLabelTxt, shiftAdapterCardStartTimeTxt, shiftAdapterCardEndTimeTxt;
        public Button shiftAdapterCancelShiftButton, shiftAdapterChangeShiftButton;

        // Link to xml files
        public ViewHolder(View view) {
            super(view);
            shiftAdapterDateLabelTxt = view.findViewById(R.id.doctorShiftDateLabel);
            shiftAdapterCardStartTimeTxt = view.findViewById(R.id.doctorShiftCardStartTime);
            shiftAdapterCardEndTimeTxt = view.findViewById(R.id.doctorShiftCardEndTime);
            shiftAdapterCancelShiftButton = view.findViewById(R.id.doctorCancelShiftButton);
            shiftAdapterChangeShiftButton = view.findViewById(R.id.doctorChangeShiftButton);
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
                Context context = holder.itemView.getContext();
                // START HERE WOOOOO

                // Get current user to access "Users" collection
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                FirebaseAuth fAuth = FirebaseAuth.getInstance();

                FirebaseUser currentUser = fAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    DocumentReference docRef = fStore.collection("Users").document(userId);

                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                List<HashMap<String, Object>> shiftsData = (List<HashMap<String, Object>>) document.get("Shifts");

                                if (shiftsData != null) {
                                    for (HashMap<String, Object> shiftData : shiftsData) {
                                        //need to figure out how to query shift that is clicked on
                                    }
                                }

                            } else {
                                Log.d("ERROR DEBUG 1", "DOCUMENT NOT FOUND");
                            }
                        } else {
                            Log.d("ERROR DEBUG 2", "TASK FAILURE");
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shiftList.size();
    }
}
