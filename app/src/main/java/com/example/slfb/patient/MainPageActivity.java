package com.example.slfb.patient;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.slfb.R;
import com.example.slfb.databinding.ActivityMainPageBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainPageActivity extends AppCompatActivity {
    ActivityMainPageBinding binding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomePFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                replaceFragment(new HomePFragment());
            } else if (itemId == R.id.bottom_record) {
                replaceFragment(new RecordFragment());
            } else if (itemId == R.id.bottom_doctor) {
                replaceFragment(new MyDoctorFragment());
            } else if (itemId == R.id.bottom_chat) {
                replaceFragment(new ChatFragment());
            }
            return true;
        });

        // Ensure the ActionBar's home button is displayed
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Navigation drawer setup
        drawerLayout = findViewById(R.id.layDL);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Set up navigation view
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
                replaceFragment(new ProfileFragmentP());
                drawerLayout.closeDrawers();
                return true;
            } else if (itemId == R.id.row_logout) {
                Toast.makeText(MainPageActivity.this, "Logout", Toast.LENGTH_SHORT).show();
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

}