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

    private StorageReference storageRef;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");

    CircleImageView changePicBtn;
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

        storageRef = FirebaseStorage.getInstance().getReference();

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
                nextVolCardBtn.setBackground(getResources().getDrawable(R.drawable.background_button_dark));
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
                oldVolCardBtn.setBackground(getResources().getDrawable(R.drawable.background_button_dark));
                nextVolCardView.setVisibility(View.GONE);
                oldVolCardView.setVisibility(View.VISIBLE);

            }
        });

        RecyclerView recyclerViewNew = rootView.findViewById(R.id.recyclerNew);
        recyclerViewNew.setHasFixedSize(true);
        recyclerViewNew.setLayoutManager(new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL, false));

        final List<Volunteering>volunteeringListNew = new ArrayList<>();
        volunteeringListNew.add(new Volunteering("Lidan", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListNew.add(new Volunteering("Lida", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListNew.add(new Volunteering("Lid", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListNew.add(new Volunteering("Li", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));

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
        volunteeringListOld.add(new Volunteering("Avi", "Tel-Aviv","st", "10/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListOld.add(new Volunteering("Avi", "Tel-Aviv","st", "10/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListOld.add(new Volunteering("Avi", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListOld.add(new Volunteering("Avi", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));

        HomeVolunteeringAdapter volunteeringAdapterOld = new HomeVolunteeringAdapter(volunteeringListOld);
        recyclerViewOld.setAdapter(volunteeringAdapterOld);

        //downloadImage();

        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
            profileImageUri_local = data.getData();
            Glide.with(getContext()).load(profileImageUri_local).into(changePicBtn);

            uploadImage();

        }
    }

    private void uploadImage(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

//        Uri file = Uri.fromFile(new File(profileImageUri.toString()));
        StorageReference imageStoreRef = storageRef.child(firebaseUser.getUid()+"/profile_image");

        imageStoreRef.putFile(profileImageUri_local)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        progressDialog.dismiss();

//                                downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                downloadUrl = uri;
                            }
                        });

                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Upload Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        // Progress bar
                        double progress = (100.0*snapshot.getBytesTransferred()/snapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    private void downloadImage(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading profile Please wait..");
        progressDialog.show();

        File localFile = null;
        try {

            localFile = File.createTempFile("images", "jpg");
            final File finalLocalFile = localFile;

            StorageReference imageStoreRef = storageRef.child(firebaseUser.getUid()+"/profile_image");

            imageStoreRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            Uri profileUri = Uri.fromFile(finalLocalFile);
                            Glide.with(getContext()).load(profileUri).into(changePicBtn);

                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    Toast.makeText(getContext(), "Download Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

//            Uri profileUri = Uri.fromFile(localFile);
//            Glide.with(getContext()).load(profileUri).into(changePicBtn);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
