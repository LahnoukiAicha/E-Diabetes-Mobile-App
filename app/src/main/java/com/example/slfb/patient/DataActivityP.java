package com.example.slfb.patient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.slfb.R;
import com.example.slfb.doctor.DataActivityD;
import com.example.slfb.doctor.HelperClassD;
import com.example.slfb.doctor.LoginActivityDoc;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.Objects;
import java.util.Random;

public class DataActivityP extends AppCompatActivity {

    // Fields declaration
    EditText age, sex, height, weight, activityLevel, sleepDuration, diabetesSince;
    TextView loginRedirectText;
    Button signupButton,browse;

    // Database declaration
    FirebaseDatabase database;
    DatabaseReference reference;
    Uri filePath;
    Bitmap bitmap;
    ImageView img;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_p);

        // Initialize views
        age = findViewById(R.id.p_age);
        sex = findViewById(R.id.p_sex);
        height = findViewById(R.id.p_height);
        diabetesSince = findViewById(R.id.p_since);
        activityLevel = findViewById(R.id.p_activity);
        sleepDuration = findViewById(R.id.p_sleep);
        weight = findViewById(R.id.p_weight);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        img=findViewById(R.id.img);
        browse=findViewById(R.id.browse);
        signupButton = findViewById(R.id.signup_button);



        //brows button click listener
        browse.setOnClickListener(view -> browse.setOnClickListener(v -> {
            // Créer un intent pour sélectionner une image depuis la galerie
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }));

        // Signup Button click listener


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDb();
            }
        });


        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DataActivityP.this, LoginActivityPatient.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==1  && resultCode==RESULT_OK)
        {
            assert data != null;
            filePath=data.getData();
            try{
                InputStream inputStream=getContentResolver().openInputStream(filePath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(bitmap);
            }catch (Exception ex)
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void saveToDb(){
        //picture storing

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference uploader = storage.getReference("Image1" + new Random().nextInt(50));


        uploader.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        database = FirebaseDatabase.getInstance();
                        reference = database.getReference("patients");

                        // Retrieve user data from intent extras
                        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(); // Get the current user's UID
                        String name = getIntent().getStringExtra("name");
                        String email = getIntent().getStringExtra("email");
                        String phone = getIntent().getStringExtra("phone");
                        String password = getIntent().getStringExtra("password");

                        // Check if userId is not null
                        // Retrieve data from EditText fields
                        String ageUser = age.getText().toString();
                        String activityUser = activityLevel.getText().toString();
                        String sleepUser = sleepDuration.getText().toString();
                        String weightUser = weight.getText().toString();
                        String heightUser = height.getText().toString();
                        String sexUser = sex.getText().toString();
                        String diabetesUser = diabetesSince.getText().toString();
                        String imageUrl = uri.toString();


                        // Create HelperClassPatient instance
                        HelperClassPatient helperClassP = new HelperClassPatient(name, email, phone, password, ageUser, heightUser, weightUser, sexUser, sleepUser, activityUser, diabetesUser,imageUrl);

                        // Save user data to database using user's UID as key
                        reference.child(userId).setValue(helperClassP);

                        // Display success message
                        Toast.makeText(DataActivityP.this, "You have inserted your data successfully!", Toast.LENGTH_SHORT).show();

                        // Redirect to login activity
                        Intent intent = new Intent(DataActivityP.this, LoginActivityPatient.class);
                        startActivity(intent);
                        finish(); // Finish the activity to prevent going back to the signup screen
                    }
                }));
    }
}
