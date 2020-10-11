package com.example.all_together;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class AfterOldRegisterFragment extends Fragment {

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

    boolean isImageSelected;

    interface OnAfterOldRegisterFragmentListener {
        void onAfterOldRegister();
    }

    AfterOldRegisterFragment.OnAfterOldRegisterFragmentListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (AfterOldRegisterFragment.OnAfterOldRegisterFragmentListener) context;
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

        isImageSelected = false;

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isImageSelected = true;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,IMAGE_REQUEST);
            }
        });

        Button submitBtn = view.findViewById(R.id.submit_add_info);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userName = userNameEt.getText().toString();
                final String age = ageEt.getText().toString();
                final String country = countryEt.getText().toString();
                final String city = cityEt.getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    userNameEt.setError(getResources().getString(R.string.name_required));
                    return;
                }

                if (TextUtils.isEmpty(age)) {
                    ageEt.setError(getResources().getString(R.string.age_required));
                    return;
                }

                if (TextUtils.isEmpty(country)) {
                    countryEt.setError(getResources().getString(R.string.country_required));
                    return;
                }

                if (TextUtils.isEmpty(city)) {
                    cityEt.setError(getResources().getString(R.string.city_required));
                    return;
                }

                // update the DB
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("user_name").setValue(userName);
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("age").setValue(age);
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("country").setValue(country);
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("city").setValue(city);

                // Set user as old
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("is_old_user").setValue(true);

                // check if an image was selected
                if (isImageSelected)
                    uploadImage();

                callback.onAfterOldRegister();

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

        StorageReference imageStoreRef = storageRef.child(firebaseUser.getUid()+"/profile_image");

        imageStoreRef.putFile(profileImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
//                        progressDialog.dismiss();

                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                downloadUrl = uri;
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getContext(), "Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                        isImageSelected = false;
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){

            profileImageUri = data.getData();
            Glide.with(getContext()).load(profileImageUri).into(profileImage);

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