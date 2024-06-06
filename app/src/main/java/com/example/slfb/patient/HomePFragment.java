package com.example.slfb.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.slfb.R;

public class HomePFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_p, container, false);

        // Get the container where you want to add the MedicalHistoryFragment
        LinearLayout containerLayout = view.findViewById(R.id.frame_layout);

        // Create an instance of MedicalHistoryFragment with the flag to show only the graph
        MedicalHistoryFragment medicalHistoryFragment = MedicalHistoryFragment.newInstance(true);

        // Add the MedicalHistoryFragment to the container
        getChildFragmentManager().beginTransaction()
                .add(containerLayout.getId(), medicalHistoryFragment)
                .commit();

        return view;
    }
}