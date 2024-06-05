
package com.example.slfb.doctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.slfb.R;
import com.example.slfb.patient.Appointment;

import java.util.ArrayList;

public class AppointmentListAdapter extends ArrayAdapter<Appointment> {

    private Context mContext;
    private int mResource;

    public AppointmentListAdapter(Context context, int resource, ArrayList<Appointment> appointments) {
        super(context, resource, appointments);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String patientName = getItem(position).getPatientName();
        String date = getItem(position).getDate();
        String time = getItem(position).getTime();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvPatientName = convertView.findViewById(R.id.tv_patient_name);
        TextView tvDate = convertView.findViewById(R.id.tv_date);
        TextView tvTime = convertView.findViewById(R.id.tv_time);

        tvPatientName.setText(patientName);
        tvDate.setText(date);
        tvTime.setText(time);

        return convertView;
    }
}
