package com.example.all_together.ui.chat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.R;
import com.example.all_together.adapter.ChatAdapter;
import com.example.all_together.model.Chat;
import com.example.all_together.model.Volunteering;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    List<Chat> chatList = new ArrayList<>();
    RecyclerView recyclerView;
    ChatAdapter adapter;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference volunteersDB = database.getReference("volunteerList");
    DatabaseReference usersDB = database.getReference("users");
    DatabaseReference chatsDB = database.getReference("chats");

    boolean isOldUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);

        //.. list of conversations
        //  when click conversation chat opens.

        recyclerView =  view.findViewById(R.id.chats_list_recycler);
        adapter = new ChatAdapter(chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    ReadFirebaseDB();
                }
            }
        };

        recyclerView.setAdapter(adapter);

        adapter.setListener(new ChatAdapter.ChatListener() {
            @Override
            public void onChatClicked(int position, View view) {

                Chat chat = chatList.get(position);

                Fragment fragment = new ConversationChatFragment(chat);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (isOldUser) {
                    fragmentTransaction.replace(R.id.drawerLayout_activityolduser, fragment);
                } else {
                    fragmentTransaction.replace(R.id.drawerLayout_activitymainapp, fragment);
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void ReadFirebaseDB(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Chats, Please wait..");
        progressDialog.show();

        chatsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatList.clear();

                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Chat chat = ds.getValue(Chat.class);
                        if ((chat.getSideAUid().equals(firebaseUser.getUid())) ||
                                (chat.getSideBUid().equals(firebaseUser.getUid())))
                            // add my chats only
                            chatList.add(chat);
                    }
                    adapter.notifyDataSetChanged();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "onCancelled", error.toException());
            }
        });

        // Check if it is a old user
        usersDB.child(firebaseUser.getUid()).child("is_old_user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isOldUser = snapshot.getValue(boolean.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

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
