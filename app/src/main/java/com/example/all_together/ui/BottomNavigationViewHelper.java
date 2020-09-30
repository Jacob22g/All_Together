package com.example.all_together.ui;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.all_together.R;

import com.example.all_together.ui.add.AddFragment;
import com.example.all_together.ui.chat.ChatsFragment;
import com.example.all_together.ui.dashboard.DashboardFragment;
import com.example.all_together.ui.home.HomeFragment;
import com.example.all_together.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    public static void setBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){

        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
        bottomNavigationViewEx.setIconSize(30,30);
    }

    public static void enableNavigation(final Context context, final BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home: // ACTIVITY_NUM = 0;
                        Intent intentHome = new Intent(context, HomeFragment.class);
                        context.startActivity(intentHome);
                        break;

                    case R.id.navigation_dashboard: // ACTIVITY_NUM = 1;
                        Intent intentDashboard = new Intent(context, DashboardFragment.class);
                        context.startActivity(intentDashboard);
                        break;

                    case R.id.navigation_add: // ACTIVITY_NUM = 2;
                        Intent intentAdd = new Intent(context, AddFragment.class);
                        context.startActivity(intentAdd);
                        break;

                    case R.id.navigation_profile:// ACTIVITY_NUM = 3;
                        Intent intentPerson = new Intent(context, ProfileFragment.class);
                        context.startActivity(intentPerson);
                        break;

                    case R.id.navigation_chat:// ACTIVITY_NUM = 4;
                        Intent intentEmail = new Intent(context, ChatsFragment.class);
                        context.startActivity(intentEmail);
                        break;
                }
                return false;
            }
        });
    }
}
