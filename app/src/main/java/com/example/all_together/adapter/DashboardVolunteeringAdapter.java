package com.example.all_together.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.R;
import com.example.all_together.model.Volunteering;

import java.util.List;

public class DashboardVolunteeringAdapter extends RecyclerView.Adapter<DashboardVolunteeringAdapter.VolunteerViewHolder> {

    private List<Volunteering> volunteerList;

    public DashboardVolunteeringAdapter(List<Volunteering> volunteerList) {
        this.volunteerList = volunteerList;
    }

    public interface VolunteerListener{
        void onVolunteerClicked(int position, View view);
    }

    private VolunteerListener listener;

    public void setListener(VolunteerListener listener){ this.listener = listener;}

    public class VolunteerViewHolder extends RecyclerView.ViewHolder{

        TextView volunteerTypeTV;
        TextView volunteerLocationTV;
        TextView volunteerDateTV;
        TextView volunteerHourTV;

        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);

            this.volunteerTypeTV = itemView.findViewById(R.id.volunteer_type_tv);
            this.volunteerLocationTV = itemView.findViewById(R.id.volunteer_location_tv);
            this.volunteerDateTV = itemView.findViewById(R.id.volunteer_date_tv);
            this.volunteerHourTV = itemView.findViewById(R.id.volunteer_time_tv);

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
    public DashboardVolunteeringAdapter.VolunteerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_volunteer_layout, parent, false);
        VolunteerViewHolder volunteerViewHolder = new VolunteerViewHolder(view);
        return volunteerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardVolunteeringAdapter.VolunteerViewHolder holder, int position) {

        Volunteering volunteering = volunteerList.get(position);

        holder.volunteerTypeTV.setText(volunteering.getType());
        holder.volunteerDateTV.setText(volunteering.getDate());
        holder.volunteerHourTV.setText(volunteering.getHour());
        holder.volunteerLocationTV.setText(volunteering.getLocationCity());
    }

    @Override
    public int getItemCount() {
        return volunteerList.size();
    }
}
