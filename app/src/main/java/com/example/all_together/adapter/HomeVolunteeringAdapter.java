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

public class HomeVolunteeringAdapter extends RecyclerView.Adapter<HomeVolunteeringAdapter.VolunteeringViewHolder> {

    public List<Volunteering>volunteeringList;
    public MyVolunteeringInfoListener listener;

    public interface MyVolunteeringInfoListener{
        void onVolunteeringClicked(int position, View view);
    }

    public void setListener(MyVolunteeringInfoListener listener){
        this.listener = listener;
    }

    public HomeVolunteeringAdapter(List<Volunteering> volunteeringList) {
        this.volunteeringList = volunteeringList;
    }

    @NonNull
    @Override
    public VolunteeringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_volunteering_card,parent,false);
        VolunteeringViewHolder volunteeringViewHolder = new VolunteeringViewHolder(view);
        return volunteeringViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteeringViewHolder holder, int position) {

            Volunteering volunteering = volunteeringList.get(position);
            holder.nameTv.setText(volunteering.getName());
            holder.locationTv.setText(volunteering.getLocationCity());
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
        //ImageButton infoBtn;

        public VolunteeringViewHolder(@NonNull View itemView) {
            super(itemView);
                nameTv = itemView.findViewById(R.id.name_on_card);
                locationTv = itemView.findViewById(R.id.location_on_card);
                dateTv = itemView.findViewById(R.id.date_on_card);
                hourTv = itemView.findViewById(R.id.hour_on_card);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listener!=null){
                            listener.onVolunteeringClicked(getAdapterPosition(),view);
                        }
                    }
                });

//                infoBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(v.getContext(), "Your Name is: " + v., Toast.LENGTH_SHORT).show();
//                    }
//                });
            }


    }
}
