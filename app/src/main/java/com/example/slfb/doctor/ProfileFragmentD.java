package com.example.slfb.doctor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.slfb.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragmentD extends Fragment {

    TextView profileName, profileEmail, profilePhone, profileAddress, profileAbout, profileEducation, profileExperience;
    Button editProfile;

    private String name, email, phone, address, about, education, experience, password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_d, container, false);

        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePhone = view.findViewById(R.id.profilePhone);
        profileAddress = view.findViewById(R.id.profileAddress);
        profileAbout = view.findViewById(R.id.profileAbout);
        profileEducation = view.findViewById(R.id.profileEducation);
        profileExperience = view.findViewById(R.id.profileExperience);
        editProfile = view.findViewById(R.id.editButton);

        // Retrieve user data once
        retrieveUserData();

        editProfile.setOnClickListener(v -> {
            passUserData();
        });

        return view;
    }

    // Method to retrieve user data from Firebase
    private void retrieveUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("docs");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming there's only one doctor node for simplicity, you might need to adjust this logic if there are multiple doctors
                    DataSnapshot doctorSnapshot = snapshot.getChildren().iterator().next();

                    name = doctorSnapshot.child("name").getValue(String.class);
                    email = doctorSnapshot.child("email").getValue(String.class);
                    phone = doctorSnapshot.child("phone").getValue(String.class);
                    address = doctorSnapshot.child("address").getValue(String.class);
                    about = doctorSnapshot.child("about").getValue(String.class);
                    education = doctorSnapshot.child("education").getValue(String.class);
                    experience = doctorSnapshot.child("experience").getValue(String.class);
                    password = doctorSnapshot.child("password").getValue(String.class);

                    // Set the retrieved data to the TextViews
                    profileName.setText(name);
                    profileEmail.setText(email);
                    profilePhone.setText(phone);
                    profileAddress.setText(address);
                    profileAbout.setText(about);
                    profileEducation.setText(education);
                    profileExperience.setText(experience);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
                Log.e("ProfileFragmentD", "Failed to read value.", error.toException());
            }
        });
    }

    // Method to pass user data to EditProfileFragmentD
    public void passUserData() {

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("email", email);
        bundle.putString("phone", phone);
        bundle.putString("address", address);
        bundle.putString("about", about);
        bundle.putString("education", education);
        bundle.putString("experience", experience);
        bundle.putString("password", password);

        EditProfileFragmentD editProfileFragmentD = new EditProfileFragmentD();
        editProfileFragmentD.setArguments(bundle);

        // Navigate to EditProfileFragmentD
        assert getActivity() != null;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, editProfileFragmentD);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}