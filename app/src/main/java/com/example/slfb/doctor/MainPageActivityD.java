package com.example.slfb.doctor;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.slfb.R;
import com.example.slfb.databinding.ActivityMainPageAcitivityDBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainPageActivityD extends AppCompatActivity {
    ActivityMainPageAcitivityDBinding binding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

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
            }
            if (itemId == R.id.bottom_record) {
                replaceFragment(new MyAppointmentsFragment());
            }
            if (itemId == R.id.bottom_patient) {
                replaceFragment(new MyPatientFragment());
            }
            if (itemId == R.id.bottom_chat) {
                replaceFragment(new ChatFragment());
            }

            return true;

        });
        //navigation drawer

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
            } else if (itemId == R.id.row_logout) {
                Toast.makeText(MainPageActivityD.this, "Logout", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                return true;
            }
            else if (itemId == R.id.row_profile) {
                replaceFragment(new ProfileFragmentD());
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

}