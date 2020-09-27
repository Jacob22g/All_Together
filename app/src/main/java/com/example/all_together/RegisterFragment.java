package com.example.all_together;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterFragment extends Fragment {

    public static int MIN_CHARACTERS_PASSWORD = 6;
    public static int PHONE_NUMBER_CHARACTERS = 9;

    interface OnRegisterFragmentListener {
        void onRegister(String email, String password);
        void onPhoneRegister(String phoneNumber,String password);
    }

    OnRegisterFragmentListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            callback = (OnRegisterFragmentListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement OnRegisterFragmentListener interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.register_fragment, container, false);

        final Button layout1 = rootView.findViewById(R.id.layout1);
        final Button layout2 = rootView.findViewById(R.id.layout2);
        Button back = rootView.findViewById(R.id.back_to_login_btn);

        final CardView cardView = rootView.findViewById(R.id.cardView);
        final CardView cardView1 = rootView.findViewById(R.id.cardView1);
        final CardView cardView2 = rootView.findViewById(R.id.cardView2);

        layout1.setOnClickListener(new View.OnClickListener() { //layout for volunteer
            @Override
            public void onClick(View v) {
                cardView2.setVisibility(View.GONE);
                cardView1.setVisibility(View.VISIBLE);
                layout1.setBackgroundColor(getResources().getColor(R.color.colorPrimary) );
                layout2.setBackgroundColor(getResources().getColor(R.color.grayBlueColor) );
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() { //layout for need help
            @Override
            public void onClick(View v) {
                cardView1.setVisibility(View.GONE);
                cardView2.setVisibility(View.VISIBLE);
                layout1.setBackgroundColor(getResources().getColor(R.color.grayBlueColor));
                layout2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onRegister("","");

//                cardView1.setVisibility(View.GONE);
//                cardView2.setVisibility(View.GONE);
//                cardView.setVisibility(View.VISIBLE);
            }
        });

        final EditText passwordEditText = rootView.findViewById(R.id.password_register);
        final EditText emailEditText = rootView.findViewById(R.id.email_register);

        final EditText passwordOldEditText = rootView.findViewById(R.id.password_old_register);
        final EditText phoneEditText = rootView.findViewById(R.id.phone_register);

        Button submitOldBtn = rootView.findViewById(R.id.submit_register_help);

        submitOldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String passwordOld = passwordOldEditText.getText().toString();
                final String phoneNumber = phoneEditText.getText().toString();

                if(TextUtils.isEmpty(phoneNumber)){
                    phoneEditText.setError("Phone Number is Required");
                    return;
                }

                if (phoneNumber.length()< PHONE_NUMBER_CHARACTERS ) {
                    passwordEditText.setError("Phone Number Must be at least " + PHONE_NUMBER_CHARACTERS + " Numbers ");
                    return;
                }

                if(TextUtils.isEmpty(passwordOld)){
                    passwordEditText.setError("Password is Required");
                    return;
                }

                if (passwordOld.length()< MIN_CHARACTERS_PASSWORD ) {
                    passwordEditText.setError("Password Must be at least " + MIN_CHARACTERS_PASSWORD + " Characters ");
                    return;
                }

                callback.onPhoneRegister(phoneNumber, passwordOld);

            }
        });

        Button submitBtn = rootView.findViewById(R.id.submit_register);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String password = passwordEditText.getText().toString();
                final String email = emailEditText.getText().toString();

                if(TextUtils.isEmpty(email)){
                     emailEditText.setError("User Email is Required");
                     return;
                }

                if(TextUtils.isEmpty(password)){
                    passwordEditText.setError("Password is Required");
                    return;
                }

                if (password.length() == MIN_CHARACTERS_PASSWORD ) {
                    passwordEditText.setError("Password Must be " + MIN_CHARACTERS_PASSWORD + " Characters ");
                    return;
                }

                callback.onRegister(email, password);

            }
        });

        return rootView;
      }
}


//class RegisterF<MaterialEditText> extends AppCompatActivity {
//
//    CoordinatorLayout coordinatorLayout;
//    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//    DatabaseReference reference;
//    //  FirebaseAuth.AuthStateListener authStateListener;
//    //MaterialEditText emailEt, usernameEt, passwordEt;
//    EditText emailEt, usernameEt, passwordEt;
//    Button register_btn;
//    public static int MIN_CHARACTERS_PASSWORD = 6;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.register_fragment);
//
//        //pointers----------------------------
//        register_btn=findViewById(R.id.submit_register);
//        usernameEt=findViewById(R.id.userNameInput);
//        emailEt=findViewById(R.id.email_register);
//        passwordEt=findViewById(R.id.passwordInput);
//        coordinatorLayout=findViewById(R.id.coordinator);
//        //------------------------------------
//
//        register_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//             /*  String email = emailEt.getText().toString();
//               //  fullName = fullnameEt.getText().toString();
//               String password = passwordEt.getText().toString();
//
//               //sign up the user
//               firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                   @Override
//                   public void onComplete(@NonNull Task<AuthResult> task) {
//
//                       if (task.isSuccessful())
//                           Snackbar.make(coordinatorLayout, "Sign up successful", Snackbar.LENGTH_SHORT).show();
//                       else
//                           Snackbar.make(coordinatorLayout, "Sign up failed", Snackbar.LENGTH_SHORT).show();
//
//                   }
//               });*/
//                String txt_username = usernameEt.getText().toString();
//                String txt_email = emailEt.getText().toString();
//                String txt_password = passwordEt.getText().toString();
//
//                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
//                    Toast.makeText(RegisterActivity.this, "all_fileds_are_required", Toast.LENGTH_SHORT).show();
//                } else if (txt_password.length() < 6 ){
//
//                    Toast.makeText(RegisterActivity.this, "Password Must be at least " + MIN_CHARACTERS_PASSWORD + " Characters",Toast.LENGTH_SHORT).show();
//                } else {
//                    register(txt_username, txt_email, txt_password);
//                }
//
//            }
//        });
//
//    }
//
//    private void register(final String username, String email, String password) {
//
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//                            assert firebaseUser != null;
//                            String userid = firebaseUser.getUid();
//
//                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
//
//                            HashMap<String, String> hashMap = new HashMap<>();
//                            hashMap.put("id", userid);
//                            hashMap.put("username", username);
//                            hashMap.put("imageURL", "default");
//                            hashMap.put("status", "offline");
//                            hashMap.put("search", username.toLowerCase());
//
//                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                }
//                            });
//                        } else {
//                            Toast.makeText(RegisterActivity.this, "register with this email or password)", Toast.LENGTH_SHORT).show();
//                    }
//                    }
//                });
//
//
//    }
//
//}

