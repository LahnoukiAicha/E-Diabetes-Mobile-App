package com.example.slfb.doctor;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.slfb.R;
import com.example.slfb.patient.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class AcceptedAppointmentsFragment extends Fragment {

    private AppointmentListAdapter  adapter;
    private ArrayList<Appointment> acceptedAppointments;
    private String doctorId;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ValueEventListener appointmentsListener;

    public AcceptedAppointmentsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accepted_appointments, container, false);

        ListView acceptedAppointmentListView = view.findViewById(R.id.accepted_appointment_list);
        acceptedAppointments = new ArrayList<>();
        adapter = new AppointmentListAdapter(requireContext(), R.layout.appointment_item, acceptedAppointments);
        acceptedAppointmentListView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("appointments");

        loadAcceptedAppointments();

        return view;
    }

    private void loadAcceptedAppointments() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference docReference = FirebaseDatabase.getInstance().getReference().child("docs").child(userId);
            docReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    doctorId = dataSnapshot.getKey();
                    if (appointmentsListener != null) {
                        databaseReference.removeEventListener(appointmentsListener);
                    }
                    appointmentsListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            acceptedAppointments.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Appointment appointment = snapshot.getValue(Appointment.class);
                                if (appointment != null && appointment.isAccepted() && appointment.getDoctorId().equals(doctorId)) {
                                    appointment.setId(snapshot.getKey());
                                    acceptedAppointments.add(appointment);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Failed to load accepted appointments", Toast.LENGTH_SHORT).show();
                            Log.d("accepted appointment", "Failed to load accepted appointments");
                        }
                    };
                    databaseReference.orderByChild("accepted").equalTo(true).addValueEventListener(appointmentsListener);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load doctor data", Toast.LENGTH_SHORT).show();
                    Log.d("data doctor", "Failed to load doctor data");
                }
            });
        }
    }
}
