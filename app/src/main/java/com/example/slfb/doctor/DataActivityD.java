package com.example.slfb.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class DataActivityD extends AppCompatActivity {

    // Fields declaration
    EditText about, address, experience, education;
    TextView loginRedirectText;
    Button signupButton,selectImageButton;


    // Database declaration
    FirebaseDatabase database;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_d);

        // Initialize views
        about = findViewById(R.id.d_about);
        address = findViewById(R.id.d_address);
        experience = findViewById(R.id.d_experience);
        education = findViewById(R.id.d_education);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        // Button click listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("docs");

                // Retrieve user data from intent extras
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(); // Get the current user's UID
                String name = getIntent().getStringExtra("name");
                String email = getIntent().getStringExtra("email");
                String phone = getIntent().getStringExtra("phone");
                String password = getIntent().getStringExtra("password");

                // Retrieve data from EditText fields
                String aboutDoctor = about.getText().toString();
                String addressDoctor = address.getText().toString();
                String experienceDoctor = experience.getText().toString();
                String educationDoctor = education.getText().toString();

                // Create HelperClassDoctor instance
                HelperClassD helperClassD = new HelperClassD(name, email, phone, password, aboutDoctor, addressDoctor, experienceDoctor, educationDoctor);

                // Save user data to database using user's UID as key
                reference.child(userId).setValue(helperClassD);

                // Display success message
                Toast.makeText(DataActivityD.this, "You have inserted your data successfully!", Toast.LENGTH_SHORT).show();

                // Redirect to login activity
                Intent intent = new Intent(DataActivityD.this, LoginActivityDoc.class);
                startActivity(intent);
                finish(); // Finish the activity to prevent going back to the signup screen
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DataActivityD.this, LoginActivityDoc.class);
                startActivity(intent);
            }
        });
    }
}
