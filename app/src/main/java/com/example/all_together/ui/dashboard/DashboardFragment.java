package com.example.all_together.ui.dashboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.R;
import com.example.all_together.adapter.VolunteerAdapter;
import com.example.all_together.model.Volunteer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    final String TAG = "tag";

    List<Volunteer> volunteerList  = new ArrayList<>();;
    VolunteerAdapter adapter;
    RecyclerView recyclerView;

    FloatingActionButton fab;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("volunteerList");;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard,container,false);

        recyclerView =  view.findViewById(R.id.volunteer_recycler);
        adapter = new VolunteerAdapter(volunteerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user!=null){

                    ReadFirebaseDB();
                }
            }
        };

        adapter.setListener(new VolunteerAdapter.VolunteerListener() {
            @Override
            public void onVolunteerClicked(int position, View view) {

                Volunteer volunteer = volunteerList.get(position);
                volunteer.setCompleted(!volunteer.isCompleted());

                Toast.makeText(getContext(), "Volunteer "+position+" "+volunteer.isCompleted(), Toast.LENGTH_SHORT).show();

                // Need to update the DB
//                myRef.setValue(volunteerList);
//                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(volunteerList);
            }
        });

        fab = view.findViewById(R.id.dashboard_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                volunteerList.add(new Volunteer("Title","Description",false));
                adapter.notifyItemInserted(volunteerList.size());

                // Write to DB
                myRef.setValue(volunteerList);
            }
        });

        return view;
    }

    private void ReadFirebaseDB(){
        // Read from the database
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Volunteer list, Please wait..");
        progressDialog.show();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                volunteerList.clear();

                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Volunteer volunteer = ds.getValue(Volunteer.class);
                        volunteerList.add(volunteer);
                    }
                    adapter.notifyDataSetChanged();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
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
