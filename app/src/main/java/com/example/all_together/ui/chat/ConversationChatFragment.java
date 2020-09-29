package com.example.all_together.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.R;
import com.example.all_together.adapter.ChatMassageAdapter;
import com.example.all_together.model.ChatMessage;
import com.example.all_together.model.Volunteering;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConversationChatFragment extends Fragment {

    Volunteering volunteering;

    EditText msgEt;

    ImageButton backBtn;
    ImageButton sendMsgBtn;

    List<ChatMessage> chatMessageList = new ArrayList<>();
    RecyclerView recyclerView;
    ChatMassageAdapter adapter;

    //Firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference volunteersDB = database.getReference("volunteerList");
    DatabaseReference usersDB = database.getReference("users");
//    DatabaseReference userChatsDB = database.getReference("user_chats");

    public ConversationChatFragment(Volunteering volunteering) {
        this.volunteering = volunteering;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation_chat, container, false);

        recyclerView = view.findViewById(R.id.chat_recycler);
        adapter = new ChatMassageAdapter(chatMessageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    //ReadeDB();
                }
            }
        };

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
                            volunteering.getOldUID(),
                             currentTime.getTime());

                    // add it
                    chatMessageList.add(chatMessage);
                    adapter.notifyItemInserted(chatMessageList.size());

                    // save it
                    usersDB.child(firebaseUser.getUid()).child("chats").child(volunteering.getOldUID()).setValue(chatMessageList);

                    // NEED TO MAKE A VOLUNTEERING ID! THAT WOULD HELP A LOT.

                }
                // at the end
                msgEt.setText("");
            }
        });

        usersDB.child(firebaseUser.getUid()).child("chats").child(volunteering.getOldUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatMessageList.clear();

                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        ChatMessage chatMessage = ds.getValue(ChatMessage.class);
                        chatMessageList.add(chatMessage);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


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