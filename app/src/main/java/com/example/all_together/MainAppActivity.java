package com.example.all_together;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.example.all_together.ui.BottomNavigationViewHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainAppActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        setBottomNavigationViewEx();

    }

    private void setBottomNavigationViewEx(){
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.nav_view);
        BottomNavigationViewHelper.setBottomNavigationView(bottomNavigationViewEx);

    }




//        //BottomNavigationView navView = findViewById(R.id.nav_view);
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.nav_view);
//
//        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_search, R.id.navigation_profile)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//
//        // To change to action bar menu title, But we are in Theme No action bar
////        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//
//        NavigationUI.setupWithNavController(bottomNavigationViewEx, navController);

}