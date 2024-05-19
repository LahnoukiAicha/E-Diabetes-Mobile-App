package com.example.slfb.doctor;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slfb.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.Objects;
import java.util.Random;

public class DataActivityD extends AppCompatActivity {

    // Fields declaration
    EditText about, address, experience, education;
    TextView loginRedirectText;
    Button signupButton,browse;
    Uri filePath;
    Bitmap bitmap;
    ImageView img;
    private static final int PICK_IMAGE_REQUEST = 1;


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
                        Intent intent = new Intent(DataActivityD.this, LoginActivityDoc.class);
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
                        String imgDoctor = uri.toString();

                        HelperClassD helperClassD = new HelperClassD(name, email, phone, password, aboutDoctor, addressDoctor, experienceDoctor, educationDoctor, imgDoctor);

                        // Save user data to database using user's UID as key
                        reference.child(userId).setValue(helperClassD);

                        // Display success message
                        Toast.makeText(DataActivityD.this, "You have inserted your data successfully!", Toast.LENGTH_SHORT).show();

                        // Redirect to login activity
                        Intent intent = new Intent(DataActivityD.this, LoginActivityDoc.class);
                        startActivity(intent);
                        finish(); // Finish the activity to prevent going back to the signup screen
                    }
                }));
    }
}
