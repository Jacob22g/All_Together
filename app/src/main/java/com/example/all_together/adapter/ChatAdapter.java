package com.example.all_together.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.all_together.R;
import com.example.all_together.model.Chat;
import com.example.all_together.model.Volunteering;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    List<Chat> chatList;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    public interface ChatListener{
        void onChatClicked(int position, View view);
    }

    private ChatListener listener;

    public void setListener(ChatListener listener){ this.listener = listener;}

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imageView;
        TextView nameTv;
//        TextView dateTv;
//        TextView timeTv;

        public ChatViewHolder(@NonNull final View itemView) {
            super(itemView);

            this.imageView = itemView.findViewById(R.id.chat_volunteer_image);
            this.nameTv = itemView.findViewById(R.id.chat_volunteer_name);
//            this.dateTv = itemView.findViewById(R.id.chat_volunteer_date);
//            this.timeTv = itemView.findViewById(R.id.chat_volunteer_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open chat conversation Fragment- implement in chat fragment
                    listener.onChatClicked(getAdapterPosition(), v);
                }
            });
        }
    }

    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_volunteer_layout, parent, false);
        ChatViewHolder chatViewHolder = new ChatViewHolder(view);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {

        Chat chat = chatList.get(position);

        holder.nameTv.setText(chat.getReceiverName());
//        holder.dateTv.setText(volunteering.getDate());
//        holder.timeTv.setText(volunteering.getHour());

        final ChatAdapter.ChatViewHolder holder1 = holder;

        String receiverId;
        if (firebaseUser.getUid().equals(chat.getSideAUid())){
            receiverId = chat.getSideBUid();
        } else {
            receiverId = chat.getSideAUid();
        }
        // load image
        StorageReference imageStorageRef = storageRef.child(receiverId+"/profile_image");
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
        return chatList.size();
    }
}
