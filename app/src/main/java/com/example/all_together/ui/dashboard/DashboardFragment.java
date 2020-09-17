package com.example.all_together.ui.dashboard;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.R;
import com.example.all_together.adapter.VolunteerAdapter;
import com.example.all_together.model.Volunteer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardFragment extends Fragment {

    final String TAG = "tag";

    List<Volunteer> volunteerList  = new ArrayList<>();
    VolunteerAdapter adapter;
    RecyclerView recyclerView;

    FloatingActionButton fab;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("volunteerList");;

    CoordinatorLayout coordinatorLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_dashboard,container,false);

        coordinatorLayout = view.findViewById(R.id.coordinator_dashboard);

        recyclerView =  view.findViewById(R.id.volunteer_recycler);
        adapter = new VolunteerAdapter(volunteerList);
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

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN ,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int toPosition = target.getAdapterPosition();
                int fromPosition = viewHolder.getAdapterPosition();

                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(volunteerList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(volunteerList, i, i - 1);
                    }
                }

                adapter.notifyItemMoved(fromPosition, toPosition);

                // save list
                // Need to update the DB after release
//                myRef.setValue(volunteerList);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                final Volunteer item = volunteerList.get(position);

                volunteerList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item removed from list", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                volunteerList.add(position, item);
                                adapter.notifyItemInserted(position);
                                myRef.setValue(volunteerList);
                            }
                        });
                //snackbar.setActionTextColor(Color.MAGENTA);
                snackbar.show();

                myRef.setValue(volunteerList);
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
        adapter.setListener(new VolunteerAdapter.VolunteerListener() {
            @Override
            public void onVolunteerClicked(int position, View view) {

                Volunteer volunteer = volunteerList.get(position);
                volunteer.setCompleted(!volunteer.isCompleted());
                adapter.notifyItemChanged(position);

                Toast.makeText(getContext(), "Volunteer "+position+" "+volunteer.isCompleted(), Toast.LENGTH_SHORT).show();

                // Need to update the DB
                myRef.setValue(volunteerList);

//                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(volunteerList);
            }
        });

        fab = view.findViewById(R.id.dashboard_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                volunteerList.add(new Volunteer("Title"+volunteerList.size(),"Description",false));
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
