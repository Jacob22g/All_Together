package com.example.all_together.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.all_together.R;
import com.example.all_together.model.Volunteering;

import java.util.List;

public class HomeOldVolunteeringAdapter extends RecyclerView.Adapter<HomeOldVolunteeringAdapter.VolunteeringViewHolder> {

    public List<Volunteering>volunteeringList;
    public MyVolunteeringInfoListener listener;

    public interface MyVolunteeringInfoListener{
        void onVolunteeringClicked(int position, View view);
    }

    public void setListener(MyVolunteeringInfoListener listener){
        this.listener = listener;
    }

    public HomeOldVolunteeringAdapter(List<Volunteering> volunteeringList) {
        this.volunteeringList = volunteeringList;
    }

    @NonNull
    @Override
    public VolunteeringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_old_volunteering_card,parent,false);
        VolunteeringViewHolder volunteeringViewHolder = new VolunteeringViewHolder(view);
        return volunteeringViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteeringViewHolder holder, int position) {
        Volunteering volunteering = volunteeringList.get(position);

        holder.dateTv.setText(volunteering.getDate());
        holder.hourTv.setText(volunteering.getHour());

        if (volunteering.getVolunteerUID()!=null){

            Glide.with(holder.itemView).load(R.drawable.ic_check_box).into(holder.checkIv);
        } else {

//            Glide.with(holder.itemView).
//                    load(R.drawable.ic_check_box_blank).
//                    into(holder.checkIv);

            // No image:
            holder.checkIv.setVisibility(View.GONE);
        }


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

        TextView dateTv;
        TextView hourTv;
        ImageView checkIv;

        public VolunteeringViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTv = itemView.findViewById(R.id.date_on_old_card);
            hourTv = itemView.findViewById(R.id.hour_on_old_card);
            checkIv = itemView.findViewById(R.id.image_on_old_card);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onVolunteeringClicked(getAdapterPosition(),view);
                    }
                }
            });

        }

    }
}
