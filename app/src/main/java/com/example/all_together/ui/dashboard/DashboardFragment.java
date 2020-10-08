package com.example.all_together.ui.dashboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.R;
import com.example.all_together.VolunteeringFragment;
import com.example.all_together.adapter.DashboardVolunteeringAdapter;
import com.example.all_together.model.Volunteering;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment {

    final String TAG = "tag";

    List<Volunteering> volunteerList  = new ArrayList<>();
    DashboardVolunteeringAdapter adapter;
    RecyclerView recyclerView;

    FloatingActionButton fab;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference volunteersDB = database.getReference("volunteerList");
//    DatabaseReference usersDB = database.getReference("users");

    CoordinatorLayout coordinatorLayout;
    RelativeLayout filterRelativeLayout;
    Boolean flag = true;
    Button filterSubmitBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_dashboard,container,false);

        coordinatorLayout = view.findViewById(R.id.coordinator_dashboard);

        recyclerView =  view.findViewById(R.id.volunteer_recycler);
        adapter = new DashboardVolunteeringAdapter(volunteerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        filterSubmitBtn = view.findViewById(R.id.filterBtn);
        filterSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Collections.sort((List<Comparable>) volunteersDB);
//                adapter.notifyDataSetChanged();
            }
        });

        final ImageView showFilterNavBtn = view.findViewById(R.id.showFiler);
        filterRelativeLayout = view.findViewById(R.id.filterRelativeLayout);
        filterRelativeLayout.setVisibility(View.GONE);
        showFilterNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    filterRelativeLayout.setVisibility(View.VISIBLE);
                    showFilterNavBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_up));
                    flag = false;
                }
                else {
                    filterRelativeLayout.setVisibility(View.GONE);
                    showFilterNavBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_down));
                    flag = true;
                }
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user!=null){
                    ReadFirebaseDB();
                }
            }
        };

//        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0 ,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//                final int position = viewHolder.getAdapterPosition();
//                final Volunteering item = volunteerList.get(position);
//
//                volunteerList.remove(viewHolder.getAdapterPosition());
//                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//
//                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item removed from list", Snackbar.LENGTH_LONG)
//                        .setAction("UNDO", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                volunteerList.add(position, item);
//                                adapter.notifyItemInserted(position);
//                                volunteersDB.setValue(volunteerList);
//                            }
//                        });
//                snackbar.show();
//
//                volunteersDB.setValue(volunteerList);
//            }
//        };
//
//        ItemTouchHelper helper = new ItemTouchHelper(callback);
//        helper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
        adapter.setListener(new DashboardVolunteeringAdapter.VolunteerListener() {
            @Override
            public void onVolunteerClicked(int position, View view) {

                Volunteering volunteering = volunteerList.get(position);

//                adapter.notifyItemChanged(position);
//                Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();

                // Open This volunteering
//                Fragment fragment = new VolunteeringFragment(volunteering, position);                // Maybe would change it and use the id and not position
                Fragment fragment = new VolunteeringFragment(volunteering);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.replace(R.id.drawerLayout_activitymainapp, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        return view;
    }

    private void ReadFirebaseDB(){
        // Read from the database
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Volunteering list, Please wait..");
        progressDialog.show();

        volunteersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                volunteerList.clear();

                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Volunteering volunteering = ds.getValue(Volunteering.class);

//                        volunteerList.add(volunteering);
                        if (volunteering.getVolunteerUID()==null){
                            volunteerList.add(volunteering);
                        }

                    }
                    adapter.notifyDataSetChanged();
                }

                // Sort the list by date
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Collections.sort(volunteerList, new Comparator<Volunteering>() {
                    public int compare(Volunteering v1, Volunteering v2) {
                        Date volunteeringDate1 = null, volunteeringDate2 = null;
                        int result;
                        try {
                            volunteeringDate1 = simpleDateFormat.parse(v1.getDate());
                            volunteeringDate2 = simpleDateFormat.parse(v2.getDate());
                            result = volunteeringDate1.compareTo(volunteeringDate2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            result=0;
                        }
                        return result;
                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

//        final VolunteeringComparator comparator = new VolunteeringComparator();
//        comparator.compare(volunteerList.get(0),volunteerList.get(1));



    }


//    static class VolunteeringComparator implements Comparator<Volunteering> {
//        public int compare(Volunteering v1, Volunteering v2) {
//            //possibly check for nulls to avoid NullPointerException
//            return v1.getDate().compareTo(v2.getDate());
//        }
//    }


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
