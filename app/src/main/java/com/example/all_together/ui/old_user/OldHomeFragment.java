package com.example.all_together.ui.old_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.all_together.R;
import com.example.all_together.VolunteeringFragment;
import com.example.all_together.adapter.HomeOldVolunteeringAdapter;
import com.example.all_together.model.Volunteering;
import com.example.all_together.adapter.HomeVolunteeringAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class OldHomeFragment extends Fragment {

    Button nextVolCardBtn,oldVolCardBtn;
    CardView nextVolCardView,oldVolCardView;

    StorageReference storageRef;
    StorageReference imageStorageRef;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");
    DatabaseReference volunteersDB = database.getReference("volunteerList");

    List<Volunteering> userVolunteerList  = new ArrayList<>();
    List<Volunteering> volunteeringListNew = new ArrayList<>();
    List<Volunteering> volunteeringListOld = new ArrayList<>();

    RecyclerView recyclerViewOld;
    RecyclerView recyclerViewNew;

    CircleImageView profileImage;
    Uri profileImageUri_local;
    Uri downloadUrl;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    TextView userNameTv;
    TextView userAddressTv;
    TextView userAgeTv;
    TextView userEmailTv;

    String city;
    String country;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_old_home,container,false);

        userNameTv = rootView.findViewById(R.id.userFullNameTv);
        userAddressTv = rootView.findViewById(R.id.userAddressTv);
        userAgeTv = rootView.findViewById(R.id.userAgeTv);
        profileImage = rootView.findViewById(R.id.change_profile_pic_btn);

        storageRef = FirebaseStorage.getInstance().getReference();

        loadImage();


//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                final FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user!=null){
//                    // Load a list with all user volunteering
//                    volunteersDB.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            userVolunteerList.clear();
//                            if (snapshot.exists()){
//                                for (DataSnapshot ds : snapshot.getChildren()){
//                                    Volunteering volunteering = ds.getValue(Volunteering.class);
//                                    if (volunteering.getOldUID().equals(firebaseUser.getUid())) {
//                                        // add only my ones
//                                        userVolunteerList.add(volunteering);
//                                    }
//                                }
//                                // Create the lists after getting all users list
//                                CreateTheLists();
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.w("TAG", "onCancelled", error.toException());
//                        }
//                    });
//
//                    loadImage();
//                }
//            }
//        };


        usersDB.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds: snapshot.getChildren()){
                        switch (ds.getKey()){
                            case "age":
                                userAgeTv.setText(ds.getValue(String.class));
                                break;
                            case "user_name":
                                userNameTv.setText(ds.getValue(String.class));
                                break;
                            case "city":
                                city = ds.getValue(String.class);
                                break;
                            case "country":
                                country = ds.getValue(String.class);
                                break;
                        }
                    }
                }

                userAddressTv.setText(city + ", " + country);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Load a list with all user volunteering
        volunteersDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userVolunteerList.clear();

                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Volunteering volunteering = ds.getValue(Volunteering.class);
                        if (volunteering.getOldUID().equals(firebaseUser.getUid())) {
                            // add only my ones
                            userVolunteerList.add(volunteering);
                        }
                    }

                    // Create the lists after getting all users list
                    CreateTheLists();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "onCancelled", error.toException());
            }
        });


        nextVolCardView = rootView.findViewById(R.id.cardNextVol);
        oldVolCardView =  rootView.findViewById(R.id.cardOldVol);

        nextVolCardBtn = rootView.findViewById(R.id.next_vol_card_btn);
        nextVolCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextVolCardBtn.setBackground(getResources().getDrawable(R.drawable.color_back4_circle));
                oldVolCardBtn.setBackground(getResources().getDrawable(R.drawable.background_button_light));
                oldVolCardView.setVisibility(View.GONE);
                nextVolCardView.setVisibility(View.VISIBLE);
            }
        });
        oldVolCardBtn = rootView.findViewById(R.id.old_vol_card_btn);
        oldVolCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextVolCardBtn.setBackground(getResources().getDrawable(R.drawable.background_button_light));
                oldVolCardBtn.setBackground(getResources().getDrawable(R.drawable.color_back4_circle));
                nextVolCardView.setVisibility(View.GONE);
                oldVolCardView.setVisibility(View.VISIBLE);

            }
        });

        recyclerViewNew = rootView.findViewById(R.id.recyclerNew);
        recyclerViewNew.setHasFixedSize(true);
        recyclerViewNew.setLayoutManager(new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL, false));

        recyclerViewOld = rootView.findViewById(R.id.recyclerOld);
        recyclerViewOld.setHasFixedSize(true);
        recyclerViewOld.setLayoutManager(new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL, false));

        return rootView;

    }

    private void CreateTheLists(){
        // Create the lists:

        String currentDateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        for (Volunteering volunteering: userVolunteerList){
            // Check if it happened or not
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date volunteeringDate = simpleDateFormat.parse(volunteering.getDate());
                Date currentDate = simpleDateFormat.parse(currentDateString);
                if (volunteeringDate.compareTo(currentDate) >= 0){
                    volunteeringListNew.add(volunteering);
                } else{
                    volunteeringListOld.add(volunteering);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

//        // new volunteering list
//        HomeVolunteeringAdapter volunteeringAdapterNew = new HomeVolunteeringAdapter(volunteeringListNew);
//        volunteeringAdapterNew.setListener(new HomeVolunteeringAdapter.MyVolunteeringInfoListener() {
//            @Override
//            public void onVolunteeringClicked(int position, View view) {
//                Volunteering volunteering = volunteeringListNew.get(position);
//                Fragment fragment = new VolunteeringFragment(volunteering);
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.drawerLayout_activityolduser, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });
//        recyclerViewNew.setAdapter(volunteeringAdapterNew);
//
//        // old volunteering list
//        HomeVolunteeringAdapter volunteeringAdapterOld = new HomeVolunteeringAdapter(volunteeringListOld);
//        volunteeringAdapterOld.setListener(new HomeVolunteeringAdapter.MyVolunteeringInfoListener() {
//            @Override
//            public void onVolunteeringClicked(int position, View view) {
//                Volunteering volunteering = volunteeringListOld.get(position);
//                Fragment fragment = new VolunteeringFragment(volunteering);
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.drawerLayout_activityolduser, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });
//        recyclerViewOld.setAdapter(volunteeringAdapterOld);

        // new volunteering list

        HomeOldVolunteeringAdapter volunteeringAdapterNew = new HomeOldVolunteeringAdapter(volunteeringListNew);
        volunteeringAdapterNew.setListener(new HomeOldVolunteeringAdapter.MyVolunteeringInfoListener() {
            @Override
            public void onVolunteeringClicked(int position, View view) {
                Volunteering volunteering = volunteeringListNew.get(position);
                Fragment fragment = new VolunteeringFragment(volunteering);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.drawerLayout_activityolduser, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        recyclerViewNew.setAdapter(volunteeringAdapterNew);

        // old volunteering list
        HomeOldVolunteeringAdapter volunteeringAdapterOld = new HomeOldVolunteeringAdapter(volunteeringListOld);
        volunteeringAdapterOld.setListener(new HomeOldVolunteeringAdapter.MyVolunteeringInfoListener() {
            @Override
            public void onVolunteeringClicked(int position, View view) {
                Volunteering volunteering = volunteeringListOld.get(position);
                Fragment fragment = new VolunteeringFragment(volunteering);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.drawerLayout_activityolduser, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        recyclerViewOld.setAdapter(volunteeringAdapterOld);
    }


    private void loadImage(){

        imageStorageRef = storageRef.child(firebaseUser.getUid()+"/profile_image");

        imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                if (getContext()!=null) {
                    Glide.with(getContext())
                            .load(imageURL)
                            .into(profileImage);
                }
            }
        });

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        firebaseAuth.addAuthStateListener(authStateListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        firebaseAuth.removeAuthStateListener(authStateListener);
//    }
}
