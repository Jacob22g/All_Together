package com.example.all_together.ui.chat;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.all_together.R;
import com.example.all_together.adapter.ChatMassageAdapter;
import com.example.all_together.model.Chat;
import com.example.all_together.model.ChatMessage;
import com.example.all_together.model.Volunteering;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ConversationChatFragment extends Fragment {

    Chat chat;

    EditText msgEt;

    ImageButton backBtn;
    ImageButton sendMsgBtn;

    TextView receiverNameTv;
    ImageView receiverImage;

    List<ChatMessage> chatMessageList = new ArrayList<>();
    RecyclerView recyclerView;
    ChatMassageAdapter adapter;

    String receiverID;
    String receiverName;

    //Firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference volunteersDB = database.getReference("volunteerList");
    DatabaseReference chatsDB = database.getReference("chats");
    DatabaseReference usersDB = database.getReference("users");
    private StorageReference storageRef;

    public ConversationChatFragment(Chat chat) {
        this.chat = chat;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation_chat, container, false);

        if (firebaseUser.getUid().equals(chat.getSideBUid()))
            receiverID = chat.getSideAUid();
        else {
            receiverID = chat.getSideBUid();
        }


        recyclerView = view.findViewById(R.id.chat_recycler);
        adapter = new ChatMassageAdapter(chatMessageList);

        // Make sure the chat scrolls up
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);

        // Set profile name and image
        receiverNameTv = view.findViewById(R.id.username_conversation_chat);
        usersDB.child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    receiverName = snapshot.child("user_name").getValue(String.class);
                    receiverNameTv.setText(receiverName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
//        receiverNameTv.setText(chat.getReceiverName());

        receiverImage = view.findViewById(R.id.image_conversation_chat);
        storageRef = FirebaseStorage.getInstance().getReference();
        // load user image
        StorageReference imageStorageRef = storageRef.child(receiverID+"/profile_image");
        imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String uriStr = uri.toString();
                if (getContext()!=null) {
                    Glide.with(getContext())
                            .load(uriStr)
                            .into(receiverImage);
                }
            }
        });


        // LOAD FROM CHAT DB
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // Load the chat
//                    chatsDB.child(String.valueOf(chat.getChatID())).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                chatMessageList.clear();
//                                for (DataSnapshot ds : snapshot.getChildren()) {
//                                    ChatMessage chatMessage = ds.getValue(ChatMessage.class);
//                                    chatMessageList.add(chatMessage);
//                                }
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                        }
//                    });
//                }
            }
        };

        chatsDB.child(String.valueOf(chat.getChatID())).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chatMessageList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ChatMessage chatMessage = ds.getValue(ChatMessage.class);
                        chatMessageList.add(chatMessage);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

//        // Chat update listener
//        chatsDB.child(String.valueOf(chat.getChatID())).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                // Update the conversation
//                chatMessageList.clear();
//                Iterator i = snapshot.getChildren().iterator();
//                while (i.hasNext()){
//                    ChatMessage chatMessage = ((DataSnapshot)i.next()).getValue(ChatMessage.class);
//                    // add it
//                    chatMessageList.add(chatMessage);
//                }
//                adapter.notifyItemInserted(chatMessageList.size());
//            }
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                // Update the conversation
//                chatMessageList.clear();
//                Iterator i = snapshot.getChildren().iterator();
//                while (i.hasNext()){
//                    ChatMessage chatMessage = ((DataSnapshot)i.next()).getValue(ChatMessage.class);
//                    // add it
//                    chatMessageList.add(chatMessage);
//                }
//                adapter.notifyItemInserted(chatMessageList.size());
//            }
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });

        msgEt = view.findViewById(R.id.text_message);

        // Send and save it in firebase
        sendMsgBtn = view.findViewById(R.id.send_message_btn);
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date currentTime = Calendar.getInstance().getTime();

                if (!msgEt.getText().toString().equals("")){

                    // Create the message
                    ChatMessage chatMessage = new ChatMessage(msgEt.getText().toString(),
                            firebaseUser.getUid(),
                            receiverID,
                             currentTime.getTime());

                    // add it
                    chatMessageList.add(chatMessage);
                    adapter.notifyItemInserted(chatMessageList.size());

                    // save it
                    // SAVE IN THE CHAT DB
//                    usersDB.child(firebaseUser.getUid()).child("chats").child(volunteering.getOldUID()).setValue(chatMessageList);
                    chatsDB.child(String.valueOf(chat.getChatID())).child("messages").setValue(chatMessageList);

                }
                // at the end
                msgEt.setText("");
            }
        });

        recyclerView.setAdapter(adapter);

        backBtn = view.findViewById(R.id.conversation_chat_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}