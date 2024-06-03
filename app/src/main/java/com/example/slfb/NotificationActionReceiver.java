package com.example.slfb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String appointmentId = intent.getStringExtra("appointmentId");
        String doctorId = intent.getStringExtra("doctorId");

        if ("ACTION_ACCEPT".equals(action)) {
            acceptAppointment(context, appointmentId);
        } else if ("ACTION_DECLINE".equals(action)) {
            declineAppointment(context, appointmentId);
        }
    }

    private void acceptAppointment(Context context, String appointmentId) {
        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference().child("appointments").child(appointmentId);
        appointmentRef.child("accepted").setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Appointment accepted", Toast.LENGTH_SHORT).show();
                    // Optionally send a notification to the patient
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to accept appointment", Toast.LENGTH_SHORT).show();
                    Log.e("NotificationAction", "Failed to accept appointment", e);
                });
    }

    private void declineAppointment(Context context, String appointmentId) {
        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference().child("appointments").child(appointmentId);
        appointmentRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Appointment declined", Toast.LENGTH_SHORT).show();
                    // Optionally send a notification to the patient
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to decline appointment", Toast.LENGTH_SHORT).show();
                    Log.e("NotificationAction", "Failed to decline appointment", e);
                });
    }
}
