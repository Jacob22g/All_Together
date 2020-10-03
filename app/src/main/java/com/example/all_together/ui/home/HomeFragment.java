package com.example.all_together.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.all_together.R;
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
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    final int IMAGE_REQUEST = 111;

    Button nextVolCardBtn,oldVolCardBtn;
    CardView nextVolCardView,oldVolCardView;

    StorageReference storageRef;
    StorageReference imageStorageRef;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");
    DatabaseReference volunteersDB = database.getReference("volunteerList");

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
        final View rootView = inflater.inflate(R.layout.fragment_home,container,false);

        userNameTv = rootView.findViewById(R.id.userFullNameTv);
        userAddressTv = rootView.findViewById(R.id.userAddressTv);
        userAgeTv = rootView.findViewById(R.id.userAgeTv);
        profileImage = rootView.findViewById(R.id.change_profile_pic_btn);

        storageRef = FirebaseStorage.getInstance().getReference();

        loadImage();

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

        RecyclerView recyclerViewNew = rootView.findViewById(R.id.recyclerNew);
        recyclerViewNew.setHasFixedSize(true);
        recyclerViewNew.setLayoutManager(new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL, false));

        final List<Volunteering>volunteeringListNew = new ArrayList<>();
//        volunteeringListNew.add(new Volunteering("Lidan", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
//        volunteeringListNew.add(new Volunteering("Lida", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
//        volunteeringListNew.add(new Volunteering("Lid", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
//        volunteeringListNew.add(new Volunteering("Li", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));

        HomeVolunteeringAdapter volunteeringAdapterNew = new HomeVolunteeringAdapter(volunteeringListNew);
        volunteeringAdapterNew.setListener(new HomeVolunteeringAdapter.MyVolunteeringInfoListener() {
            @Override
            public void onVolunteeringClicked(int position, View view) {
                Toast.makeText(view.getContext(), "My name is:" + volunteeringListNew.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewNew.setAdapter(volunteeringAdapterNew);


        RecyclerView recyclerViewOld = rootView.findViewById(R.id.recyclerOld);
        recyclerViewOld.setHasFixedSize(true);
        recyclerViewOld.setLayoutManager(new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL, false));

        List<Volunteering>volunteeringListOld = new ArrayList<>();
//        volunteeringListOld.add(new Volunteering("Avi", "Tel-Aviv","st", "10/05/20", "12:15", "Shopping","bla bli blopy","1"));
//        volunteeringListOld.add(new Volunteering("Avi", "Tel-Aviv","st", "10/05/20", "12:15", "Shopping","bla bli blopy","1"));
//        volunteeringListOld.add(new Volunteering("Avi", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
//        volunteeringListOld.add(new Volunteering("Avi", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));

        HomeVolunteeringAdapter volunteeringAdapterOld = new HomeVolunteeringAdapter(volunteeringListOld);
        recyclerViewOld.setAdapter(volunteeringAdapterOld);

        return rootView;

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
}
