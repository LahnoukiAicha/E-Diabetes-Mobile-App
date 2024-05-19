package com.example.slfb.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.slfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TodayInfoFragment extends Fragment {

    EditText etInsulin, etGlucose, etMedications, etNotes;
    Button btnSave;
    TextView tvDateTime;


    // Firebase authentication and database reference
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    public TodayInfoFragment(){}
    public static TodayInfoFragment newInstance() {
        return new TodayInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_info, container, false);

        // Initialize Firebase authentication and database reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("today_info");

        // Initialize views
        tvDateTime = view.findViewById(R.id.tvDateTime);
        etInsulin = view.findViewById(R.id.etInsulin);
        etGlucose = view.findViewById(R.id.etGlucose);
        etMedications = view.findViewById(R.id.etMedications);
        etNotes = view.findViewById(R.id.etNotes);
        btnSave = view.findViewById(R.id.btnSave);

        // Display current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        tvDateTime.setText(currentDateTime);

        // Handle save button click
        btnSave.setOnClickListener(v -> saveData(currentDateTime));

        return view;
    }

    // Save data to Firebase and redirect to MedicalHistoryActivity
    private void saveData(String dateTime) {
        // Get user input
        String insulin = etInsulin.getText().toString();
        String glucose = etGlucose.getText().toString();
        String medications = etMedications.getText().toString();
        String notes = etNotes.getText().toString();

        // Check if any field is empty
        if (insulin.isEmpty() || glucose.isEmpty() || medications.isEmpty() || notes.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a TodayInfo object to save
        TodayInfo dataObject = new TodayInfo(dateTime, insulin, glucose, medications, notes);

        // Get the logged-in user's UID
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userUid = user.getUid();

            // Reference to the specific user's node in the database
            DatabaseReference userReference = databaseReference.child("patients").child(userUid).child("todayInfo");

            // Save data to the user's node
            userReference.push().setValue(dataObject)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            MedicalHistoryFragment medicalHistoryFragment = new MedicalHistoryFragment();
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, medicalHistoryFragment);
                            transaction.addToBackStack(null);  // Optional: Add transaction to back stack
                            transaction.commit();
                        } else {
                            Toast.makeText(getActivity(), "Failed to save data. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}