package com.example.all_together;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.all_together.Notifications.APIService;
import com.example.all_together.Notifications.Client;
import com.example.all_together.Notifications.Data;
import com.example.all_together.Notifications.MyResponse;
import com.example.all_together.Notifications.Sender;
import com.example.all_together.Notifications.Token;
import com.example.all_together.model.Chat;
import com.example.all_together.model.Volunteering;
import com.example.all_together.ui.chat.ConversationChatFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class VolunteeringFragment extends Fragment {

    Volunteering volunteering;
    int positionInList;

    TextView nameTv;
    TextView dateTv;
    TextView timeTv;
    TextView locationTv;
    TextView descriptionTv;

    Button addBtn;
    Button chatBtn;
    Button profileBtn;
    ImageButton backBtn;

    String newChatId;
    Chat chat;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String firebaseUserName;

    List<Volunteering> volunteerList  = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference volunteersDB = database.getReference("volunteerList");
    DatabaseReference usersDB = database.getReference("users");
    DatabaseReference chats_id = database.getReference("chatIdNum");
    DatabaseReference chatsDB = database.getReference("chats");

    boolean isOldUser;

    APIService apiService;

    public VolunteeringFragment(Volunteering volunteering) {
        this.volunteering = volunteering;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_volunteering, container, false);

        // For notifications
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        // Add the volunteering parameters
        nameTv = view.findViewById(R.id.selected_volunteering_name);
        dateTv = view.findViewById(R.id.selected_volunteering_date);
        timeTv = view.findViewById(R.id.selected_volunteering_time);
        locationTv = view.findViewById(R.id.selected_volunteering_location);
        descriptionTv = view.findViewById(R.id.selected_volunteering_description);

        nameTv.setText(volunteering.getNameOld());
        dateTv.setText(volunteering.getDate());
        timeTv.setText(volunteering.getHour());
        locationTv.setText(volunteering.getLocationCity() +", "+ volunteering.getLocationStreet());

        descriptionTv.setText(volunteering.getDescription());
        if (volunteering.getDescription() == null)
            descriptionTv.setVisibility(View.GONE);

        chat = new Chat();
        newChatId = UUID.randomUUID().toString();

        // Check if it is a old user
        //  Only after getting this result we cav know how to design the fragment
        usersDB.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isOldUser = snapshot.child("is_old_user").getValue(boolean.class);
                    firebaseUserName = snapshot.child("user_name").getValue(String.class);

                    // Do this here after we know id it is an old user
                    listenerForAddBtn();
                    listenerForChatBtn();
                    listenerForProfileBtn();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Read the DB to update it if needed
        volunteersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                volunteerList.clear();
                int i=0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Volunteering listVolunteer = ds.getValue(Volunteering.class);
                        // find the volunteering position in the list
                        if (listVolunteer.getId() == volunteering.getId())
                            positionInList=i;
                        else
                            i++;
                        volunteerList.add(listVolunteer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

//        // Get the chat ID if we create a chat
//        chats_id.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                newChatId = snapshot.getValue(long.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });

        // Generate a random id:

        // Add user to the volunteering
        addBtn = view.findViewById(R.id.selected_volunteering_add_btn);
        addBtn.setVisibility(View.GONE);

        profileBtn = view.findViewById(R.id.selected_profile_page_btn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


//        if (!isOldUser) {
//            addBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (firebaseUser.getUid().equals(volunteering.getVolunteerUID())) {
//                        addBtn.setText("Add me");
//                        Toast.makeText(getContext(), "you have been removed", Toast.LENGTH_SHORT).show();
//                        volunteering.setVolunteerUID(null);
//                        volunteering.setNameVolunteer(null);
//                    } else {
//                        addBtn.setText("Remove me");
//                        Toast.makeText(getContext(), "you have been added", Toast.LENGTH_SHORT).show();
//                        volunteering.setVolunteerUID(firebaseUser.getUid()); // Saving user in the volunteering
//                        volunteering.setNameVolunteer(firebaseUserName);
//                    }
//
//                    // Saving the new list
//                    volunteerList.set(positionInList, volunteering);
//                    volunteersDB.setValue(volunteerList);
//                }
//            });
//        } else {
//            addBtn.setVisibility(View.GONE);
//        }

        if (firebaseUser.getUid().equals(volunteering.getVolunteerUID())) {
            addBtn.setText("Remove me");
        } else {
            if (volunteering.getVolunteerUID() == null)
                addBtn.setText("Add me");
            else {
                // Check if another user took it
                addBtn.setText("Volunteer taken");
                addBtn.setEnabled(false);
            }
        }

        chatBtn = view.findViewById(R.id.selected_volunteering_chat_btn);
        chatBtn.setVisibility(View.GONE);
//        if (isOldUser && volunteering.getVolunteerUID()==null) {
//            chatBtn.setVisibility(View.GONE);
//        } else {
//            chatBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    // Open the Chat and add the chat to chats list
//
//                    // Check if chat between these  two users exists
//
//                    // if exists the ChatExists() will put existing chat into chat;
//                    if (!ChatExists()) {
//                        // if not exists Create the chat and add it
//                        chat.setChatID(newChatId);
//                        chat.setSideAUid(firebaseUser.getUid());
//                        if (isOldUser) {
//                            chat.setSideBUid(volunteering.getVolunteerUID());
////                            chat.setReceiverName(volunteering.getNameVolunteer());
//                            chat.setReceiverName(volunteering.getNameVolunteer());
//                        } else {
//                            chat.setSideBUid(volunteering.getOldUID());
//                            chat.setReceiverName(volunteering.getNameOld());
////                            chat.setReceiverName(volunteering.getNameOld());
//                        }
//                        // add the chat to chat list
//                        chatsDB.setValue(newChatId+1);
//                        chatsDB.child(String.valueOf(newChatId)).setValue(chat);
//                    }
//
//                    // open the chat
//                    Fragment fragment = new ConversationChatFragment(chat);
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    if (isOldUser) {
//                        fragmentTransaction.replace(R.id.drawerLayout_activityolduser, fragment);
//                    } else {
//                        fragmentTransaction.replace(R.id.drawerLayout_activitymainapp, fragment);
//                    }
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                }
//            });
//        }

        backBtn = view.findViewById(R.id.selected_volunteering_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void listenerForProfileBtn() {

    }

    private void listenerForAddBtn(){

        if (!isOldUser) {
            // change it to default View.GONE
            addBtn.setVisibility(View.VISIBLE);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firebaseUser.getUid().equals(volunteering.getVolunteerUID())) {
                        addBtn.setText("Add me");
                        Toast.makeText(getContext(), "you have been removed", Toast.LENGTH_SHORT).show();
                        volunteering.setVolunteerUID(null);
                        volunteering.setNameVolunteer(null);
                    } else {
                        addBtn.setText("Remove me");
                        Toast.makeText(getContext(), "you have been added", Toast.LENGTH_SHORT).show();
                        volunteering.setVolunteerUID(firebaseUser.getUid()); // Saving user in the volunteering
                        volunteering.setNameVolunteer(firebaseUserName);

                        // Send notification to old user
                        sendNotification(volunteering.getOldUID(), firebaseUserName, "will volunteer with you");
                    }

                    // Saving the new list
                    volunteerList.set(positionInList, volunteering);
                    volunteersDB.setValue(volunteerList);
                }
            });
        } else {
            addBtn.setVisibility(View.GONE);
        }
    }

    private void  listenerForChatBtn(){

        if (isOldUser && volunteering.getVolunteerUID()==null) {
            chatBtn.setVisibility(View.GONE);
        } else {
            // change it to default View.GONE
            chatBtn.setVisibility(View.VISIBLE);
            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Open the Chat and add the chat to chats list
                    // Check if chat between these  two users exists
                    // if exists the ChatExists() will put existing chat into chat;
//                    if (!ChatExistsOrCreate()) {
//                        // if not exists Create the chat and add it
//                        chat.setChatID(newChatId);
//                        chat.setSideAUid(firebaseUser.getUid());
//                        if (isOldUser) {
//                            chat.setSideBUid(volunteering.getVolunteerUID());
////                            chat.setReceiverName(volunteering.getNameVolunteer());
////                            chat.setReceiverName(volunteering.getNameVolunteer());
//                        } else {
//                            chat.setSideBUid(volunteering.getOldUID());
////                            chat.setReceiverName(volunteering.getNameOld());
////                            chat.setReceiverName(volunteering.getNameOld());
//                        }
//                        // add the chat to chat list
////                        chatsDB.setValue(newChatId+1);
////                        chatsDB.child(String.valueOf(newChatId)).setValue(chat);
//                        chatsDB.child(newChatId).setValue(chat);
//                    }

                    // Check if chat exists or create a new chat
//                    ChatExistsOrCreate();

                    chatsDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){

                                for (DataSnapshot ds: snapshot.getChildren()){

                                    Chat checkChat = ds.getValue(Chat.class);

//                                    // for testing -----------
//                                    String chatId = checkChat.getChatID();
//                                    String sideAId = checkChat.getSideAUid();
//                                    String sideBId = checkChat.getSideBUid();
//                                    String oldId = volunteering.getOldUID();
//                                    String volunteerId = volunteering.getVolunteerUID(); // may be null
//                                    String firebaseUserId = firebaseUser.getUid();
//                                    //-------------------------

                                    // Chat when the volunteer user is sign to the volunteering
                                    if (((checkChat.getSideAUid().equals(volunteering.getOldUID())) &&
                                            (checkChat.getSideBUid().equals(volunteering.getVolunteerUID())))
                                            ||
                                            ((checkChat.getSideAUid().equals(volunteering.getVolunteerUID())) &&
                                                    (checkChat.getSideBUid().equals(volunteering.getOldUID())))) {
                                        chat = checkChat;
                                    }
                                    // Chat when the volunteer user is not sign to the volunteering
                                    else if (((checkChat.getSideAUid().equals(firebaseUser.getUid())) &&
                                            (checkChat.getSideBUid().equals(volunteering.getOldUID())))
                                            ||
                                            ((checkChat.getSideAUid().equals(volunteering.getOldUID())) &&
                                                    (checkChat.getSideBUid().equals(firebaseUser.getUid())))) {
                                        chat = checkChat;

                                    } else {
                                        // Create the chat
                                        chat.setChatID(newChatId);
                                        chat.setSideAUid(firebaseUser.getUid());
                                        if (isOldUser) {
                                            chat.setSideBUid(volunteering.getVolunteerUID());
                                        } else {
                                            chat.setSideBUid(volunteering.getOldUID());
                                        }
                                        chatsDB.child(newChatId).setValue(chat);
                                    }
                                }

                            } else {

                                chat.setChatID(newChatId);
                                chat.setSideAUid(firebaseUser.getUid());
                                if (isOldUser) {
                                    chat.setSideBUid(volunteering.getVolunteerUID());
                                } else {
                                    chat.setSideBUid(volunteering.getOldUID());
                                }
                                chatsDB.child(newChatId).setValue(chat);
                            }

                            // Send the chat to the chat activity
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("chat",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(chat);
                            editor.putString("chat", json);
                            editor.commit();

                            Intent intent = new Intent(getActivity().getApplicationContext(), ChatConversationActivity.class);
                            startActivity(intent);

//                            // open the chat
//                            Fragment fragment = new ConversationChatFragment(chat);
//                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            if (isOldUser) {
//                                fragmentTransaction.replace(R.id.drawerLayout_activityolduser, fragment);
//                            } else {
//                                fragmentTransaction.replace(R.id.drawerLayout_activitymainapp, fragment);
//                            }
//                            fragmentTransaction.addToBackStack(null);
//                            fragmentTransaction.commit();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });


//                    // open the chat
//                    Fragment fragment = new ConversationChatFragment(chat);
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    if (isOldUser) {
//                        fragmentTransaction.replace(R.id.drawerLayout_activityolduser, fragment);
//                    } else {
//                        fragmentTransaction.replace(R.id.drawerLayout_activitymainapp, fragment);
//                    }
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
                }
            });
        }
    }

    private void sendNotification(final String receiverID, final String userName, final String message) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiverID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),
                            R.drawable.volunteer_icon,
                            userName + " " + message,
                            "New Volunteer added",
                            receiverID);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(getContext(), "Notification Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void ChatExistsOrCreate(){

        chatsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    for (DataSnapshot ds: snapshot.getChildren()){

                        Chat checkChat = ds.getValue(Chat.class);
                        // for testing -----------
                        String chatId = checkChat.getChatID();
                        String sideAId = checkChat.getSideAUid();
                        String sideBId = checkChat.getSideBUid();
                        String oldId = volunteering.getOldUID();
                        String volunteerId = volunteering.getVolunteerUID(); // may be null
                        String firebaseUserId = firebaseUser.getUid();
                        //-------------------------

                        // Chat when the volunteer user is sign to the volunteering
                        if (((checkChat.getSideAUid().equals(volunteering.getOldUID())) &&
                                (checkChat.getSideBUid().equals(volunteering.getVolunteerUID())))
                                ||
                                ((checkChat.getSideAUid().equals(volunteering.getVolunteerUID())) &&
                                        (checkChat.getSideBUid().equals(volunteering.getOldUID())))) {
                            chat = checkChat;
                        }
                        // Chat when the volunteer user is not sign to the volunteering
                        else if (((checkChat.getSideAUid().equals(firebaseUser.getUid())) &&
                                (checkChat.getSideBUid().equals(volunteering.getOldUID())))
                                ||
                                ((checkChat.getSideAUid().equals(volunteering.getOldUID())) &&
                                        (checkChat.getSideBUid().equals(firebaseUser.getUid())))) {
                            chat = checkChat;
                        } else {

                            // Create the chat
                            chat.setChatID(newChatId);
                            chat.setSideAUid(firebaseUser.getUid());
                            if (isOldUser) {
                                chat.setSideBUid(volunteering.getVolunteerUID());
                            } else {
                                chat.setSideBUid(volunteering.getOldUID());
                            }
                            chatsDB.child(newChatId).setValue(chat);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}