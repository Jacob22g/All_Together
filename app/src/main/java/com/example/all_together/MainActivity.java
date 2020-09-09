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
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RegisterFragment.OnRegisterFragmentListener {

    final String FRAGMENT_REGISTER_TAG = "fragment_register";
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private CardView cardView;
    private ArrayList<User> Users = new ArrayList<>();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener listener;

    EditText userEt;
    EditText passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardView = findViewById(R.id.cardView);
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

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

                userEt = findViewById(R.id.userNameInput);
                passwordEt = findViewById(R.id.passwordInput);

                final String userName = userEt.getText().toString();
                final String password = passwordEt.getText().toString();

                auth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Add why a user was unable to log in

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Sign In in Successfull" + ", User: " + userName + ", " + "Password: " + password , Toast.LENGTH_SHORT).show();
                            final CollapsingToolbarLayout collapsing = findViewById(R.id.collapsingLayout);
                            collapsing.setTitle(userName);
                        }
                        else
                            Toast.makeText(MainActivity.this, "Sign In Failed ", Toast.LENGTH_SHORT).show();


                    }
                });
            }
        });

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){ //sign in or sign up

                }

                else { // sign out

                    //toolbar.getMenu().findItem(R.id.item2).setVisible(true);
                }
            }
        };

    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                Toast.makeText(this, "Help is selected", Toast.LENGTH_SHORT).show();
                return true;
            case  R.id.item2:
                auth.signOut();
                Toast.makeText(this, "LogOut is selected", Toast.LENGTH_SHORT).show();
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
    public void onRegister(final String userName, final String password, final String email) {

        auth.createUserWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //Add why a user was unable to log in

                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Sign Up in Successfull" + ", User: " + userName + ", " + "Password: " + password + ", " , Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "Sign Up Failed ", Toast.LENGTH_SHORT).show();

        }
        });
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_REGISTER_TAG);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
}