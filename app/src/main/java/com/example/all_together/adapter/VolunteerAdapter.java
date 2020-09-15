package com.example.all_together.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.R;
import com.example.all_together.model.Volunteer;

import java.util.List;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder> {

    private List<Volunteer> volunteerList;

    public VolunteerAdapter(List<Volunteer> volunteerList) {
        this.volunteerList = volunteerList;
    }

    public interface VolunteerListener{
        void onVolunteerClicked(int position, View view);
    }

    private VolunteerListener listener;

    public void setListener(VolunteerListener listener){ this.listener = listener;}

    public class VolunteerViewHolder extends RecyclerView.ViewHolder{

        TextView volunteerTitleTV;
        TextView volunteerDescriptionTV;
        CheckBox volunteerCB;

        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);

            this.volunteerTitleTV = itemView.findViewById(R.id.volunteer_title_tv);
            this.volunteerDescriptionTV = itemView.findViewById(R.id.volunteer_description_tv);
            this.volunteerCB = itemView.findViewById(R.id.volunteer_cb);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onVolunteerClicked(getAdapterPosition(), v);
                }
            });
        }
    }


    @NonNull
    @Override
    public VolunteerAdapter.VolunteerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.volunteer_layout, parent, false);
        VolunteerViewHolder volunteerViewHolder = new VolunteerViewHolder(view);
        return volunteerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteerAdapter.VolunteerViewHolder holder, int position) {

        Volunteer volunteer = volunteerList.get(position);

        holder.volunteerTitleTV.setText(volunteer.getTitle());
        holder.volunteerDescriptionTV.setText(volunteer.getDescription());
        holder.volunteerCB.setChecked(volunteer.isCompleted());
    }

    @Override
    public int getItemCount() {
        return volunteerList.size();
    }
}
