package com.example.all_together.ui.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.ChatConversationActivity;
import com.example.all_together.Notifications.Token;
import com.example.all_together.OldUserActivity;
import com.example.all_together.R;
import com.example.all_together.adapter.ChatAdapter;
import com.example.all_together.model.Chat;
import com.example.all_together.model.Volunteering;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ChatsFragment extends Fragment {

    List<Chat> chatList = new ArrayList<>();
    RecyclerView recyclerView;
    ChatAdapter adapter;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");
    DatabaseReference chatsDB = database.getReference("chats");

    CoordinatorLayout coordinatorLayout;

    boolean isOldUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);

        coordinatorLayout = view.findViewById(R.id.coordinator_chat);

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

                    updateToken(FirebaseInstanceId.getInstance().getToken());
                }
            }
        };

        recyclerView.setAdapter(adapter);

        adapter.setListener(new ChatAdapter.ChatListener() {
            @Override
            public void onChatClicked(int position, View view) {

                Chat chat = chatList.get(position);

                // Send the chat to the chat activity
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("chat",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String json = gson.toJson(chat);
                editor.putString("chat", json);
                editor.commit();

                Intent intent = new Intent(getActivity().getApplicationContext(), ChatConversationActivity.class);
                startActivity(intent);

//                Fragment fragment = new ConversationChatFragment(chat);
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                if (isOldUser) {
//                    fragmentTransaction.replace(R.id.drawerLayout_activityolduser, fragment);
//                } else {
//                    fragmentTransaction.replace(R.id.drawerLayout_activitymainapp, fragment);
//                }
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
            }
        });

//        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//                final int position = viewHolder.getAdapterPosition();
//                final Chat item = chatList.get(position);
//
//                chatList.remove(viewHolder.getAdapterPosition());
//                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//
//                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item removed from list", Snackbar.LENGTH_LONG)
//                        .setAction("UNDO", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                chatList.add(position, item);
//                                adapter.notifyItemInserted(position);
//                                chatsDB.setValue(chatList);
//                            }
//                        });
//                snackbar.show();
//
//                chatsDB.setValue(chatList);
//            }
//        };
//        ItemTouchHelper helper = new ItemTouchHelper(callback);
//        helper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void ReadFirebaseDB(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.loading_chats));
        progressDialog.show();

        chatsDB.addListenerForSingleValueEvent(new ValueEventListener() {
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


    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
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
