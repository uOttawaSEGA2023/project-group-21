package com.example.docappoint.Doctor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.ListRequest;
import com.example.docappoint.Patient.PatientApproval;
import com.example.docappoint.Patient.PatientRejectApproval;
import com.example.docappoint.R;
import com.example.docappoint.RequestAdapter;

import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentRequestAdapter extends RecyclerView.Adapter<DoctorAppointmentRequestAdapter.ViewHolder> {
    private List<DoctorAppointmentRequest> accountApprovalList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView accountFirstName, accountLastName, startTime, endTime;
        public Button viewMore;

        public ViewHolder(View view) {
            super(view);
            accountFirstName = view.findViewById(R.id.firstNameRecycle);
            accountLastName = view.findViewById(R.id.lastNameRecycle);
            startTime = view.findViewById(R.id.doctorAppointmentCardStartTime);
            endTime = view.findViewById(R.id.doctorAppointmentCardEndTime2);
            viewMore = view.findViewById(R.id.seeMoreButton);
        }
    }
        public DoctorAppointmentRequestAdapter(List<DoctorAppointmentRequest> l) {
            this.accountApprovalList = l;
        }

        @Override
        public DoctorAppointmentRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_doctor_appointment, parent, false);
            return new DoctorAppointmentRequestAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorAppointmentRequestAdapter.ViewHolder holder, int position) {
            DoctorAppointmentRequest currentRequest = accountApprovalList.get(position);
            holder.accountFirstName.setText(currentRequest.getAccountFirstName());
            holder.accountLastName.setText(currentRequest.getAccountLastName());

            holder.viewMore.setOnClickListener(v -> {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, PatientApproval.class);

                // Pass user data intent to approval page
                intent.putExtra("firstName", currentRequest.getAccountFirstName());
                intent.putExtra("lastName", currentRequest.getAccountLastName());
                intent.putExtra("healthCardNumber", currentRequest.getAccountHealthCardNumber());
                intent.putExtra("address", currentRequest.getAccountAddress());
                intent.putExtra("phoneNumber", currentRequest.getAccountPhoneNumber());
                intent.putExtra("email", currentRequest.getAccountEmail());
                intent.putExtra("password", currentRequest.getAccountPassword());
                intent.putExtra("uid", currentRequest.getAccountUID());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return accountApprovalList.size();
        }
}
