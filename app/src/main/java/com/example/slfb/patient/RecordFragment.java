package com.example.slfb.patient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.slfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecordFragment extends Fragment {

    private TextView patientInfoTextView;
    private TextView todayInfoTextView;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Button exportButton;

    private static final String TAG = "RecordFragment";
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        patientInfoTextView = view.findViewById(R.id.patientInfoTextView);
        todayInfoTextView = view.findViewById(R.id.todayInfoTextView);
        exportButton = view.findViewById(R.id.btnExportPdf);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String patientId = currentUser.getUid();
            loadPatientInfo(patientId);
            loadTodayInfo(patientId);
        }

        // Set up the export button click listener
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    exportToPdf();
                } else {
                    requestPermission();
                }
            }
        });

        return view;
    }

    private void loadPatientInfo(String patientId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("patients").child(patientId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HelperClassPatient patient = dataSnapshot.getValue(HelperClassPatient.class);
                if (patient != null) {
                    String patientInfo = "Nom: " + patient.getName() +
                            "\nEmail: " + patient.getEmail() +
                            "\nTéléphone: " + patient.getPhone() +
                            "\nÂge: " + patient.getAge() +
                            "\nTaille: " + patient.getHeight() +
                            "\nPoids: " + patient.getWeight() +
                            "\nSexe: " + patient.getSex() +
                            "\nDurée de sommeil: " + patient.getSleepDuration() +
                            "\nNiveau d'activité: " + patient.getActivityLevel() +
                            "\nDiabète depuis: " + patient.getDiabetesSince();
                    patientInfoTextView.setText(patientInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read patient info", databaseError.toException());
            }
        });
    }

    private void loadTodayInfo(String patientId) {
        DatabaseReference todayInfoRef = FirebaseDatabase.getInstance().getReference("today_info")
                .child("patients").child(patientId).child("todayInfo");

        todayInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TodayInfo todayInfo = snapshot.getValue(TodayInfo.class);
                    if (todayInfo != null) {
                        String todayInfoText = "Date: " + todayInfo.getDateTime() +
                                "\nInsuline: " + todayInfo.getInsulin() +
                                "\nGlucose: " + todayInfo.getGlucose() +
                                "\nMédicaments: " + todayInfo.getMedications() +
                                "\nNotes: " + todayInfo.getNotes();
                        todayInfoTextView.setText(todayInfoText);
                        break; // assuming you only want the latest entry
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read today's info", databaseError.toException());
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportToPdf();
            } else {
                Toast.makeText(getContext(), "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void exportToPdf() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        // Create a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        int x = 10, y = 25;

        // Draw the patient information
        String patientInfo = patientInfoTextView.getText().toString();
        for (String line : patientInfo.split("\n")) {
            canvas.drawText(line, x, y, paint);
            y += paint.descent() - paint.ascent();
        }

        // Move to next section
        y += 20;

        // Draw today's information
        String todayInfo = todayInfoTextView.getText().toString();
        for (String line : todayInfo.split("\n")) {
            canvas.drawText(line, x, y, paint);
            y += paint.descent() - paint.ascent();
        }

        pdfDocument.finishPage(page);

        // Save the document to a file
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(directoryPath, "DossierMedical.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(getContext(), "PDF Exporté: " + file.getPath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de l'exportation du PDF", e);
            Toast.makeText(getContext(), "Erreur lors de l'exportation du PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }
}
