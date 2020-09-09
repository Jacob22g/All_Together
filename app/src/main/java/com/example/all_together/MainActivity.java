package com.example.all_together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final String FRAGMENT_REGISTER_TAG = "fragment_register";
    DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        TextView register_btn = (TextView) findViewById(R.id.text);

        Button login = findViewById(R.id.login_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager registerFragment = getSupportFragmentManager();
                FragmentTransaction transaction = registerFragment.beginTransaction();
                transaction.add(R.id.root_layout,new RegisterFragment(), FRAGMENT_REGISTER_TAG);
                transaction.commit();

            }
        });


//        register_btn.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//
////                FragmentManager registerFragment = getSupportFragmentManager();
////                FragmentTransaction transaction = registerFragment.beginTransaction();
////                transaction.add(R.id.root_layout,new RegisterFragment(), FRAGMENT_REGISTER_TAG);
////                transaction.commit();
//            }
//        });

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.home_icon2);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                Toast.makeText(this, "Help is selected", Toast.LENGTH_SHORT).show();
                return true;
            case  R.id.item2:
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
}