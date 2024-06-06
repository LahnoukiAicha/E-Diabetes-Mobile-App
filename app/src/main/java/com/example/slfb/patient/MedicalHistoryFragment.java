package com.example.slfb.patient;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.slfb.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MedicalHistoryFragment extends Fragment {

    private static final String ARG_SHOW_GRAPH_ONLY = "show_graph_only";

    private boolean showGraphOnly;
    private LinearLayout llHistoryData;
    private LineChart lineChart;
    private DatabaseReference databaseReference;

    public static MedicalHistoryFragment newInstance(boolean showGraphOnly) {
        MedicalHistoryFragment fragment = new MedicalHistoryFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_GRAPH_ONLY, showGraphOnly);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_history, container, false);

        llHistoryData = view.findViewById(R.id.llHistoryData);
        lineChart = view.findViewById(R.id.lineChart);

        if (getArguments() != null) {
            showGraphOnly = getArguments().getBoolean(ARG_SHOW_GRAPH_ONLY, false);
        }

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("today_info");

        // Get the logged-in user's UID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userUid = user.getUid();

            // Reference to the specific user's node in the database
            DatabaseReference userReference = databaseReference.child("patients").child(userUid).child("todayInfo");

            // Add a single value event listener to the user's node
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Entry> insulinEntries = new ArrayList<>();
                    List<Entry> glucoseEntries = new ArrayList<>();

                    // Iterate through each child node in the snapshot
                    for (DataSnapshot dataObjectSnapshot : dataSnapshot.getChildren()) {
                        TodayInfo dataObject = dataObjectSnapshot.getValue(TodayInfo.class);
                        if (dataObject != null) {
                            if (!showGraphOnly) {
                                addDataCard(dataObject);
                            }

                            // Assuming dateTime is in the format "yyyy-MM-dd HH:mm:ss" and parsing it to get x-axis values
                            int xValue = (int) parseDateTimeToXValue(dataObject.getDateTime());
                            insulinEntries.add(new Entry(xValue, Float.parseFloat(dataObject.getInsulin())));
                            glucoseEntries.add(new Entry(xValue, Float.parseFloat(dataObject.getGlucose())));
                        }
                    }

                    // Set data to the line chart
                    setLineChartData(insulinEntries, glucoseEntries);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Log the error if reading from database is cancelled
                    Log.e("MedicalHistoryFragment", "onCancelled: Error reading from database", databaseError.toException());
                }
            });
        } else {
            // Log if the user is null (not signed in)
            Log.d("MedicalHistoryFragment", "User is null");
        }

        return view;
    }

    private void addDataCard(TodayInfo dataObject) {
        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 16, 16, 16);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(8);
        cardView.setCardElevation(4);
        cardView.setContentPadding(16, 16, 16, 16);
        cardView.setBackgroundColor(Color.WHITE);

        LinearLayout cardContent = new LinearLayout(getContext());
        cardContent.setOrientation(LinearLayout.VERTICAL);

        TextView tvDateTime = new TextView(getContext());
        tvDateTime.setText("Date and Time: " + dataObject.getDateTime());
        tvDateTime.setTextColor(Color.BLACK);

        TextView tvInsulin = new TextView(getContext());
        tvInsulin.setText("Insulin: " + dataObject.getInsulin());
        tvInsulin.setTextColor(Color.BLACK);

        TextView tvGlucose = new TextView(getContext());
        tvGlucose.setText("Glucose: " + dataObject.getGlucose());
        tvGlucose.setTextColor(Color.BLACK);

        TextView tvMedications = new TextView(getContext());
        tvMedications.setText("Medications: " + dataObject.getMedications());
        tvMedications.setTextColor(Color.BLACK);

        TextView tvNotes = new TextView(getContext());
        tvNotes.setText("Notes: " + dataObject.getNotes());
        tvNotes.setTextColor(Color.BLACK);

        cardContent.addView(tvDateTime);
        cardContent.addView(tvInsulin);
        cardContent.addView(tvGlucose);
        cardContent.addView(tvMedications);
        cardContent.addView(tvNotes);

        cardView.addView(cardContent);
        llHistoryData.addView(cardView);
    }

    private long parseDateTimeToXValue(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateTime);
            return date.getTime(); // Returns timestamp in milliseconds
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 or handle error appropriately
        }
    }

    private void setLineChartData(List<Entry> insulinEntries, List<Entry> glucoseEntries) {
        LineDataSet insulinDataSet = new LineDataSet(insulinEntries, "Insulin");
        insulinDataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        insulinDataSet.setValueTextColor(Color.BLACK);
        insulinDataSet.setValueTextSize(12f);

        LineDataSet glucoseDataSet = new LineDataSet(glucoseEntries, "Glucose");
        glucoseDataSet.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        glucoseDataSet.setValueTextColor(Color.BLACK);
        glucoseDataSet.setValueTextSize(12f);

        LineData lineData = new LineData(insulinDataSet, glucoseDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh

        // Customize chart appearance
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.WHITE);

        // Customize x-axis
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                return dateFormat.format(new Date((long) value));
            }
        });
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
    }
}