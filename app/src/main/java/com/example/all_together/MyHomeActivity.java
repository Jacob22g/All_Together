package com.example.all_together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MyHomeActivity extends AppCompatActivity {

    private TextView userTv;

    CoordinatorLayout coordinatorLayout;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_home);

        coordinatorLayout = findViewById(R.id.coordinatorLayout_myhome);
        navigationView = findViewById(R.id.navigation_view_myhome);
        drawerLayout = findViewById(R.id.drawerLayout_myhome);

        Toolbar toolbar = findViewById(R.id.toolbar_myhome);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.icons_menu_w);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.sign_up:
                        Toast.makeText(MyHomeActivity.this, "Sign Up", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sign_in:
                        Toast.makeText(MyHomeActivity.this, "Sign In", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sign_out:
                        Toast.makeText(MyHomeActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                        //firebaseAuth.signOut();
                        break;
                }
                return false;
            }
        });


        //Intent intent = getIntent();
        //String userName = intent.getStringExtra("userName");
        //userTv.setText(userName);
    }


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