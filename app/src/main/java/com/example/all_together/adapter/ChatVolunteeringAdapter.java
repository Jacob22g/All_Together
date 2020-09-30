package com.example.all_together.adapter;

import android.net.Uri;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatVolunteeringAdapter extends RecyclerView.Adapter<ChatVolunteeringAdapter.ChatVolunteeringViewHolder> {

    List<Volunteering> volunteeringList;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public ChatVolunteeringAdapter(List<Volunteering> volunteeringList) {
        this.volunteeringList = volunteeringList;
    }

    public interface ChatVolunteeringListener{
        void onChatVolunteerClicked(int position, View view);
    }

    private ChatVolunteeringListener listener;

    public void setListener(ChatVolunteeringListener listener){ this.listener = listener;}

    public class ChatVolunteeringViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imageView;
        TextView nameTv;
        TextView dateTv;
        TextView timeTv;

        public ChatVolunteeringViewHolder(@NonNull final View itemView) {
            super(itemView);

            this.imageView = itemView.findViewById(R.id.chat_volunteer_image);
            this.nameTv = itemView.findViewById(R.id.chat_volunteer_name);
            this.dateTv = itemView.findViewById(R.id.chat_volunteer_date);
            this.timeTv = itemView.findViewById(R.id.chat_volunteer_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open chat conversation Fragment- implement in chat fragment
                    listener.onChatVolunteerClicked(getAdapterPosition(), v);
                }
            });
        }
    }

    @NonNull
    @Override
    public ChatVolunteeringAdapter.ChatVolunteeringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_volunteer_layout, parent, false);
        ChatVolunteeringViewHolder chatVolunteeringViewHolder = new ChatVolunteeringViewHolder(view);
        return chatVolunteeringViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVolunteeringAdapter.ChatVolunteeringViewHolder holder, int position) {

        Volunteering volunteering = volunteeringList.get(position);

        holder.nameTv.setText(volunteering.getName());
        holder.dateTv.setText(volunteering.getDate());
        holder.timeTv.setText(volunteering.getHour());

        final ChatVolunteeringAdapter.ChatVolunteeringViewHolder holder1 = holder;

        // load image - might be to slow!
        StorageReference imageStorageRef = storageRef.child(volunteering.getOldUID()+"/profile_image");
        imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(holder1.itemView).
                        load(imageURL).
                        into(holder1.imageView);
            }
        });

    }

    @Override
    public int getItemCount() {
        return volunteeringList.size();
    }
}
