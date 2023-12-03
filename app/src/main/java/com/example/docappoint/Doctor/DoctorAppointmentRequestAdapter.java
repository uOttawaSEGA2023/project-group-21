package com.example.docappoint.Doctor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.Appointment;
import com.example.docappoint.Patient.PatientApproval;
import com.example.docappoint.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoctorAppointmentRequestAdapter extends RecyclerView.Adapter<DoctorAppointmentRequestAdapter.ViewHolder> {
    private final List<DoctorAppointmentRequest> accountApprovalList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView accountFirstName, accountLastName, startTime, endTime,date;
        public Button viewMore;

        public ViewHolder(View view) {
            super(view);
            accountFirstName = view.findViewById(R.id.DoctorAppointmentfirstNameRecycle);
            accountLastName = view.findViewById(R.id.DoctorAppointmentlastNameRecycle);
            date = view.findViewById(R.id.dateDoctorAppointmentCard);
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
            holder.startTime.setText(currentRequest.getAppointmentTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            try {
                Date startTime = dateFormat.parse(currentRequest.getAppointmentTime());
                if (startTime != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startTime);
                    calendar.add(Calendar.MINUTE, 30);
                    String endTimeString = dateFormat.format(calendar.getTime());
                    holder.endTime.setText(endTimeString);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                holder.endTime.setText("");
            }
            holder.date.setText(currentRequest.getAppointmentDate());

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
                intent.putExtra("isAppointRequest", true);
                intent.putExtra("appointment", currentRequest.getAppointment());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return accountApprovalList.size();
        }

        public void removeAppointmentRequest(Appointment appointmentToRemove) {
            for (int i = 0; i < accountApprovalList.size(); i++) {
                if (accountApprovalList.get(i).getAppointment().equals(appointmentToRemove)) {
                    accountApprovalList.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
    }
}
