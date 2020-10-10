package com.example.all_together;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.all_together.ui.BottomNavigationViewHelper;
import com.example.all_together.ui.add.AddFragment;
import com.example.all_together.ui.chat.ChatsFragment;
import com.example.all_together.ui.dashboard.DashboardFragment;
import com.example.all_together.ui.home.HomeFragment;
import com.example.all_together.ui.profile.ProfileFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainAppActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    GoogleSignInClient mGoogleSignInClient;

    //user details
    TextView fullName,email,age;
    ImageButton photo;
    Button signOut;

    String userName;
    TextView userTv;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersDB = database.getReference("users");
    GoogleSignInAccount account;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Boolean isGoogle = getIntent().getBooleanExtra("isGoogle",false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        fullName = findViewById(R.id.userFullNameTv);
        email = findViewById(R.id.userEmailTv);
        age = findViewById(R.id.userAgeTv);
        //photo = findViewById(R.id.userPhotoIv);
        photo = findViewById(R.id.change_profile_pic_btn);
        //signOut = findViewById(R.id.signOutBtn);

        usersDB.child(firebaseUser.getUid()).child("user_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.getValue(String.class);
                userTv.setText(userName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        coordinatorLayout = findViewById(R.id.container);
        navigationView = findViewById(R.id.navigation_view_activitymainapp);
        drawerLayout = findViewById(R.id.drawerLayout_activitymainapp);

        if (isGoogle){
            navigationView.getMenu().findItem(R.id.sign_out_google).setVisible(true);
            Toast.makeText(this, "navigationView Google", Toast.LENGTH_SHORT).show();
        }

        View headerView = navigationView.getHeaderView(0);
        userTv = headerView.findViewById(R.id.navigation_header_container);

        if (firebaseUser != null) { //signed in
            navigationView.getMenu().findItem(R.id.sign_in).setVisible(false);
            navigationView.getMenu().findItem(R.id.sign_up).setVisible(false);
            navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);
//            updateUI();

        } else { // signed out
            navigationView.getMenu().findItem(R.id.sign_in).setVisible(true);
            navigationView.getMenu().findItem(R.id.sign_up).setVisible(true);
            navigationView.getMenu().findItem(R.id.sign_out).setVisible(false);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.sign_up:
                        Toast.makeText(MainAppActivity.this, "Sign Up", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sign_in:
                        Toast.makeText(MainAppActivity.this, "Sign In", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sign_out:
                        Toast.makeText(MainAppActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                        logout(new View(MainAppActivity.this));
                        break;
                    case R.id.sign_out_google:
                        Toast.makeText(MainAppActivity.this, "Sign Out Google account", Toast.LENGTH_SHORT).show();
                        googleSignOut();
                        finish();
                        break;
                }
                return false;
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                View headerView = navigationView.getHeaderView(0);
                TextView userTv = headerView.findViewById(R.id.navigation_header_container);

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) { //sign in or sign up
                    userTv.setText(user.getEmail());
                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);
                } else { // sign out
                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(true);
                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(true);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(false);
                }
            }
        };


        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.nav_view);
        setBottomNavigationViewEx();
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(listener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }

    private BottomNavigationViewEx.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.navigation_dashboard:
                            selectedFragment = new DashboardFragment();
                            break;
                        case R.id.navigation_add:
                            selectedFragment = new AddFragment();
                            break;
                        case R.id.navigation_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.navigation_chat:
                            selectedFragment = new ChatsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    return true;
                }
            };

    private void setBottomNavigationViewEx() {
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.nav_view);
        BottomNavigationViewHelper.setBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }



    private void googleSignOut() {
        // Firebase sign out
        Toast.makeText(this, "Google", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Sign Out from your Google account", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
