package com.example.docappoint.Patient;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docappoint.R;
import com.example.docappoint.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorSearchAdapter extends RecyclerView.Adapter<DoctorSearchAdapter.ViewHolder> {
    private ArrayList<DoctorChip> doctorList;
    private ArrayList<DoctorChip> doctorListFiltered;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView doctorFirstName, doctorLastName, doctorSpecialties, doctorRatingNumber, doctorNumRatings, doctorNextAvailableTime, doctorNextAvailableDate;
        public ImageView doctorProfilePicture;
        public RatingBar doctorRatingBar;
        public Button doctorBook;

        public ViewHolder(View view){
            super(view);
            doctorFirstName = view.findViewById(R.id.firstNameDoctorSelectRecycle);
            doctorLastName = view.findViewById(R.id.lastNameDoctorRecycle);
            doctorSpecialties = view.findViewById(R.id.doctorSpecialtiesRecycler);
            doctorBook = view.findViewById(R.id.doctorBookAppointmentButtonRecycle);
            doctorRatingNumber = view.findViewById(R.id.numberRatingTextRecycler);
            doctorNumRatings = view.findViewById(R.id.numRatingsRecycler);
            doctorNextAvailableTime = view.findViewById(R.id.nextAvailableTimeRecycle);
            doctorNextAvailableDate = view.findViewById(R.id.nextAvailableDateRecycle);
            doctorProfilePicture = view.findViewById(R.id.doctorProfilePicRecycler);
            doctorRatingBar = view.findViewById(R.id.ratingBarRecycle);
        }
    }

    public DoctorSearchAdapter(ArrayList<DoctorChip> l){this.doctorList = l; this.doctorListFiltered = new ArrayList<>(l);}

    @Override
    public DoctorSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_doctorselect, parent, false);
        return new DoctorSearchAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoctorChip currentRequest = doctorListFiltered.get(position);
        ArrayList<String> specialties = currentRequest.getSpecialties();
        String specialtiesText = (specialties != null) ? TextUtils.join(", ", specialties) : "No Specialties";

        holder.doctorFirstName.setText(currentRequest.getAccountFirstName());
        holder.doctorLastName.setText(currentRequest.getAccountLastName());
        holder.doctorSpecialties.setText(specialtiesText);
        holder.doctorRatingNumber.setText(String.valueOf(currentRequest.getRatingNumber()));
        holder.doctorNumRatings.setText(String.valueOf(currentRequest.getNumberOfRatings()));
        holder.doctorNextAvailableDate.setText(currentRequest.getNextAvailableDate());
        holder.doctorNextAvailableTime.setText(currentRequest.getNextAvailableTime());

        holder.doctorBook.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, BookAppointment.class);

            // Initialize Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Get user data from firestore
            db.collection("Users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    // PendingUsers data extraction
                                    String firstName = document.getString("First Name");
                                    String lastName = document.getString("Last Name");

                                    // Check if user type is doctor or patient and assign it to userType variable
                                    int isDoctor = document.getLong("isDoctor") != null ? Math.toIntExact(document.getLong("isDoctor")) : 0;
                                    int isPatient = document.getLong("isPatient") != null ? Math.toIntExact(document.getLong("isPatient")) : 0;

                                    if (isDoctor == 1 && firstName.equals(currentRequest.getAccountFirstName()) && lastName.equals(currentRequest.getAccountLastName())) {
                                        String uid = document.getString("UID");
                                        intent.putExtra("uid", uid);
                                        context.startActivity(intent);
                                    }
                                }
                            }
                        }
                    });

            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return doctorListFiltered.size();
    }


    // Add a method to filter the list based on the search query
    public void filter(String query) {
        // Start with an empty list
        ArrayList<DoctorChip> filteredList = new ArrayList<>();

        // If search query is not empty, perform filtering
        if (!query.isEmpty()) {
            query = query.toLowerCase().trim();

            for (DoctorChip doctor : doctorList) {
                // Your existing logic to add matching doctors to filteredList
            }
        } else {
            // If search query is empty, use the original list
            filteredList.addAll(doctorList);
        }

        // Update the filtered list
        doctorListFiltered = filteredList;
        notifyDataSetChanged();
    }

}

