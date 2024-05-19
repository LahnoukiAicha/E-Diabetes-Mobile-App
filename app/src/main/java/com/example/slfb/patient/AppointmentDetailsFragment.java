package com.example.slfb.patient;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.slfb.R;

public class AppointmentDetailsFragment extends Fragment {

    private String appointmentDate;
    private String appointmentTime;
    private String doctorName;
    private String patientName;

    public AppointmentDetailsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment_details, container, false);

        if (getArguments() != null) {
            appointmentDate = getArguments().getString("appointmentDate");
            appointmentTime = getArguments().getString("appointmentTime");
            doctorName = getArguments().getString("doctorName");
            patientName = getArguments().getString("patientName");
        }

        ((TextView) view.findViewById(R.id.appointment_date)).setText(appointmentDate);
        ((TextView) view.findViewById(R.id.appointment_time)).setText(appointmentTime);
        ((TextView) view.findViewById(R.id.doctor_name)).setText(doctorName);
        ((TextView) view.findViewById(R.id.patient_name)).setText(patientName);

        return view;
    }
}
