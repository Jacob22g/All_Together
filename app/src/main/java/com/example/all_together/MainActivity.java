package com.example.all_together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RegisterFragment.OnRegisterFragmentListener {

    final String FRAGMENT_REGISTER_TAG = "fragment_register";
    private Toolbar toolbar;
    private CardView cardView;

    private ArrayList<User> Users = new ArrayList<>();

    private DatabaseReference mDatabase;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private FirebaseAuth.AuthStateListener listener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);

        cardView = findViewById(R.id.cardView);
        toolbar = findViewById(R.id.myToolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        collapsingToolbarLayout = findViewById(R.id.collapsingLayout);

        final EditText passwordEt = findViewById(R.id.passwordInput);
        final EditText emailEt = findViewById(R.id.emailInput);

        TextView register_btn = (TextView) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardView.setVisibility(View.GONE);

                FragmentManager registerFragment = getSupportFragmentManager();
                FragmentTransaction transaction = registerFragment.beginTransaction();
                transaction.add(R.id.root_layout,new RegisterFragment(), FRAGMENT_REGISTER_TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button login = findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = passwordEt.getText().toString();
                String email = emailEt.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Add why a user was unable to log in
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Sign In in Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),MyHomeActivity.class);
                            //intent.putExtra(userName,"userName");
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(MainActivity.this, "Sign In Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                    }
                });


                listener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) { //sign in or sign up

                        } else { // sign out

                        }
                    }
                };

            }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                Toast.makeText(this, "Help is selected", Toast.LENGTH_SHORT).show();
                return true;
            case  R.id.item2:
                mAuth.signOut();
                cardView.setVisibility(View.VISIBLE);
                Toast.makeText(this, "LogOut is selected", Toast.LENGTH_SHORT).show();
                return true;
        }

        //drawerLayout.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onRegister(final String userName, String password, String email) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Sign Up in Successful" , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MyHomeActivity.class);
                    //intent.putExtra(userName,"userName");
                    startActivity(intent);
//                    String user_id = mAuth.getCurrentUser().getUid();
//                    DatabaseReference CurrentUser_db =  mDatabase.child(user_id);
//                    CurrentUser_db.child("Email").setValue(email);
//                    CurrentUser_db.child("Password").setValue(password);
                }

                //Add why a user was unable to log in
                else
                    Toast.makeText(MainActivity.this, "Sign Up Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_REGISTER_TAG);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }




}
