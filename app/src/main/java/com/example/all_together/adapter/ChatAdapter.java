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
import com.example.all_together.model.ChatMessage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    List<Chat> chatList;

    String receiverId;
    String receiverName;
    String lastMsg;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");
    DatabaseReference chatsDB = database.getReference("chats");

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
        TextView lastMsgTv;

        public ChatViewHolder(@NonNull final View itemView) {
            super(itemView);

            this.imageView = itemView.findViewById(R.id.chat_volunteer_image);
            this.nameTv = itemView.findViewById(R.id.chat_volunteer_name);
            this.lastMsgTv = itemView.findViewById(R.id.chat_volunteer_last_msg);

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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);
        ChatViewHolder chatViewHolder = new ChatViewHolder(view);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {

        Chat chat = chatList.get(position);

        final ChatAdapter.ChatViewHolder holder1 = holder;

        if (firebaseUser.getUid().equals(chat.getSideAUid())){
            receiverId = chat.getSideBUid();
        } else {
            receiverId = chat.getSideAUid();
        }

//        getReceiverName();
//        getLastChatMessage(chat);
//
//        holder.nameTv.setText(receiverName);
//        holder.lastMsgTv.setText(lastMsg);


        usersDB.child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    receiverName = snapshot.child("user_name").getValue(String.class);
                    holder1.nameTv.setText(receiverName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        chatsDB.child(chat.getChatID()).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<ChatMessage> chatMessages = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ChatMessage chatMessage = ds.getValue(ChatMessage.class);
                        chatMessages.add(chatMessage);
                    }
                    lastMsg = chatMessages.get(chatMessages.size()-1).getMessageText();
                    holder1.lastMsgTv.setText(lastMsg);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });



        // load image
        StorageReference imageStorageRef = storageRef.child(receiverId+"/profile_image");
        imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                if (holder1.itemView!=null) {
                    Glide.with(holder1.itemView).
                            load(imageURL).
                            into(holder1.imageView);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private void getReceiverName(){
        usersDB.child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    receiverName = snapshot.child("user_name").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getLastChatMessage(Chat chat){
        chatsDB.child(chat.getChatID()).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<ChatMessage> chatMessages = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ChatMessage chatMessage = ds.getValue(ChatMessage.class);
                        chatMessages.add(chatMessage);
                    }
                    lastMsg = chatMessages.get(chatMessages.size()-1).getMessageText();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


}
