package com.example.slfb.doctor;

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

public class EditProfileFragmentD extends Fragment {

    EditText editName, editEmail, editPhone, editAddress, editAbout, editEducation, editExperience;
    Button saveButton;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile_d, container, false);

        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editPhone = view.findViewById(R.id.editPhone);
        editAddress = view.findViewById(R.id.editAddress);
        editAbout = view.findViewById(R.id.editAbout);
        editEducation = view.findViewById(R.id.editEducation);
        editExperience = view.findViewById(R.id.editExperience);
        saveButton = view.findViewById(R.id.saveButton);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve bundle data
        Bundle bundle = getArguments();
        if (bundle != null) {
            editName.setText(bundle.getString("name"));
            editEmail.setText(bundle.getString("email"));
            editPhone.setText(bundle.getString("phone"));
            editAddress.setText(bundle.getString("address"));
            editAbout.setText(bundle.getString("about"));
            editEducation.setText(bundle.getString("education"));
            editExperience.setText(bundle.getString("experience"));
        }

        saveButton.setOnClickListener(v -> {
            updateUserProfile();
        });

        return view;
    }

    private void updateUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("docs").child(user.getUid());

            reference.child("name").setValue(editName.getText().toString());
            reference.child("email").setValue(editEmail.getText().toString());
            reference.child("phone").setValue(editPhone.getText().toString());
            reference.child("address").setValue(editAddress.getText().toString());
            reference.child("about").setValue(editAbout.getText().toString());
            reference.child("education").setValue(editEducation.getText().toString());
            reference.child("experience").setValue(editExperience.getText().toString());

            // Update UI
            ProfileFragmentD profileFragmentD = new ProfileFragmentD();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, profileFragmentD)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
