package com.example.all_together;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    List<Volunteering> volunteerList  = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference volunteersDB = database.getReference("volunteerList");

    public VolunteeringFragment(Volunteering volunteering) {
        this.volunteering = volunteering;
    }

    public VolunteeringFragment(Volunteering volunteering, int positionInList) {
        this.volunteering = volunteering;
        this.positionInList = positionInList;
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

        nameTv.setText(volunteering.getName());
        dateTv.setText(volunteering.getDate());
        timeTv.setText(volunteering.getHour());
        locationTv.setText(volunteering.getLocationCity() +", "+ volunteering.getLocationStreet());

        descriptionTv.setText(volunteering.getDescription());
        if (volunteering.getDescription() == null)
            descriptionTv.setVisibility(View.GONE);

        // Read the DB to update it if needed
        volunteersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                volunteerList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Volunteering volunteering = ds.getValue(Volunteering.class);
                        volunteerList.add(volunteering);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addBtn = view.findViewById(R.id.selected_volunteering_add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser.getUid().equals(volunteering.getVolunteerUID())) {
                    addBtn.setText("Add me");
                    Toast.makeText(getContext(), "you have been removed", Toast.LENGTH_SHORT).show();
                    volunteering.setVolunteerUID(null);
                } else {
                    addBtn.setText("Remove me");
                    Toast.makeText(getContext(), "you have been added", Toast.LENGTH_SHORT).show();
                    volunteering.setVolunteerUID(firebaseUser.getUid());
                }
                volunteerList.set(positionInList, volunteering);
                volunteersDB.setValue(volunteerList);
            }
        });

        if (firebaseUser.getUid().equals(volunteering.getVolunteerUID()))
            addBtn.setText("Remove me");
        else
            if (volunteering.getVolunteerUID()==null)
                addBtn.setText("Add me");
            else {
                // Check if another user took it
                addBtn.setText("Volunteer taken");
                addBtn.setEnabled(false);
            }


        chatBtn = view.findViewById(R.id.selected_volunteering_chat_btn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "need to open a chat old and volunteer", Toast.LENGTH_SHORT).show();
            }
        });


        backBtn = view.findViewById(R.id.selected_volunteering_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }


}