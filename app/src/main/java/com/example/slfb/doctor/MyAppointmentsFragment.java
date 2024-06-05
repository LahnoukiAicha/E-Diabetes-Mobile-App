package com.example.slfb.doctor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slfb.R;
import com.example.slfb.patient.Appointment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAppointmentsFragment extends Fragment {

    private AppointmentListAdapter adapter;
    private ArrayList<Appointment> appointments;
    private String doctorId;
    private FirebaseAuth mAuth;

    public MyAppointmentsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_appointments, container, false);

        ListView appointmentListView = view.findViewById(R.id.appointment_list);
        appointments = new ArrayList<>();
        adapter = new AppointmentListAdapter(getContext(), R.layout.appointment_item, appointments);
        appointmentListView.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();

        loadAppointments();

        appointmentListView.setOnItemClickListener((parent, view1, position, id) -> {
            Appointment appointment = appointments.get(position);
            // Show dialog to accept or decline the appointment
            showAcceptDeclineDialog(appointment);
        });

        return view;
    }

    public void loadAppointments() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference doctReference = FirebaseDatabase.getInstance().getReference().child("docs").child(userId);
            doctReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    doctorId = dataSnapshot.getKey();
                    DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");
                    appointmentsRef.orderByChild("doctorId").equalTo(doctorId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            appointments.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Appointment appointment = snapshot.getValue(Appointment.class);
                                if (appointment != null && !appointment.isAccepted()) {
                                    appointment.setId(snapshot.getKey());
                                    appointments.add(appointment);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                            Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(getContext(), "Failed to load doctor data", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                        DatabaseReference appointmentRef = appointmentsRef.child(appointment.getId()); // Use the appointment ID to update the correct node
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
                        appointment.setAccepted(false);
                        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");
                        DatabaseReference appointmentRef = appointmentsRef.child(appointment.getId());
                        appointmentRef.setValue(appointment)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Appointment declined", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failed to decline appointment", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .show();
    }
}
