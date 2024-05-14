package com.example.slfb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slfb.doctor.SignupActivityDoc;
import com.example.slfb.patient.SignupActivityPatient;

public class MainActivity extends AppCompatActivity {
    Button b1  ;
    Button b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        b1 = findViewById(R.id.patient);
        b1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, SignupActivityPatient.class);
                        startActivity(i);
                    }
                }
        );

        b2 = findViewById(R.id.doctor);
        b2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, SignupActivityDoc.class);
                        startActivity(i);
                    }
                }
        );
    }
}