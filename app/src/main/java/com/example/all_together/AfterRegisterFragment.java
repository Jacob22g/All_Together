package com.example.all_together;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class AfterRegisterFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");
    private StorageReference storageRef;

    final int IMAGE_REQUEST = 111;

    EditText userNameEt;
    EditText ageEt;
    EditText countryEt;
    EditText cityEt;
    ImageView profileImage;

    Uri profileImageUri;

    Uri downloadUrl;

    interface OnAfterRegisterFragmentListener {
        void onAfterRegister();
    }

    AfterRegisterFragment.OnAfterRegisterFragmentListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (AfterRegisterFragment.OnAfterRegisterFragmentListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement OnAfterRegisterFragmentListener interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_after_register, container, false);

        // Storage to save Image
        storageRef = FirebaseStorage.getInstance().getReference();

        userNameEt = view.findViewById(R.id.full_name_add_info);
        ageEt = view.findViewById(R.id.age_add_info);
        countryEt = view.findViewById(R.id.country_add_info);
        cityEt = view.findViewById(R.id.city_add_info);
        profileImage = view.findViewById(R.id.image_profile_add_info);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,IMAGE_REQUEST);
            }
        });

        Button submitBtn = view.findViewById(R.id.submit_add_info);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (profileImageUri==null){

                    Toast.makeText(getContext(), "Upload user image!", Toast.LENGTH_SHORT).show();

                } else {

                    final String userName = userNameEt.getText().toString();
                    final String age = ageEt.getText().toString();
                    final String country = countryEt.getText().toString();
                    final String city = cityEt.getText().toString();

                    if (TextUtils.isEmpty(userName)) {
                        userNameEt.setError("First name is Required");
                        return;
                    }

                    if (TextUtils.isEmpty(age)) {
                        ageEt.setError("Age is Required");
                        return;
                    }

                    if (TextUtils.isEmpty(country)) {
                        countryEt.setError("Country is Required");
                        return;
                    }

                    if (TextUtils.isEmpty(city)) {
                        cityEt.setError("City is Required");
                        return;
                    }

                    // update the DB
                    usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("user_name").setValue(userName);
                    usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("age").setValue(age);
                    usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("country").setValue(country);
                    usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("city").setValue(city);

                    // Save Email in DB
                    usersDB.child(firebaseUser.getUid()).child("EmailAddress").setValue(firebaseUser.getEmail());

                    // Number of Volunteering and level
                    usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("number_of_volunteering").setValue(0);
                    usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("volunteering_level").setValue("Beginner");

                    // Set user as not old
                    usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("is_old_user").setValue(false);

//                usersDB.child(firebaseAuth.getCurrentUser().getUid()).setValue(profileImage);

                    uploadImage();

                    callback.onAfterRegister();

                }
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 firebaseUser = firebaseAuth.getCurrentUser();
            }
        };

        return view;
    }

    private void uploadImage(){

//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setTitle("Uploading...");
//        progressDialog.show();

//        Uri file = Uri.fromFile(new File(profileImageUri.toString()));
        StorageReference imageStoreRef = storageRef.child(firebaseUser.getUid()+"/profile_image");

        imageStoreRef.putFile(profileImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
//                        progressDialog.dismiss();

//                                downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                downloadUrl = uri;
                                //createNewPost(imageUrl);
                            }
                        });

//                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
//                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                        // Progress bar
//                        double progress = (100.0*snapshot.getBytesTransferred()/snapshot
//                                .getTotalByteCount());
//                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
//                    }
//                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){

            profileImageUri = data.getData();
            Glide.with(getContext()).load(profileImageUri).into(profileImage);

            // Need to save image in firebase

        }
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