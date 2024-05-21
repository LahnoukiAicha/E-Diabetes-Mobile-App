package com.example.slfb.doctor;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.slfb.R;
import com.example.slfb.patient.Appointment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;

public class AcceptedAppointmentsFragment extends Fragment {

    private ArrayAdapter<Appointment> adapter;
    private ArrayList<Appointment> acceptedAppointments;

    public AcceptedAppointmentsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accepted_appointments, container, false);

        ListView acceptedAppointmentListView = view.findViewById(R.id.accepted_appointment_list);
        acceptedAppointments = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, acceptedAppointments);
        acceptedAppointmentListView.setAdapter(adapter);

        loadAcceptedAppointments();

        return view;
    }

    private void loadAcceptedAppointments() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("appointments");
        databaseReference.orderByChild("accepted").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                acceptedAppointments.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null &&  appointment.isAccepted()) {
                        acceptedAppointments.add(appointment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
