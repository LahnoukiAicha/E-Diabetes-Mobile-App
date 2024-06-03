package com.example.slfb.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.slfb.R;
import com.example.slfb.databinding.ActivityMainPageAcitivityDBinding;
import com.example.slfb.patient.Appointment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainPageActivityD extends AppCompatActivity {
    private ActivityMainPageAcitivityDBinding binding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final String CHANNEL_ID = "heads_up_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageAcitivityDBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomePFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                replaceFragment(new HomePFragment());
            } else if (itemId == R.id.bottom_record) {
                replaceFragment(new AcceptedAppointmentsFragment());
            } else if (itemId == R.id.bottom_patient) {
                replaceFragment(new MyPatientFragment());
            } else if (itemId == R.id.bottom_chat) {
                replaceFragment(new ChatFragment());
            }
            return true;
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> replaceFragment(new MyAppointmentsFragment()));

        setupNavigationDrawer();

        checkAppointments();
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.layDL);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.vNV);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.row_home) {
                replaceFragment(new HomePFragment());
                drawerLayout.closeDrawers();
                return true;
            } else if (itemId == R.id.row_settings) {
                replaceFragment(new SettingsPFragment());
                drawerLayout.closeDrawers();
                return true;
            } else if (itemId == R.id.row_profile) {
                replaceFragment(new ProfileFragmentD());
                drawerLayout.closeDrawers();
                return true;
            } else if (itemId == R.id.row_logout) {
                logout();
                drawerLayout.closeDrawers();
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAppointments() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String doctorId = currentUser.getUid();
            DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

            // Query appointments for the current doctor where accepted is false
            appointmentsRef.orderByChild("doctorId").equalTo(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean hasNewAppointments = false;
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Appointment appointment = snapshot.getValue(Appointment.class);
                            if (appointment != null && !appointment.isAccepted()) {
                                hasNewAppointments = true;
                                break;
                            }
                        }
                    }

                    // Show the notification only if there are new appointments
                    showHeadsUpNotification(hasNewAppointments);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    private void showHeadsUpNotification(boolean hasNewAppointments) {
        String message;
        if (hasNewAppointments) {
            message = "You have new appointments! check your notification ";
        } else {
            message = "You have no appointments.";
        }

        createNotificationChannel();
        Intent intent =  new Intent(this, MainPageActivityD.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Ediabetes Notification")
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(0, notification);
    }


    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainPageActivityD.this, LoginActivityDoc.class));
        finish();
    }
}
