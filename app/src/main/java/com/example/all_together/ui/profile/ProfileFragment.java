package com.example.all_together.ui.profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.example.all_together.R;
import com.example.all_together.model.Volunteering;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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

    CircleImageView changePicBtn;

    Uri profileImageUri_local;
    Uri downloadUrl;

    Button profileEditBtn;

    TextView userNameTv;
    TextView userAddressTv;
    TextView userAgeTv;
    TextView userEmailTv;
    TextView numOfVolunteeringTv;
    TextView userLevelTv;

    String personName;
    //    String personAge;
//    String personAddress;
    String personGivenName;
    String personFamilyName;
    String personEmail;
    String personId;
    Uri personPhoto;

    String city;
    String country;

    Button TypesOfVolunteeringBtn;
    ArrayList<String> volunteeringTypes = new ArrayList<>();
    ListView listView;

    Button aboutMeBtn;
    TextView aboutMeTv;

    GoogleSignInAccount account;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        userNameTv = view.findViewById(R.id.userFullNameTv);
        userAddressTv = view.findViewById(R.id.userAddressTv);
        userAgeTv = view.findViewById(R.id.userAgeTv);
        userEmailTv = view.findViewById(R.id.userEmailTv);
        numOfVolunteeringTv = view.findViewById(R.id.user_num_of_vol_tv);
        userLevelTv = view.findViewById(R.id.user_vol_lvl_tv);
        aboutMeTv = view.findViewById(R.id.about_me_tv);
        aboutMeBtn = view.findViewById(R.id.about_me_edit_btn);
        profileEditBtn = view.findViewById(R.id.profile_edit_btn);

        listView= view.findViewById(R.id.TypesOfVolunteering_list);

        account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account!=null){
            personName = account.getDisplayName();
            personGivenName = account.getGivenName();
            personFamilyName = account.getFamilyName();
            personEmail = account.getEmail();
            personId = account.getId();
            personPhoto = account.getPhotoUrl();

            userNameTv.setText(personName);
//            Glide.with(getContext()).load(String.valueOf(personPhoto)).into(changePicBtn);
            userAgeTv.setText(" ");
        }

        storageRef = FirebaseStorage.getInstance().getReference();

        // Save Volunteering types change
        TypesOfVolunteeringBtn = view.findViewById(R.id.TypesOfVolunteering_edit_btn);
        TypesOfVolunteeringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final ArrayList<String> types = new ArrayList<>();
                final ArrayList<String> types = volunteeringTypes;
                final String[] typesArr = getResources().getStringArray(R.array.volunteering_categories_array);

                boolean[] isPreChecked = new boolean[typesArr.length];
                for (int i=0;i<typesArr.length;i++){
                    isPreChecked[i] = false;
                    for (String t : volunteeringTypes){
                        if (t.equals(typesArr[i]))
                            isPreChecked[i] = true;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Please pick types")
                        .setMultiChoiceItems(R.array.volunteering_categories_array, isPreChecked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked)
                            types.add(typesArr[which]);
                        else
                            types.remove(typesArr[which]);
                    }
                }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), types.toString(), Toast.LENGTH_SHORT).show();
                        volunteeringTypes = types;
                        usersDB.child(firebaseUser.getUid()).child("volunteeringTypes").setValue(types);

                        // Display the list
                        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, volunteeringTypes);
                        listView.setAdapter(adapter);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(R.drawable.volunteer_icon)
                  .show();
            }
        });

        // save about me
        aboutMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open et dialog- custom dialog the save it in the tv

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.edit_aboutme_dialog, null);

                final EditText aboutMeEt = dialogView.findViewById(R.id.about_me_et_dialog);

                final AlertDialog show = builder.setView(dialogView).show();

                Button saveAboutMeBtn = dialogView.findViewById(R.id.about_me_et_btn_dialog);
                saveAboutMeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        aboutMeTv.setText(aboutMeEt.getText().toString());

                        usersDB.child(firebaseUser.getUid()).child("aboutMe").setValue(aboutMeTv.getText().toString());

                        show.dismiss();
                    }
                });

            }
        });

        profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open dialog of editing name address and age
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.edit_user_info_dialog, null);

                final EditText nameEt = dialogView.findViewById(R.id.full_name_edit_user_info);
                final EditText cityEt = dialogView.findViewById(R.id.city_edit_user_info);
                final EditText countryEt = dialogView.findViewById(R.id.country_edit_user_info);
                final EditText ageEt = dialogView.findViewById(R.id.age_edit_user_info);

                final AlertDialog show = builder.setView(dialogView).show();

                Button saveAboutMeBtn = dialogView.findViewById(R.id.save_edit_user_dialog);
                saveAboutMeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!nameEt.getText().toString().isEmpty()) {
                            userNameTv.setText(nameEt.getText().toString());
                            usersDB.child(firebaseUser.getUid()).child("user_name").setValue(nameEt.getText().toString());
                        }
                        if (!cityEt.getText().toString().isEmpty()) {
                            city = (cityEt.getText().toString());
                            userAddressTv.setText(city + ", " + country);
                            usersDB.child(firebaseUser.getUid()).child("city").setValue(cityEt.getText().toString());
                        }
                        if (!countryEt.getText().toString().isEmpty()) {
                            country = (countryEt.getText().toString());
                            userAddressTv.setText(city + ", " + country);
                            usersDB.child(firebaseUser.getUid()).child("country").setValue(countryEt.getText().toString());
                        }
                        if (!ageEt.getText().toString().isEmpty()) {
                            userAgeTv.setText(ageEt.getText().toString());
                            usersDB.child(firebaseUser.getUid()).child("age").setValue(ageEt.getText().toString());
                        }
                        show.dismiss();
                    }
                });
            }
        });

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
                            case "number_of_volunteering":
                                numOfVolunteeringTv.setText(String.valueOf(ds.getValue(Integer.class)));
                                break;
                            case "volunteering_level":
                                userLevelTv.setText(ds.getValue(String.class));
                                break;
                            case "volunteeringTypes":
                                for (DataSnapshot d : ds.getChildren()){
                                    String str = d.getValue(String.class);
                                    volunteeringTypes.add(str);
                                }
                                // Display the list
                                if (getContext()!= null) {
                                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, volunteeringTypes);
                                    listView.setAdapter(adapter);
                                }
                                break;
                            case "aboutMe":
                                aboutMeTv.setText(ds.getValue(String.class));
                                break;
                        }

                    }

                }
                    userAddressTv.setText(city + ", " + country);
                    userEmailTv.setText(firebaseUser.getEmail());

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

        setUserVolunteeringLevel();

        return view;
    }

    private void setUserVolunteeringLevel() {
//        String userLevel = userLevelTv.getText().toString();
//        int numOfVolunteering = Integer.getInteger(numOfVolunteeringTv.getText().toString());
//        if (numOfVolunteering>5 && numOfVolunteering<=15 && !userLevel.equals(getResources().getString(R.string.good))){
//            userLevel = getResources().getString(R.string.good);
//            usersDB.child(firebaseUser.getUid()).child("volunteering_level").setValue(userLevel);
//        } else if (numOfVolunteering>15 && numOfVolunteering<=30 && !userLevel.equals(getResources().getString(R.string.excellent))){
//            userLevel =  getResources().getString(R.string.excellent);
//            usersDB.child(firebaseUser.getUid()).child("volunteering_level").setValue(userLevel);
//        } else if (numOfVolunteering>30 && numOfVolunteering<=50 && !userLevel.equals(getResources().getString(R.string.superior))){
//            userLevel =  getResources().getString(R.string.superior);
//            usersDB.child(firebaseUser.getUid()).child("volunteering_level").setValue(userLevel);
//        } else if (numOfVolunteering>50 && !userLevel.equals(getResources().getString(R.string.outstanding))){
//            userLevel =  getResources().getString(R.string.outstanding);
//            usersDB.child(firebaseUser.getUid()).child("volunteering_level").setValue(userLevel);
//        } else {
//            if (!userLevel.equals(getResources().getString(R.string.beginner))) {
//                userLevel = getResources().getString(R.string.beginner);
//                usersDB.child(firebaseUser.getUid()).child("volunteering_level").setValue(userLevel);
//            }
//        }
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

    private void loadImage(){

        imageStorageRef = storageRef.child(firebaseUser.getUid()+"/profile_image");

        imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                if (getContext()!=null) {
                    Glide.with(getContext())
                            .load(imageURL)
                            .into(changePicBtn);
                }
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