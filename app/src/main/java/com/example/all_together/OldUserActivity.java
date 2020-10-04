package com.example.all_together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.all_together.ui.BottomNavigationViewHelper;
import com.example.all_together.ui.add.AddFragment;
import com.example.all_together.ui.chat.ChatsFragment;
import com.example.all_together.ui.home.HomeFragment;
import com.example.all_together.ui.old_user.*;
import com.example.all_together.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class OldUserActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_user_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        coordinatorLayout = findViewById(R.id.container_activityolduser);
        navigationView = findViewById(R.id.navigation_view_activityolduser);
        drawerLayout = findViewById(R.id.drawerLayout_activityolduser);

        View headerView = navigationView.getHeaderView(0);
        TextView userTv = headerView.findViewById(R.id.navigation_header_container);
        userTv.setText(firebaseUser.getEmail());
        if (firebaseUser != null) { //signed in
            navigationView.getMenu().findItem(R.id.sign_in).setVisible(false);
            navigationView.getMenu().findItem(R.id.sign_up).setVisible(false);
            navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);
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
                        Toast.makeText(OldUserActivity.this, "Sign Up", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sign_in:
                        Toast.makeText(OldUserActivity.this, "Sign In", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sign_out:
                        Toast.makeText(OldUserActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                        logout(new View(OldUserActivity.this));
                        break;
                }
                return false;
            }
        });

//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//                View headerView = navigationView.getHeaderView(0);
//                TextView userTv = headerView.findViewById(R.id.navigation_header_container);
//
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                if (user != null) { //sign in or sign up
//
//                    userTv.setText(user.getEmail());
//
//                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(false);
//                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(false);
//                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);
//                } else { // sign out
//                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(true);
//                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(true);
//                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(false);
//                }
//            }
//        };

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.nav_view_old);

        BottomNavigationViewHelper.setBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(listener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }

    private BottomNavigationViewEx.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.navigation_home_old:
//                            selectedFragment = new HomeFragment();
                            selectedFragment = new OldHomeFragment();
                            break;
                        case R.id.navigation_add_old:
                            selectedFragment = new AddFragment();
                            break;
                        case R.id.navigation_profile_old:
//                            selectedFragment = new ProfileFragment();
                            selectedFragment = new OldProfileFragment();
                            break;
                        case R.id.navigation_chat_old:
                            selectedFragment = new ChatsFragment();
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    return true;
                }
    };

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}