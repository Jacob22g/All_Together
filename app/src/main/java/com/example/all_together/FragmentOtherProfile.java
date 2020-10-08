package com.example.all_together;

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

public class FragmentOtherProfile extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");

    StorageReference storageRef;
    StorageReference imageStorageRef;

    CircleImageView changePicBtn;

    ImageButton chatBtn;

    TextView userNameTv;
    TextView userAddressTv;
    TextView userAgeTv;
    TextView userEmailTv;
    TextView numOfVolunteeringTv;
    TextView userLevelTv;

    String personName;
    String personGivenName;
    String personFamilyName;
    String personEmail;
    String personId;
    Uri personPhoto;

    String city;
    String country;

    ArrayList<String> volunteeringTypes = new ArrayList<>();
    ListView listView;

    TextView aboutMeTv;

    GoogleSignInAccount account;

    String otherProfileId;

    public FragmentOtherProfile(String otherProfileId) {
        this.otherProfileId = otherProfileId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // maybe will check here if old user or not
        View view = inflater.inflate(R.layout.fragment_other_profile ,container,false);

        userNameTv = view.findViewById(R.id.userFullNameTv);
        userAddressTv = view.findViewById(R.id.userAddressTv);
        userAgeTv = view.findViewById(R.id.userAgeTv);
        userEmailTv = view.findViewById(R.id.userEmailTv);
        numOfVolunteeringTv = view.findViewById(R.id.user_num_of_vol_tv);
        userLevelTv = view.findViewById(R.id.user_vol_lvl_tv);
        aboutMeTv = view.findViewById(R.id.about_me_tv);
        listView= view.findViewById(R.id.TypesOfVolunteering_list);
        changePicBtn = view.findViewById(R.id.change_profile_pic_btn);

        // Need to add the chat btn option

        chatBtn = view.findViewById(R.id.open_chat_from_profile_btn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open Chat:
            }
        });


        //-----------------------

        account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account!=null){
            personName = account.getDisplayName();
            personGivenName = account.getGivenName();
            personFamilyName = account.getFamilyName();
            personEmail = account.getEmail();
            personId = account.getId();
            personPhoto = account.getPhotoUrl();

            userNameTv.setText(personName);
            //Glide.with(getContext()).load(String.valueOf(personPhoto)).into(changePicBtn);
            userAgeTv.setText(" ");
        }

        //-----------------------

        storageRef = FirebaseStorage.getInstance().getReference();

        // otherProfileId Info load
        usersDB.child(otherProfileId).addListenerForSingleValueEvent(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        loadImage();

        return view;
    }

    private void loadImage(){
        imageStorageRef = storageRef.child(otherProfileId+"/profile_image");
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
    }
}