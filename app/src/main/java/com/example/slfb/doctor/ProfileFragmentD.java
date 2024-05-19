package com.example.slfb.doctor;

import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.example.slfb.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragmentD extends Fragment {

    TextView profileName, profileEmail, profilePhone, profileAddress, profileAbout, profileEducation, profileExperience;
    Button editProfile;
    ImageView img;
    private FirebaseAuth mauth;

    private String name, email, phone, address, about, education, experience, password,imageUrl;

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
        img=view.findViewById(R.id.img);
        editProfile = view.findViewById(R.id.editButton);
        mauth = FirebaseAuth.getInstance();


        // Retrieve user data once
        retrieveUserData();

        editProfile.setOnClickListener(v -> {
            passUserData();
        });

        return view;
    }

    // Method to retrieve user data from Firebase
    private void retrieveUserData() {
        FirebaseUser user = mauth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("docs").child(userId);
            DatabaseReference getImage = reference.child("image");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        name = snapshot.child("name").getValue(String.class);
                        email = snapshot.child("email").getValue(String.class);
                        phone = snapshot.child("phone").getValue(String.class);
                        address = snapshot.child("address").getValue(String.class);
                        about = snapshot.child("about").getValue(String.class);
                        education = snapshot.child("education").getValue(String.class);
                        experience = snapshot.child("experience").getValue(String.class);
                        password = snapshot.child("password").getValue(String.class);
                        imageUrl = snapshot.child("image").getValue(String.class);


                        // Set the retrieved data to the TextViews
                        profileName.setText(name);
                        profileEmail.setText(email);
                        profilePhone.setText(phone);
                        profileAddress.setText(address);
                        profileAbout.setText(about);
                        profileEducation.setText(education);
                        profileExperience.setText(experience);
                        Picasso.get().load(imageUrl).into(img);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors
                    Log.e("ProfileFragmentD", "Failed to read value.", error.toException());
                }
            });
        }
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
