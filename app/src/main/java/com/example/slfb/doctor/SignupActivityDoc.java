package com.example.slfb.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slfb.HelperClass;
import com.example.slfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivityDoc extends AppCompatActivity {

    EditText signupName, signupPhone, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_doc);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPhone = findViewById(R.id.signup_phone);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("docs"); // Reference to the "docs" node

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = signupName.getText().toString().trim();
                final String email = signupEmail.getText().toString().trim();
                final String phone = signupPhone.getText().toString().trim();
                final String password = signupPassword.getText().toString();

                // Validate input fields
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivityDoc.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create user with email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivityDoc.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Save user data to database using user's UID as key
                                    String userId = user.getUid();
                                    HelperClass helperClass = new HelperClass(name, email, phone, password);
                                    reference.child(userId).setValue(helperClass);

                                    Toast.makeText(SignupActivityDoc.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                                    // Redirect to Doctor's Dashboard or any other relevant activity
                                    Intent intent = new Intent(SignupActivityDoc.this, DataActivityD.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("email", email);
                                    intent.putExtra("phone", phone);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(SignupActivityDoc.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivityDoc.this, LoginActivityDoc.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
