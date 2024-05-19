package com.example.slfb.doctor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.slfb.R;
import com.example.slfb.patient.Appointment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAppointmentsFragment extends Fragment {

    private ListView appointmentListView;
    private AppointmentListAdapter adapter;
    private ArrayList<Appointment> appointments;

    public MyAppointmentsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_appointments, container, false);

        appointmentListView = view.findViewById(R.id.appointment_list);
        appointments = new ArrayList<>();
        adapter = new AppointmentListAdapter(getContext(), R.layout.appointment_item, appointments);
        appointmentListView.setAdapter(adapter);


        loadAppointments();

        appointmentListView.setOnItemClickListener((parent, view1, position, id) -> {
            Appointment appointment = appointments.get(position);
            // Show dialog to accept or decline the appointment
            showAcceptDeclineDialog(appointment);
        });

        return view;
    }

    public void loadAppointments() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("appointments");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointments.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null && !appointment.isAccepted()) {
                        appointments.add(appointment);
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

    private void showAcceptDeclineDialog(Appointment appointment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Appointment with " + appointment.getPatientName())
                .setMessage("Date: " + appointment.getDate() + "\nTime: " + appointment.getTime())
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appointment.setAccepted(true);
                        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");
                        DatabaseReference appointmentRef = appointmentsRef.push();
                        String appointmentId = appointmentRef.getKey();
                        appointmentRef.setValue(appointment)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Appointment accepted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failed to accept appointment", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user declines the appointment, you can simply ignore this action here
                    }
                })
                .show();
    }
}
