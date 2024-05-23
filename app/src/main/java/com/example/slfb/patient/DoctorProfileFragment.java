package com.example.slfb.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.slfb.doctor.HelperClassD;
import com.example.slfb.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DoctorProfileFragment extends Fragment {
 ImageView imageViewProfile;
    private TextView textViewName;
    private TextView textViewAddress;
    private TextView textViewPhone;
    private TextView textViewEmail;
    private TextView textViewAbout;
    private TextView textViewEducation;
    private TextView textViewExperience;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        textViewName = view.findViewById(R.id.detailDoctorName);
        textViewAddress = view.findViewById(R.id.detailDoctorAddress);
        textViewPhone = view.findViewById(R.id.detailDoctorPhoneNumber);
        textViewEmail = view.findViewById(R.id.detailDoctorEmail);
        textViewAbout = view.findViewById(R.id.detailAbout);
        textViewEducation = view.findViewById(R.id.detailEducation);
        textViewExperience = view.findViewById(R.id.detailExperience);
        imageViewProfile=view.findViewById(R.id.detailDoctorImage);

        // Récupérer le nom du docteur sélectionné depuis les arguments du fragment
        String HelperClassDName = getArguments().getString("doctorName");
        String HelperClassDid = getArguments().getString("doctorId");

        // Récupérer les informations du docteur depuis Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("docs");
        databaseReference.orderByChild("name").equalTo(HelperClassDName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HelperClassD HelperClassD = snapshot.getValue(HelperClassD.class);
                    if (HelperClassD != null) {
                        textViewName.setText(HelperClassD.getName());
                        textViewAddress.setText(HelperClassD.getAddress());
                        textViewEmail.setText(HelperClassD.getEmail());
                        textViewPhone.setText(HelperClassD.getPhone());
                        textViewAbout.setText(HelperClassD.getAbout());
                        textViewEducation.setText(HelperClassD.getEducation());
                        textViewExperience.setText(HelperClassD.getExperience());
                        Picasso.get().load(HelperClassD.getImage()).into(imageViewProfile);


                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs
            }
        });

        // Ajouter un OnClickListener au bouton "Book Appointment"
        Button bookAppointmentButton = view.findViewById(R.id.buttonBookAppointment);
        bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviguer vers la page BookAppointmentFragment
                navigateToBookAppointmentFragment(HelperClassDName,HelperClassDid);
            }
        });

        return view;
    }

    private void navigateToBookAppointmentFragment(String doctorName,String doctorId) {
        // Créer une instance de BookAppointmentFragment
        BookAppointmentFragment bookAppointmentFragment = new BookAppointmentFragment();

        // Passer le nom du docteur à BookAppointmentFragment
        Bundle args = new Bundle();
        args.putString("doctorName", doctorName);
        args.putString("doctorId", doctorId);
        bookAppointmentFragment.setArguments(args);
        // Ouvrir le fragment BookAppointmentFragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, bookAppointmentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
