package com.example.slfb.doctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slfb.R;
import com.example.slfb.patient.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AppointmentListAdapter extends ArrayAdapter<Appointment> {

    private Context mContext;
    private int mResource;

    public AppointmentListAdapter(Context context, int resource, ArrayList<Appointment> appointments) {
        super(context, resource, appointments);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Appointment appointment = getItem(position);
        String patientName = appointment.getPatientName();
        String date = appointment.getDate();
        String time = appointment.getTime();
        boolean isAccepted = appointment.isAccepted();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvPatientName = convertView.findViewById(R.id.tv_patient_name);
        TextView tvDate = convertView.findViewById(R.id.tv_date);
        TextView tvTime = convertView.findViewById(R.id.tv_time);
        TextView tvStatus = convertView.findViewById(R.id.tv_status);
        TextView tvAccept = convertView.findViewById(R.id.tv_accept);
        TextView tvDecline = convertView.findViewById(R.id.tv_decline);

        tvPatientName.setText(patientName);
        tvDate.setText(date);
        tvTime.setText(time);
        tvStatus.setText(isAccepted ? "Accepted" : "Pending");

        tvAccept.setOnClickListener(v -> {
            appointment.setAccepted(true);
            updateAppointmentStatus(appointment);
        });

        tvDecline.setOnClickListener(v -> {
            appointment.setAccepted(false);
            updateAppointmentStatus(appointment);
        });

        return convertView;
    }

    private void updateAppointmentStatus(Appointment appointment) {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");
        DatabaseReference appointmentRef = appointmentsRef.child(appointment.getId());
        appointmentRef.setValue(appointment)
                .addOnSuccessListener(aVoid -> Toast.makeText(mContext, "Appointment updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(mContext, "Failed to update appointment", Toast.LENGTH_SHORT).show());
    }
}
