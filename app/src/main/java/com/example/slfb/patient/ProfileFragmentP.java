package com.example.slfb.patient;

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

import com.example.slfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragmentP extends Fragment {

    TextView profileName, profileEmail, profilePhone, profileHeight, profileWeight, profileSex, profileSleepDuration, profileDiabetesSince, profileAge, profileActivityLevel;
    Button editProfile;
    ImageView img;
    private FirebaseAuth mauth;

    private String name, email, phone, height, weight, sex, sleepDuration, diabetesSince, age, activityLevel,imageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_p, container, false);

        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePhone = view.findViewById(R.id.profilePhone);
        profileHeight = view.findViewById(R.id.profileHeight);
        profileWeight = view.findViewById(R.id.profileWeight);
        profileSex = view.findViewById(R.id.profileSex);
        profileSleepDuration = view.findViewById(R.id.profileSleepDuration);
        profileDiabetesSince = view.findViewById(R.id.profileDiabetesSince);
        profileAge = view.findViewById(R.id.profileAge);
        profileActivityLevel = view.findViewById(R.id.profileActivityLevel);
        img=view.findViewById(R.id.img);
        editProfile = view.findViewById(R.id.editButton);

        // Initialize FirebaseAuth
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
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child(userId);
            DatabaseReference getImage = reference.child("image");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        name = snapshot.child("name").getValue(String.class);
                        email = snapshot.child("email").getValue(String.class);
                        phone = snapshot.child("phone").getValue(String.class);
                        height = snapshot.child("height").getValue(String.class);
                        weight = snapshot.child("weight").getValue(String.class);
                        sex = snapshot.child("sex").getValue(String.class);
                        sleepDuration = snapshot.child("sleepDuration").getValue(String.class);
                        diabetesSince = snapshot.child("diabetesSince").getValue(String.class);
                        age = snapshot.child("age").getValue(String.class);
                        activityLevel = snapshot.child("activityLevel").getValue(String.class);
                        imageUrl = snapshot.child("image").getValue(String.class);


                        // Set the retrieved data to the TextViews
                        profileName.setText(name);
                        profileEmail.setText(email);
                        profilePhone.setText(phone);
                        profileHeight.setText(height);
                        profileWeight.setText(weight);
                        profileSex.setText(sex);
                        profileSleepDuration.setText(sleepDuration);
                        profileDiabetesSince.setText(diabetesSince);
                        profileAge.setText(age);
                        profileActivityLevel.setText(activityLevel);
                        Picasso.get().load(imageUrl).into(img);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors
                    Log.e("ProfileFragment", "Failed to read value.", error.toException());
                }
            });
        }
    }

    public void passUserData() {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("email", email);
        bundle.putString("phone", phone);
        bundle.putString("height", height);
        bundle.putString("weight", weight);
        bundle.putString("sex", sex);
        bundle.putString("sleepDuration", sleepDuration);
        bundle.putString("diabetesSince", diabetesSince);
        bundle.putString("age", age);
        bundle.putString("activityLevel", activityLevel);

        EditProfilePFragment editProfileFragment = new EditProfilePFragment();
        editProfileFragment.setArguments(bundle);

        // Navigate to EditProfilePFragment
        assert getActivity() != null;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, editProfileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
