package com.example.docappoint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<ListRequest> accountApprovalList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView accountFirstName, accountLastName, accountType;
        public Button seeMoreButton;

        public ViewHolder(View view){
            super(view);
            accountFirstName = view.findViewById(R.id.firstNameRecycle);
            accountLastName = view.findViewById(R.id.lastNameRecycle);
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

        holder.seeMoreButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            if (wasRejected == false){
                if ("Doctor".equals(holder.accountType.getText().toString())){
                    Intent intent = new Intent(context, DoctorApproval.class);
                    context.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(context, PatientApproval.class);
                    context.startActivity(intent);
                }
            }
            else {
                //CHANGE SO IT DISPLAYS THE APPROVAL ONLY PAGE
                if ("Doctor".equals(holder.accountType.getText().toString())){
                    Intent intent = new Intent(context, DoctorApproval.class);
                    context.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(context, PatientApproval.class);
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
