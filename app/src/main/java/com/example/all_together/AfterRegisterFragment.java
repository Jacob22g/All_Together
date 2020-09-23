package com.example.all_together;

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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.app.Activity.RESULT_OK;

public class AfterRegisterFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");

    final int IMAGE_REQUEST = 111;

    EditText firstNameEt;
    EditText laseNameEt;
    EditText ageEt;
    EditText countryEt;
    EditText cityEt;
    ImageView profileImage;

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

        firstNameEt = view.findViewById(R.id.first_name_add_info);
        laseNameEt = view.findViewById(R.id.last_name_add_info);
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

                final String firstName = firstNameEt.getText().toString();
                final String lastName = laseNameEt.getText().toString();
                final String age = ageEt.getText().toString();
                final String country = countryEt.getText().toString();
                final String city = cityEt.getText().toString();

                if(TextUtils.isEmpty(firstName)){
                    firstNameEt.setError("First name is Required");
                    return;
                }

                if(TextUtils.isEmpty(lastName)){
                    laseNameEt.setError("Lase name is Required");
                    return;
                }

                if(TextUtils.isEmpty(age)){
                    ageEt.setError("Age is Required");
                    return;
                }

                if(TextUtils.isEmpty(country)){
                    countryEt.setError("Country is Required");
                    return;
                }

                if(TextUtils.isEmpty(city)){
                    cityEt.setError("City is Required");
                    return;
                }

                // update the DB
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("first_name").setValue(firstName);
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("last_name").setValue(lastName);
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("age").setValue(age);
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("country").setValue(country);
                usersDB.child(firebaseAuth.getCurrentUser().getUid()).child("city").setValue(city);

//                usersDB.child(firebaseAuth.getCurrentUser().getUid()).setValue(profileImage);

                callback.onAfterRegister();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){

            Uri profileImageUri = data.getData();
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