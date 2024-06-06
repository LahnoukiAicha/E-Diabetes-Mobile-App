package com.example.slfb.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.slfb.R;
import com.example.slfb.databinding.ActivityMainPageBinding;
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

public class MainPageActivity extends AppCompatActivity {
    ActivityMainPageBinding binding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final String CHANNEL_ID = "heads_up_notification_channel";
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_NOTIFIED = "notified";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar); // Find the Toolbar
        setSupportActionBar(toolbar); // Set Toolbar as ActionBar

        drawerLayout = findViewById(R.id.layDL);

        replaceFragment(new HomePFragment());

        // Ensure bottomNavigationView is initialized properly
        if (binding.bottomNavigationView == null) {
            throw new NullPointerException("bottomNavigationView is null. Check if the ID is correct in the layout file.");
        }

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                replaceFragment(new HomePFragment());
            } else if (itemId == R.id.bottom_doctor) {
                replaceFragment(new DoctorListFragment());
            } else if (itemId == R.id.bottom_chat) {
                replaceFragment(new ChatFragment());
            } else if (itemId == R.id.bottom_record) {
                replaceFragment(new RecordFragment());
            }
            return true;
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab == null) {
            throw new NullPointerException("FloatingActionButton is null. Check if the ID is correct in the layout file.");
        }

        fab.setOnClickListener(view -> replaceFragment(new TodayInfoFragment()));

        checkAppointments();

        // Ensure the ActionBar's home button is displayed
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            throw new NullPointerException("getSupportActionBar() returned null.");
        }

        // Navigation drawer setup
        drawerLayout = findViewById(R.id.layDL);
        if (drawerLayout == null) {
            throw new NullPointerException("DrawerLayout is null. Check if the ID is correct in the layout file.");
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Set up navigation view
        NavigationView navigationView = findViewById(R.id.vNV);
        if (navigationView == null) {
            throw new NullPointerException("NavigationView is null. Check if the ID is correct in the layout file.");
        }

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
                replaceFragment(new ProfileFragmentP());
                drawerLayout.closeDrawers();
                return true;
            } else if (itemId == R.id.row_Medical_History) {
                replaceFragment(new MedicalHistoryFragment());
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void checkAppointments() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String patientId = currentUser.getUid();
            DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

            // Query appointments for the current patient where accepted is true
            appointmentsRef.orderByChild("patientId").equalTo(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean hasAcceptedAppointments = false;
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Appointment appointment = snapshot.getValue(Appointment.class);
                            if (appointment != null && appointment.isAccepted()) {
                                hasAcceptedAppointments = true;
                                break;
                            }
                        }
                    }

                    if (hasAcceptedAppointments) {
                        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        boolean notified = preferences.getBoolean(KEY_NOTIFIED, false);
                        if (!notified) {
                            showHeadsUpNotification();
                            preferences.edit().putBoolean(KEY_NOTIFIED, true).apply();
                        }
                    } else {
                        // Reset the notified flag if there are no accepted appointments
                        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        preferences.edit().putBoolean(KEY_NOTIFIED, false).apply();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    private void showHeadsUpNotification() {
        String message = "Your appointment has been accepted";
        createNotificationChannel();
        Intent intent = new Intent(this, MainPageActivity.class);
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
        startActivity(new Intent(MainPageActivity.this, LoginActivityPatient.class));
        finish();
    }
}
