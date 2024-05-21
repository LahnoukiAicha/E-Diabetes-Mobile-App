package com.example.slfb.patient;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slfb.doctor.HelperClassD;
import com.example.slfb.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DoctorListFragment extends Fragment {
    private RecyclerView recyclerView;
    private HelperClassDAdapter adapter;
    private List<HelperClassD> helperClassDList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        helperClassDList = new ArrayList<>();

        // Retrieve data from Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("docs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                helperClassDList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("snapchot", String.valueOf(snapshot));
                    HelperClassD helperClassD = snapshot.getValue(HelperClassD.class);
                    assert helperClassD != null;
                    helperClassD.setId(snapshot.getKey());
                    helperClassDList.add(helperClassD);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        adapter = new HelperClassDAdapter(requireContext(), helperClassDList, new HelperClassDAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HelperClassD helperClassD) {
                Fragment doctorProfileFragment = new DoctorProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("doctorName", helperClassD.getName());
                bundle.putString("doctorId", helperClassD.getId());
                doctorProfileFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, doctorProfileFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
