package com.example.docappoint;
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

import com.example.docappoint.Doctor.DoctorApproval;
import com.example.docappoint.Doctor.DoctorRejectApproval;
import com.example.docappoint.Patient.PatientApproval;
import com.example.docappoint.Patient.PatientRejectApproval;

import java.util.ArrayList;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<ListRequest> accountApprovalList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView accountFirstName, accountLastName, accountType;
        public Button seeMoreButton;

        public ViewHolder(View view){
            super(view);
            accountFirstName = view.findViewById(R.id.DoctorAppointmentfirstNameRecycle);
            accountLastName = view.findViewById(R.id.DoctorAppointmentlastNameRecycle);
            accountType = view.findViewById(R.id.roleRecycle);
            seeMoreButton = view.findViewById(R.id.seeMoreButton);
        }
    }

    public RequestAdapter(List<ListRequest> l){
        this.accountApprovalList = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_rows, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListRequest currentRequest = accountApprovalList.get(position);
        holder.accountFirstName.setText(currentRequest.getAccountFirstName());
        holder.accountLastName.setText(currentRequest.getAccountLastName());
        holder.accountType.setText(currentRequest.getAccountType());

        boolean wasRejected = currentRequest.getStatus();
        //boolean wasRejected = true;
        Log.d("SomeTag" , wasRejected?"true":"false");

        holder.seeMoreButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            if (wasRejected == false) {
                if ("Doctor".equals(holder.accountType.getText().toString())) {
                    Intent intent = new Intent(context, DoctorApproval.class);

                    // Pass user data intent to approval page
                    intent.putExtra("firstName", currentRequest.getAccountFirstName());
                    intent.putExtra("lastName", currentRequest.getAccountLastName());
                    intent.putExtra("employeeNumber", currentRequest.getAccountEmployeeNumber());
                    intent.putExtra("address", currentRequest.getAccountAddress());
                    intent.putExtra("phoneNumber", currentRequest.getAccountPhoneNumber());
                    intent.putExtra("email", currentRequest.getAccountEmail());
                    intent.putExtra("password", currentRequest.getAccountPassword());
                    intent.putExtra("uid", currentRequest.getAccountUID());
                    intent.putExtra("rejected", currentRequest.getStatus());
                    ArrayList<String> specialtiesList = currentRequest.getAccountSpecialties();
                    intent.putStringArrayListExtra("specialties", specialtiesList);
                    context.startActivity(intent);

                } else if ("Patient".equals(holder.accountType.getText().toString())) {
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
                    intent.putExtra("rejected", currentRequest.getStatus());
                    context.startActivity(intent);
                }
            }
            else {
                //CHANGE SO IT DISPLAYS THE APPROVAL ONLY PAGE
                if ("Doctor".equals(holder.accountType.getText().toString())){
                    Intent intent = new Intent(context, DoctorRejectApproval.class);
                    intent.putExtra("firstName", currentRequest.getAccountFirstName());
                    intent.putExtra("lastName", currentRequest.getAccountLastName());
                    intent.putExtra("employeeNumber", currentRequest.getAccountEmployeeNumber());
                    intent.putExtra("address", currentRequest.getAccountAddress());
                    intent.putExtra("phoneNumber", currentRequest.getAccountPhoneNumber());
                    intent.putExtra("email", currentRequest.getAccountEmail());
                    intent.putExtra("password", currentRequest.getAccountPassword());
                    intent.putExtra("uid", currentRequest.getAccountUID());
                    intent.putExtra("rejected", currentRequest.getStatus());
                    ArrayList<String> specialtiesList = currentRequest.getAccountSpecialties();
                    intent.putStringArrayListExtra("specialties", specialtiesList);

                    context.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(context, PatientRejectApproval.class);
                    intent.putExtra("firstName", currentRequest.getAccountFirstName());
                    intent.putExtra("lastName", currentRequest.getAccountLastName());
                    intent.putExtra("healthCardNumber", currentRequest.getAccountHealthCardNumber());
                    intent.putExtra("address", currentRequest.getAccountAddress());
                    intent.putExtra("phoneNumber", currentRequest.getAccountPhoneNumber());
                    intent.putExtra("email", currentRequest.getAccountEmail());
                    intent.putExtra("password", currentRequest.getAccountPassword());
                    intent.putExtra("uid", currentRequest.getAccountUID());
                    intent.putExtra("rejected", currentRequest.getStatus());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return accountApprovalList.size();
    }
}