package com.example.slfb.patient;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedicalHistoryFragment extends Fragment {

    private TextView tvHistoryData;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_history, container, false);

        // Initialize the TextView for displaying data
        tvHistoryData = view.findViewById(R.id.tvHistoryData);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("today_info");

        // Get the logged-in user's UID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userUid = user.getUid();

            // Reference to the specific user's node in the database
            DatabaseReference userReference = databaseReference.child("patients").child(userUid).child("todayInfo");

            // Add a single value event listener to the user's node
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Use a StringBuilder to construct the history data
                    StringBuilder historyData = new StringBuilder();

                    // Iterate through each child node in the snapshot
                    for (DataSnapshot dataObjectSnapshot : dataSnapshot.getChildren()) {
                        TodayInfo dataObject = dataObjectSnapshot.getValue(TodayInfo.class);
                        if (dataObject != null) {
                            // Append data for each TodayInfo object
                            historyData.append("Date and Time: ").append(dataObject.getDateTime())
                                    .append("\nInsulin: ").append(dataObject.getInsulin())
                                    .append("\nGlucose: ").append(dataObject.getGlucose())
                                    .append("\nMedications: ").append(dataObject.getMedications())
                                    .append("\nNotes: ").append(dataObject.getNotes())
                                    .append("\n\n");
                        }
                    }

                    // Update the TextView with the formatted history data
                    tvHistoryData.setText(historyData.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Log the error if reading from database is cancelled
                    Log.e("MedicalHistoryFragment", "onCancelled: Error reading from database", databaseError.toException());
                }
            });
        } else {
            // Log if the user is null (not signed in)
            Log.d("MedicalHistoryFragment", "User is null");
        }

        return view;
    }
}