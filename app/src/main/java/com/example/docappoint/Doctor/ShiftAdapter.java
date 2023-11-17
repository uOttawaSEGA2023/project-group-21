package com.example.docappoint.Doctor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.R;

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


            }
        });
    }

    @Override
    public int getItemCount() {
        return shiftList.size();
    }
}
