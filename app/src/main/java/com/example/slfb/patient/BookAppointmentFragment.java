package com.example.slfb.patient;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class BookAppointmentFragment extends Fragment {

    private CalendarView calendarView;
    private Button saveButton;
    private String selectedDate;
    private String selectedTime;
    private String doctorName;
    private String patientId;
    private String doctorId;
    private String patientName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            doctorName = getArguments().getString("doctorName");
            doctorId = getArguments().getString("doctorId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_appointment, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        saveButton = view.findViewById(R.id.bookButton);

        // Set the minimum date to today to disable past dates
        Calendar today = Calendar.getInstance();
        calendarView.setMinDate(today.getTimeInMillis());

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d - %d - %d", dayOfMonth, month + 1, year);
            ((EditText) view.findViewById(R.id.etDate)).setText(selectedDate);
        });

        saveButton.setOnClickListener(view12 -> {
            if (selectedDate != null && !selectedDate.isEmpty()) {
                selectedTime = ((EditText) view.findViewById(R.id.etTime)).getText().toString().trim();

                if (!selectedTime.isEmpty()) {
                    saveAppointment();
                } else {
                    Toast.makeText(getActivity(), "Please select a time.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please select a date.", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btnTime).setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    (view13, hourOfDay, minute) -> {
                        selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        ((EditText) view.findViewById(R.id.etTime)).setText(selectedTime);
                    }, currentHour, currentMinute, true);
            timePickerDialog.show();
        });

        return view;
    }

    private void saveAppointment() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            patientId = currentUser.getUid();
            DatabaseReference patientsRef = FirebaseDatabase.getInstance().getReference().child("patients").child(patientId);
            patientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        patientName = dataSnapshot.child("name").getValue(String.class);

                        checkAppointmentAvailability(selectedDate, selectedTime, doctorName, isAvailable -> {
                            if (isAvailable) {
                                saveAppointmentToFirebase(doctorId, selectedDate, selectedTime, doctorName, patientId, patientName);
                            } else {
                                showAppointmentNotAvailableDialog();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Patient data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAppointmentToFirebase(String doctorId, String date, String time, String doctorName, String patientId, String patientName) {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");
        String appointmentId = appointmentsRef.push().getKey();

        Appointment appointment = new Appointment(appointmentId, doctorId, date, time, doctorName, patientId, patientName, false);
        assert appointmentId != null;
        appointmentsRef.child(appointmentId).setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                    sendNotificationToDoctor(doctorId, date, time, patientName);
                    navigateToAppointmentDetailsFragment(date, time, doctorName, patientName);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to book appointment", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendNotificationToDoctor(String doctorId, String date, String time, String patientName) {
        DatabaseReference doctorsRef = FirebaseDatabase.getInstance().getReference().child("docs").child(doctorId);
        doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String doctorToken = dataSnapshot.child("fcmToken").getValue(String.class);
                    if (doctorToken != null) {
                        String serverKey = "AAAAUlK7nYs:APA91bGZsrIUBXu2L5jxVSAzx8EJ0lxrKaa1g39jMhO98CCWcqLS-_nAw4sSXXzRqRWP0BZZzKQODXR_0I6HSvI7LEqor_Df8HtZOjMNp0BwNfzV8ApUe5NgR6UFdBguYG34lXDmjakL"; // Replace with your actual server key

                        try {
                            URL url = new URL("https://fcm.googleapis.com/fcm/send");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setUseCaches(false);
                            conn.setDoInput(true);
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Authorization", "key=" + serverKey);
                            conn.setRequestProperty("Content-Type", "application/json");

                            String notificationMessage = "{"
                                    + "\"to\":\"" + doctorToken + "\","
                                    + "\"notification\":{"
                                    + "\"title\":\"New Appointment\","
                                    + "\"body\":\"You have a new appointment with " + patientName + " on " + date + " at " + time + "\","
                                    + "\"click_action\":\"OPEN_MY_APPOINTMENTS_FRAGMENT\"" // Add click action
                                    + "},"
                                    + "\"data\":{"
                                    + "\"fragment_to_open\":\"appointments\"" // Specify the fragment to open
                                    + "}"
                                    + "}";

                            new SendNotificationTask().execute(conn, notificationMessage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private static class SendNotificationTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            HttpURLConnection conn = (HttpURLConnection) params[0];
            String notificationMessage = (String) params[1];

            try {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(notificationMessage.getBytes());
                outputStream.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // Notification sent successfully
                } else {
                    // Notification failed
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }

            return null;
        }
    }

    private void navigateToAppointmentDetailsFragment(String date, String time, String doctorName, String patientName) {
        AppointmentDetailsFragment appointmentDetailsFragment = new AppointmentDetailsFragment();

        Bundle args = new Bundle();
        args.putString("appointmentDate", date);
        args.putString("appointmentTime", time);
        args.putString("doctorName", doctorName);
        args.putString("patientName", patientName);
        appointmentDetailsFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, appointmentDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void checkAppointmentAvailability(String selectedDate, String selectedTime, String doctorName, AvailabilityCallback callback) {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        appointmentsRef.orderByChild("date").equalTo(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isAvailable = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null && appointment.getTime().equals(selectedTime) && appointment.getDoctorName().equals(doctorName)) {
                        isAvailable = false;
                        break;
                    }
                }
                callback.onAvailabilityChecked(isAvailable);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                callback.onAvailabilityChecked(false);
            }
        });
    }

    interface AvailabilityCallback {
        void onAvailabilityChecked(boolean isAvailable);
    }

    private void showAppointmentNotAvailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Appointment Unavailable")
                .setMessage("This appointment is already booked.")
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_background);

        dialog.show();
    }
}
