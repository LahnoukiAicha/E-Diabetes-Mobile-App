package com.example.slfb.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfilePFragment extends Fragment {

    EditText editName, editEmail, editPhone, editAge, editHeight, editWeight, editSex, editSleepDuration, editActivityLevel, editDiabetes;
    Button saveButton;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile_p, container, false);

        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editPhone = view.findViewById(R.id.editPhone);
        editAge = view.findViewById(R.id.editAge);
        editHeight = view.findViewById(R.id.editHeight);
        editWeight = view.findViewById(R.id.editWeight);
        editSex = view.findViewById(R.id.editSex);
        editSleepDuration = view.findViewById(R.id.editSleepDuration);
        editActivityLevel = view.findViewById(R.id.editActivity);
        editDiabetes = view.findViewById(R.id.editDiabetes);
        saveButton = view.findViewById(R.id.saveButton);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve bundle data
        Bundle bundle = getArguments();
        if (bundle != null) {
            editName.setText(bundle.getString("name"));
            editEmail.setText(bundle.getString("email"));
            editPhone.setText(bundle.getString("phone"));
            editAge.setText(bundle.getString("age"));
            editHeight.setText(bundle.getString("height"));
            editWeight.setText(bundle.getString("weight"));
            editSex.setText(bundle.getString("sex"));
            editSleepDuration.setText(bundle.getString("sleepDuration"));
            editActivityLevel.setText(bundle.getString("activityLevel"));
            editDiabetes.setText(bundle.getString("diabetesSince"));
        }

        saveButton.setOnClickListener(v -> {
            updateUserProfile();
        });

        return view;
    }

    private void updateUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child(user.getUid());

            reference.child("name").setValue(editName.getText().toString());
            reference.child("email").setValue(editEmail.getText().toString());
            reference.child("phone").setValue(editPhone.getText().toString());
            reference.child("age").setValue(editAge.getText().toString());
            reference.child("height").setValue(editHeight.getText().toString());
            reference.child("weight").setValue(editWeight.getText().toString());
            reference.child("sex").setValue(editSex.getText().toString());
            reference.child("sleepDuration").setValue(editSleepDuration.getText().toString());
            reference.child("activityLevel").setValue(editActivityLevel.getText().toString());
            reference.child("diabetesSince").setValue(editDiabetes.getText().toString());

            // Update UI
            ProfileFragmentP profileFragment = new ProfileFragmentP();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, profileFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
