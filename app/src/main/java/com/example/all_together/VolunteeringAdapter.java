package com.example.all_together;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VolunteeringAdapter extends RecyclerView.Adapter<VolunteeringAdapter.VolunteeringViewHolder> {

    private List<Volunteering>volunteeringList;

    public VolunteeringAdapter(List<Volunteering> volunteeringList) {
        this.volunteeringList = volunteeringList;
    }

    @NonNull
    @Override
    public VolunteeringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.volunteering_card,parent,false);
        VolunteeringViewHolder volunteeringViewHolder = new VolunteeringViewHolder(view);
        return volunteeringViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteeringViewHolder holder, int position) {

            Volunteering volunteering = volunteeringList.get(position);
            holder.nameTv.setText(volunteering.getName());
            holder.locationTv.setText(volunteering.getLocation());
            holder.dateTv.setText(volunteering.getDate());
            holder.hourTv.setText(volunteering.getHour());
            //holder.info.setImageResource(R.drawable.ic_sharp_info_24);
    }

    @Override
    public int getItemCount() {
        return volunteeringList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class VolunteeringViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView locationTv;
        TextView dateTv;
        TextView hourTv;
        //ImageView info;

        public VolunteeringViewHolder(@NonNull View itemView) {
            super(itemView);
                nameTv = itemView.findViewById(R.id.name_on_card);
                locationTv = itemView.findViewById(R.id.location_on_card);
                dateTv = itemView.findViewById(R.id.date_on_card);
                hourTv = itemView.findViewById(R.id.hour_on_card);

//                info.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
            }
    }
}
