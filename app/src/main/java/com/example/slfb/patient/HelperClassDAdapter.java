package com.example.slfb.patient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slfb.R;
import com.example.slfb.doctor.HelperClassD;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HelperClassDAdapter extends RecyclerView.Adapter<HelperClassDAdapter.ViewHolder> {

    private Context context;
    private List<HelperClassD> helperClassDList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HelperClassD helperClassD);
    }

    public HelperClassDAdapter(Context context, List<HelperClassD> helperClassDList, OnItemClickListener listener) {
        this.context = context;
        this.helperClassDList = helperClassDList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelperClassD helperClassD = helperClassDList.get(position);
        holder.bind(helperClassD, listener);
    }

    @Override
    public int getItemCount() {
        return helperClassDList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textViewName = itemView.findViewById(R.id.text_view_name);
        }

        public void bind(final HelperClassD helperClassD, final OnItemClickListener listener) {
            textViewName.setText(helperClassD.getName());
            Picasso.get().load(helperClassD.getImage()).into(imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(helperClassD);
                }
            });
        }
    }
}
