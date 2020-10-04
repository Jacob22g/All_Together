package com.example.all_together;

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

import com.example.all_together.model.Chat;
import com.example.all_together.model.Volunteering;
import com.example.all_together.ui.chat.ConversationChatFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    ImageButton backBtn;

    long newChatId;
    Chat chat;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String firebaseUserName;

    List<Volunteering> volunteerList  = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference volunteersDB = database.getReference("volunteerList");
    DatabaseReference usersDB = database.getReference("users");
    DatabaseReference chats_id = database.getReference("chatIdNum");
    DatabaseReference chatsDB = database.getReference("chats");

//    boolean isOldUser = true;
    boolean isOldUser;

    public VolunteeringFragment(Volunteering volunteering) {
        this.volunteering = volunteering;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_volunteering, container, false);

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

        // Check if it is a old user
        usersDB.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isOldUser = snapshot.child("is_old_user").getValue(boolean.class);
                    firebaseUserName = snapshot.child("user_name").getValue(String.class);
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

        // Get the chat ID if we create a chat
        chats_id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newChatId = snapshot.getValue(long.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Add user to the volunteering
        addBtn = view.findViewById(R.id.selected_volunteering_add_btn);
        if (!isOldUser) {
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
                    }

                    // Saving the new list
                    volunteerList.set(positionInList, volunteering);
                    volunteersDB.setValue(volunteerList);
                }
            });
        } else {
            addBtn.setVisibility(View.GONE);
        }

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
        if (isOldUser && volunteering.getVolunteerUID()==null) {
            chatBtn.setVisibility(View.GONE);
        } else {
            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Open the Chat and add the chat to chats list

                    // Check if chat between these  two users exists


                    // if exists the ChatExists() will put existing chat into chat;
                    if (!ChatExists()) {
                        // if not exists Create the chat and add it
                        chat.setChatID(newChatId);
                        chat.setSideAUid(firebaseUser.getUid());
                        if (isOldUser) {
                            chat.setSideBUid(volunteering.getVolunteerUID());
                            chat.setReceiverName(volunteering.getNameVolunteer());
                        } else {
                            chat.setSideBUid(volunteering.getOldUID());
                            chat.setReceiverName(volunteering.getNameOld());
                        }
                        // add the chat to chat list
                        chatsDB.setValue(newChatId+1);
                        chatsDB.child(String.valueOf(newChatId)).setValue(chat);
                    }

                    // open the chat
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
        }

        backBtn = view.findViewById(R.id.selected_volunteering_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private boolean ChatExists(){

        final boolean[] isExists = {false};

        chatsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Chat checkChat = ds.getValue(Chat.class);
                        if ((checkChat.getSideAUid().equals(volunteering.getOldUID())) &&
                                (checkChat.getSideBUid().equals(volunteering.getVolunteerUID()))) {
                            isExists[0] = true;
                            chat = checkChat;
                        }
                        if ((checkChat.getSideAUid().equals(volunteering.getVolunteerUID())) &&
                                (checkChat.getSideBUid().equals(volunteering.getOldUID()))) {
                            isExists[0] = true;
                            chat = checkChat;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return isExists[0];
    }

}