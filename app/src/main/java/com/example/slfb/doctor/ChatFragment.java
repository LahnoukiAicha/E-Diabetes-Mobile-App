package com.example.slfb.doctor;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slfb.Message;
import com.example.slfb.MessageAdapter;
import com.example.slfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatFragment extends Fragment {

    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList;
    private DatabaseReference messagesRef;
    private String currentUserId;
    private String patientId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_d, container, false);

        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView);

        // Retrieve the current user's ID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            fetchPatientId(currentUserId);
        } else {
            Toast.makeText(getActivity(), "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show();
        }

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageRecyclerView.setAdapter(messageAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("messages");

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                messageRecyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatFragment", "Failed to read messages", error.toException());
            }
        });

        return view;
    }

    private void fetchPatientId(String doctorId) {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        appointmentsRef.orderByChild("doctorId").equalTo(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        patientId = dataSnapshot.child("patientId").getValue(String.class);
                        if (patientId != null) {
                            break; // Assuming you only need one patientId
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatFragment", "Failed to fetch patient ID", error.toException());
            }
        });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(messageText) && patientId != null) {
            long timestamp = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTime = sdf.format(new Date(timestamp));
            Message message = new Message(messageText, currentUserId, patientId, formattedTime);
            messagesRef.push().setValue(message);
            editTextMessage.setText("");
        } else {
            Toast.makeText(getActivity(), "Failed to send message. Patient ID not found.", Toast.LENGTH_SHORT).show();
        }
    }
}
