package com.example.all_together;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.all_together.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.SignInButtonImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RegisterFragment.OnRegisterFragmentListener, AfterRegisterFragment.OnAfterRegisterFragmentListener, AfterOldRegisterFragment.OnAfterOldRegisterFragmentListener, PhoneLogin.OnRegisterFragmentListener {

    final String TAG = "tag" ;
    final String FRAGMENT_REGISTER_TAG = "fragment_register";
    final String FRAGMENT_AFTER_REGISTER_TAG = "fragment_after_register";
    final String FRAGMENT_PHONE_TAG = "fragment_phone_sign_in";
    final String FRAGMENT_PHONE_LOGIN_TAG = "fragment_phone_login";
    final String FRAGMENT_AFTER_OLD_REGISTER_TAG = "fragment_after_old_register";
    final String FRAGMENT_VERIFY_TAG = "fragment_verify";
    final String FRAGMENT_SIGN_IN_TAG = "sign_in_register";
    final int RC_SIGN_IN = 1;

    boolean flag = false;

    private Toolbar toolbar;
    private CardView cardView;
    private ArrayList<User> Users = new ArrayList<>();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    //private SignInButton googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButtonImpl googleSignInButton;
    private FirebaseAuth.AuthStateListener listener;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    boolean isOldUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if user logged in
        if (firebaseUser != null){

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Check if old user
            usersDB.child(firebaseUser.getUid()).child("is_old_user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //isOldUser = snapshot.getValue(boolean.class);
                    Intent intent;
                    if(isOldUser){
                        intent  = new Intent(MainActivity.this, OldUserActivity.class);
                    } else
                        intent  = new Intent(MainActivity.this, MainAppActivity.class);
                    startActivity(intent);
                    finish();
                    progressDialog.dismiss();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

//            Intent intent;
//            if(isOldUser){
//                intent  = new Intent(this, OldUserActivity.class);
//            } else
//                intent  = new Intent(this, MainAppActivity.class);
//            startActivity(intent);
//            finish();
        }

        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();

        googleSignInButton = findViewById(R.id.googleSignIn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icons_menu_w);

        cardView = findViewById(R.id.cardView);
        collapsingToolbarLayout = findViewById(R.id.collapsingLayout);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);

        final EditText passwordEt = findViewById(R.id.passwordInput);
        final EditText emailEt = findViewById(R.id.emailInput);

        TextView register_btn = (TextView) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager registerFragment = getSupportFragmentManager();
                FragmentTransaction transaction = registerFragment.beginTransaction();
                transaction.add(R.id.drawerLayout,new RegisterFragment(), FRAGMENT_REGISTER_TAG);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        final Button phoneLogin = findViewById(R.id.phoneSignIn);
        phoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager phoneLogin = getSupportFragmentManager();
                FragmentTransaction transaction = phoneLogin.beginTransaction();
                transaction.add(R.id.drawerLayout,new PhoneLogin(), FRAGMENT_PHONE_TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        TextView forgetPassword = (TextView)findViewById(R.id.forgetPasswordBtn);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RestPasswordActivity.class));
            }
        });

        Button login = findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = passwordEt.getText().toString();
                String email = emailEt.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Add why a user was unable to log in
                            if (task.isSuccessful()) {

                                //Toast.makeText(MainActivity.this, "Sign In in Successful", Toast.LENGTH_SHORT).show();
                                navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);
                                navigationView.getMenu().findItem(R.id.sign_out_google).setVisible(false);

                                Intent intent = new Intent(getApplicationContext(), MainAppActivity.class);
                                startActivity(intent);

                                finish();
                            } else
                                Toast.makeText(MainActivity.this, "Sign In Failed, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });


        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) { //sign in or sign up
                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);
                    navigationView.getMenu().findItem(R.id.sign_out_google).setVisible(true);


                } else { // sign out

                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(true);
                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(true);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_out_google).setVisible(false);
                }
            }
        };


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

//                item.setChecked(true);

                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.sign_up:
                        //Toast.makeText(MainActivity.this, "Sign Up", Toast.LENGTH_SHORT).show();
                        FragmentManager registerFragment = getSupportFragmentManager();
                        FragmentTransaction transaction = registerFragment.beginTransaction();
                        transaction.add(R.id.drawerLayout,new RegisterFragment(), FRAGMENT_REGISTER_TAG);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case R.id.sign_in:
                        //Toast.makeText(MainActivity.this, "Sign In", Toast.LENGTH_SHORT).show();
                        // open main activity again
//                        finish();
//                        startActivity(getIntent());
//                        onBackPressed();
                        break;
                    case R.id.sign_out:
                        //Toast.makeText(MainActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                        FirebaseAuth firebaseAuth = null;
                        firebaseAuth.signOut();
                        break;
                    case R.id.sign_out_google:
                        //Toast.makeText(MainActivity.this, "Sign Out with Google", Toast.LENGTH_SHORT).show();
                        break;
                }

                return false;
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(listener);
//        updateUI(currentUser);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()== android.R.id.home){

            drawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPhoneLogin(String phoneNumber) {
        if(phoneNumber.equals(""))
            // Simulate back press
            getSupportFragmentManager().popBackStack();
        else {
            FragmentVerifyPhoneNumberLogin verifyPhoneNumberLogin = FragmentVerifyPhoneNumberLogin.newInstance(phoneNumber);
            getSupportFragmentManager().beginTransaction().add(R.id.drawerLayout, verifyPhoneNumberLogin,FRAGMENT_PHONE_LOGIN_TAG).commit();
        }
    }

    @Override
    public void onPhoneRegister(String phoneNumber) {

        if(phoneNumber.equals(""))
            // Simulate back press
            getSupportFragmentManager().popBackStack();
        else {
            FragmentVerifyPhoneNumber verifyPhoneNumber = FragmentVerifyPhoneNumber.newInstance(phoneNumber);
            getSupportFragmentManager().beginTransaction().add(R.id.drawerLayout, verifyPhoneNumber,FRAGMENT_VERIFY_TAG).commit();
        }
    }

    public void onRegister(String email, String password) {

        if(password.equals("") && email.equals(""))
            // Simulate back press
            getSupportFragmentManager().popBackStack();

        else {

            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        //Toast.makeText(MainActivity.this, "Sign Up is Successful", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

//                        flag = true;
                        // Open data fragment insertion for new users
                        FragmentManager registerFragment = getSupportFragmentManager();
                        FragmentTransaction transaction = registerFragment.beginTransaction();
                        transaction.add(R.id.drawerLayout,new AfterRegisterFragment(), FRAGMENT_AFTER_REGISTER_TAG);
                        transaction.addToBackStack(null);
                        transaction.commit();

//                        getSupportFragmentManager().beginTransaction().replace(R.id.drawerLayout,new AfterRegisterFragment()).commit();

//                        cardView.setVisibility(View.GONE);

//                        Intent intent = new Intent(getApplicationContext(), MainAppActivity.class);
//                        startActivity(intent);
                    }

                    //Add why a user was unable to log in
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Sign Up Failed, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_REGISTER_TAG);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//        }
    }

    @Override
    public void onAfterRegister() {

        // All the work is happening in the fragment
        //     save user profile data

        //Toast.makeText(MainActivity.this, "Saved user data", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), MainAppActivity.class);
        startActivity(intent);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_AFTER_REGISTER_TAG);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        finish();
    }

    @Override
    public void onAfterOldRegister() {

        //Toast.makeText(MainActivity.this, "Saved user data", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), OldUserActivity.class);
        startActivity(intent);

//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_AFTER_OLD_REGISTER_TAG);
//        getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        finish();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    public void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount>completedTask){
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            //Toast.makeText(MainActivity.this, "Signed In With Google Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Sign In With Google Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }

    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    usersDB.child(user.getUid()).child("is_old_user").setValue(false);
                    Intent intent = new Intent(getApplicationContext(), MainAppActivity.class);
                    intent.putExtra("isGoogle", true);
                    startActivity(intent);
                    //updateUI(user);
                }
                else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });
    }
}