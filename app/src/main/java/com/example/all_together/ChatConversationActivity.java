package com.example.all_together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.all_together.Notifications.APIService;
import com.example.all_together.Notifications.Client;
import com.example.all_together.Notifications.Data;
import com.example.all_together.Notifications.MyResponse;
import com.example.all_together.Notifications.Sender;
import com.example.all_together.Notifications.Token;
import com.example.all_together.adapter.ChatMassageAdapter;
import com.example.all_together.model.Chat;
import com.example.all_together.model.ChatMessage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class ChatConversationActivity extends AppCompatActivity {

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
    DatabaseReference chatsDB = database.getReference("chats");
    DatabaseReference usersDB = database.getReference("users");
    private StorageReference storageRef;
    String firebaseUserName;

    APIService apiService;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

        // Get the chat:
        SharedPreferences sharedPreferences = getSharedPreferences("chat",MODE_PRIVATE);
//        SharedPreferences sharedPreferences = getSharedPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("chat", "");
        chat = gson.fromJson(json, Chat.class);

        // For notifications
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        if (firebaseUser.getUid().equals(chat.getSideBUid()))
            receiverID = chat.getSideAUid();
        else {
            receiverID = chat.getSideBUid();
        }

        recyclerView = findViewById(R.id.chat_recycler);
        adapter = new ChatMassageAdapter(chatMessageList);

        // Make sure the chat scrolls up
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatConversationActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);

        // Set profile name and image
        receiverNameTv = findViewById(R.id.username_conversation_chat);
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

        usersDB.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    firebaseUserName = snapshot.child("user_name").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        receiverImage = findViewById(R.id.image_conversation_chat);
        storageRef = FirebaseStorage.getInstance().getReference();
        // load user image
        StorageReference imageStorageRef = storageRef.child(receiverID+"/profile_image");
        imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String uriStr = uri.toString();
                if (ChatConversationActivity.this!=null) {
                    Glide.with(ChatConversationActivity.this)
                            .load(uriStr)
                            .into(receiverImage);
                }
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
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

        msgEt = findViewById(R.id.text_message);

        // Send and save it in firebase
        sendMsgBtn = findViewById(R.id.send_message_btn);
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notify = true;

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

                    // Need to check if user is offline!
                    // Send Notification
                    if (notify) {
                        sendNotification(receiverID, firebaseUserName, msgEt.getText().toString());
                    }
                    notify = false;

                }
                // at the end
                msgEt.setText("");
            }

        });

        recyclerView.setAdapter(adapter);

        backBtn = findViewById(R.id.conversation_chat_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatConversationActivity.this.onBackPressed();
            }
        });



    }

    private void sendNotification(final String receiverID, final String userName, final String message) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiverID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),
                            R.drawable.volunteer_icon,
                            userName+": "+message,
                            "New Message",
                            receiverID); // NOT SURE THIS IS THE RECEIVER ID

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1){
                                            Toast.makeText(ChatConversationActivity.this, "Notification Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) { }
                            });


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