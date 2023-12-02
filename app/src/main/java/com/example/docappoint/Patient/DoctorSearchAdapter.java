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

    public DoctorSearchAdapter(ArrayList<DoctorChip> l){this.doctorList = l; this.doctorListFiltered =l;}

    @Override
    public DoctorSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_doctorselect, parent, false);
        return new DoctorSearchAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoctorChip currentDoctor = doctorListFiltered.get(position);

        holder.doctorFirstName.setText(currentDoctor.getAccountFirstName());
        holder.doctorLastName.setText(currentDoctor.getAccountLastName());
        holder.doctorSpecialties.setText(TextUtils.join(", ", currentDoctor.getSpecialties()));
        holder.doctorRatingNumber.setText(String.valueOf(currentDoctor.getRatingNumber()));
        holder.doctorNumRatings.setText(String.valueOf(currentDoctor.getNumberOfRatings()));
        holder.doctorNextAvailableDate.setText(currentDoctor.getNextAvailableDate());
        holder.doctorNextAvailableTime.setText(currentDoctor.getNextAvailableTime());

        holder.doctorRatingBar.setRating(currentDoctor.getRatingNumber());


        // holder.doctorProfilePicture.setImageBitmap(currentDoctor.getProfilePicture());

        holder.doctorBook.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();

            Intent intent = new Intent(context, BookAppointment.class);

            intent.putExtra("uid", currentDoctor.getUID());

            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return doctorListFiltered.size();
    }


    public void filter(String query) {
        ArrayList<DoctorChip> filteredList = new ArrayList<>();

        if (query != null && !query.isEmpty()) {
            query = query.toLowerCase().trim();

            for (DoctorChip doctor : doctorList) {
                String firstName = doctor.getAccountFirstName();
                String lastName = doctor.getAccountLastName();
                ArrayList<String> specialties = doctor.getSpecialties();

                // Check for null to avoid NullPointerException
                if ((firstName != null && firstName.toLowerCase().contains(query)) ||
                        (lastName != null && lastName.toLowerCase().contains(query)) ||
                        (specialties != null && containsSpecialty(specialties, query))) {
                    filteredList.add(doctor);
                }
            }
        } else {
            // Reset to the full list when query is empty
            filteredList.addAll(doctorList);
        }

        // Update the filtered list and notify the adapter
        doctorListFiltered = filteredList;
        notifyDataSetChanged();
    }


    // Helper method to check if the list of specialties contains the search query
    private boolean containsSpecialty(ArrayList<String> specialties, String query) {
        if (specialties != null) {
            for (String specialty : specialties) {
                if (specialty.toLowerCase().contains(query)) {
                    return true;
                }
            }
        }
        return false;
    }



}

