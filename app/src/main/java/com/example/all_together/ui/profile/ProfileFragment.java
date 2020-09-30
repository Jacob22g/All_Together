package com.example.all_together.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.example.all_together.R;
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
import com.google.firebase.inappmessaging.dagger.multibindings.StringKey;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");

    StorageReference storageRef;
    StorageReference imageStorageRef;

    final int IMAGE_REQUEST = 111;

    ImageButton changePicBtn;
    Uri profileImageUri_local;
    Uri downloadUrl;

    TextView userNameTv;
    TextView userAddressTv;
    TextView userAgeTv;
    TextView userEmailTv;

    String city;
    String country;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        userNameTv = view.findViewById(R.id.userFullNameTv);
        userAddressTv = view.findViewById(R.id.userAddressTv);
        userAgeTv = view.findViewById(R.id.userAgeTv);
        userEmailTv = view.findViewById(R.id.userEmailTv);

        storageRef = FirebaseStorage.getInstance().getReference();

        changePicBtn = view.findViewById(R.id.change_profile_pic_btn);
        changePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,IMAGE_REQUEST);
            }
        });

        usersDB.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
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

                userAddressTv.setText(city+", "+country);

                userEmailTv.setText(firebaseUser.getEmail());

////                loadImage();
//
//                // gs://all-together-e88a5.appspot.com/vnnGxqosTfdClSaYShIrPNEwta83/profile_image
//                imageStorageRef = storageRef.child(firebaseUser.getUid()+"/profile_image");
//
//                Glide.with(getContext())
//                        .load(imageStorageRef)
//                        .into(changePicBtn);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        loadImage();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){

            profileImageUri_local = data.getData();
//            Glide.with(getContext())
//                    .load(profileImageUri_local)
//                    .into(changePicBtn);

            // Should be a service
            uploadImage();

//            Glide.with(getContext())
//                    .load(imageStorageRef)
//                    .into(changePicBtn);
        }
    }

    private void uploadImage(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Replacing Image...");
        progressDialog.show();

//        Uri file = Uri.fromFile(new File(profileImageUri.toString()));
        imageStorageRef = storageRef.child(firebaseUser.getUid()+"/profile_image");

        imageStorageRef.putFile(profileImageUri_local)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        progressDialog.dismiss();

                        // Replace the image
                        loadImage();

//                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
//                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                String imageUrl = uri.toString();
//                                downloadUrl = uri;
//                            }
//                        });

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

    // Shows the same image all the time!
    private void loadImage(){

        imageStorageRef = storageRef.child(firebaseUser.getUid()+"/profile_image");

        imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getContext())
                        .load(imageURL)
                        .into(changePicBtn);
            }
        });

//        Glide.with(getContext())
//                .load(imageStorageRef)
//                .into(changePicBtn);

        // This is downloading the image

//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Loading profile Please wait..");
//        progressDialog.show();
//
//        File localFile = null;
//        try {
//
//            localFile = File.createTempFile("images", "jpg");
//            final File finalLocalFile = localFile;
//
//            StorageReference imageStoreRef = storageRef.child(firebaseUser.getUid()+"/profile_image");
//
//            imageStoreRef.getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            // Successfully downloaded data to local file
//                            Uri profileUri = Uri.fromFile(finalLocalFile);
//                            Glide.with(getContext()).load(profileUri).into(changePicBtn);
//
//                            progressDialog.dismiss();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle failed download
//                    Toast.makeText(getContext(), "Download Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//
////            Uri profileUri = Uri.fromFile(localFile);
////            Glide.with(getContext()).load(profileUri).into(changePicBtn);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
