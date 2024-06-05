package com.example.slfb.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.slfb.doctor.HelperClassD;
import com.example.slfb.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DoctorProfileFragment extends Fragment {
    private ImageView imageViewProfile;
    private TextView textViewName;
    private TextView textViewAddress;
    private TextView textViewPhone;
    private TextView textViewEmail;
    private TextView textViewAbout;
    private TextView textViewEducation;
    private TextView textViewExperience;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        textViewName = view.findViewById(R.id.detailDoctorName);
        textViewAddress = view.findViewById(R.id.detailDoctorAddress);
        textViewPhone = view.findViewById(R.id.detailDoctorPhoneNumber);
        textViewEmail = view.findViewById(R.id.detailDoctorEmail);
        textViewAbout = view.findViewById(R.id.detailAbout);
        textViewEducation = view.findViewById(R.id.detailEducation);
        textViewExperience = view.findViewById(R.id.detailExperience);
        imageViewProfile = view.findViewById(R.id.detailDoctorImage);

        // Get the selected doctor's name and ID from the fragment arguments
        String doctorName = getArguments().getString("doctorName");
        String doctorId = getArguments().getString("doctorId");

        // Retrieve doctor's information from Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("docs");
        databaseReference.orderByChild("name").equalTo(doctorName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HelperClassD helperClassD = snapshot.getValue(HelperClassD.class);
                    if (helperClassD != null) {
                        textViewName.setText(helperClassD.getName());
                        textViewAddress.setText(helperClassD.getAddress());
                        textViewEmail.setText(helperClassD.getEmail());
                        textViewPhone.setText(helperClassD.getPhone());
                        textViewAbout.setText(helperClassD.getAbout());
                        textViewEducation.setText(helperClassD.getEducation());
                        textViewExperience.setText(helperClassD.getExperience());
                        Picasso.get().load(helperClassD.getImage()).into(imageViewProfile);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        // Add an OnClickListener to the "Book Appointment" button
        Button bookAppointmentButton = view.findViewById(R.id.buttonBookAppointment);
        bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the BookAppointmentFragment
                navigateToBookAppointmentFragment(doctorName, doctorId);
            }
        });

        return view;
    }

    private void navigateToBookAppointmentFragment(String doctorName, String doctorId) {
        // Create an instance of BookAppointmentFragment
        BookAppointmentFragment bookAppointmentFragment = new BookAppointmentFragment();

        // Pass the doctor's name and ID to BookAppointmentFragment
        Bundle args = new Bundle();
        args.putString("doctorName", doctorName);
        args.putString("doctorId", doctorId);
        bookAppointmentFragment.setArguments(args);

        // Open the BookAppointmentFragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, bookAppointmentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
