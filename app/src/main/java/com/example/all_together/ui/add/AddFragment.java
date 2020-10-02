package com.example.all_together.ui.add;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.all_together.R;
import com.example.all_together.VolunteeringFragment;
import com.example.all_together.model.Volunteering;
import com.example.all_together.ui.dashboard.DashboardFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class AddFragment extends Fragment implements LocationListener {

    final String TAG = "tag";

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference volunteersDB = database.getReference("volunteerList");
    DatabaseReference usersDB = database.getReference("users");
    DatabaseReference volunteers_id = database.getReference("volunteerIdNum");

    long newVolunteerId;

    List<Volunteering> volunteerList  = new ArrayList<>();

    final int LOCATION_PERMISSION_REQUEST = 191;
    LocationManager manager;
    Geocoder geocoder;
    Handler handler;

    EditText streetEt;
    EditText cityEt;

    String spinnerText;

    String userName;

    EditText descriptionEt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add,container,false);

        // Description:

        descriptionEt = view.findViewById(R.id.new_volunteer_description);

        // Categories:

        Spinner spinner = view.findViewById(R.id.new_volunteer_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.volunteering_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                String[] vol = getResources().getStringArray(R.array.volunteering_categories_array);

                ((TextView)parent.getChildAt(0)).setTextSize(20);

                spinnerText = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), spinnerText+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(),"Nothing selected",Toast.LENGTH_SHORT).show();
            }
        });

        // Date and Time:

        final TextView timeTv = view.findViewById(R.id.new_volunteer_time_tv);
        final TextView dateTv = view.findViewById(R.id.new_volunteer_date_tv);

        Button dateBtn = view.findViewById(R.id.new_volunteer_date_btn);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        dateTv.setText(dayOfMonth + " / " + (month+1) + " / " + year);
                    }
                },year,month,day);
                dpd.show();
            }
        });

        Button timeBtn = view.findViewById(R.id.new_volunteer_time_btn);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        timeTv.setText(hourOfDay + " : " + minute);
                    }
                }, hour, minutes, true);
                tpd.show();
            }
        });

        // Location:

        geocoder = new Geocoder(getContext());
        streetEt = view.findViewById(R.id.new_volunteer_location_street_et);
        cityEt = view.findViewById(R.id.new_volunteer_location_city_et);
        handler = new Handler();


//        if(Build.VERSION.SDK_INT >= 23){
//
//            int hasLocationPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
//            }
//        }

        manager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);

        List<String> providers = manager.getProviders(false);
        for (String provider:providers){
            Log.d(provider, "enabled:" + manager.isProviderEnabled(provider));
        }

        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String bestProvider = manager.getBestProvider(criteria, false);
        if (bestProvider!=null)
            Log.d("best provider",bestProvider);

        Button getLocation = view.findViewById(R.id.new_volunteer_get_location_btn);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    // GPS is off
                    buildAlertMessageNoGps();
                else {

                    if (Build.VERSION.SDK_INT >= 23) {
                        int has1locationPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                        if (has1locationPermission != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                        } else {
                            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, AddFragment.this);
                        }
                    } else {
                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, AddFragment.this);
                    }

                }
            }
        });

        // load the list to add an item then save the hole list
        volunteersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                volunteerList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Volunteering volunteering = ds.getValue(Volunteering.class);
                        volunteerList.add(volunteering);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //----------------
        volunteers_id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    newVolunteerId = snapshot.getValue(long.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read ID value.", error.toException());
            }
        });

        // get user name
        usersDB.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        switch (ds.getKey()){
                            case "user_name":
                                userName = ds.getValue(String.class);
                                break;
//                            case "city":
//                                cityEt = ds.getValue(String.class);
//                                break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button submitBtn = view.findViewById(R.id.submit_new_volunteering);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 0. check if all required values are submitted
                if (TextUtils.isEmpty(dateTv.getText().toString())) {
                    dateTv.setError("Date is Required");
                    return;
                }
                if (TextUtils.isEmpty(timeTv.getText().toString())) {
                    timeTv.setError("Hour is Required");
                    return;
                }
                if (TextUtils.isEmpty(cityEt.getText().toString())) {
                    cityEt.setError("City is Required");
                    return;
                }
                if (TextUtils.isEmpty(streetEt.getText().toString())) {
                    streetEt.setError("Street is Required");
                    return;
                }

                // 1. get all the parameters into a volunteering object
                    // Create ID for the Volunteering

//                volunteers_id.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            newVolunteerId = snapshot.getValue(long.class);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getContext(), "No ID Found" + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });

                    // increase id by +1 for next volunteering
                volunteers_id.setValue(newVolunteerId+1);

                Volunteering volunteering = new Volunteering(newVolunteerId);

                volunteering.setDate(dateTv.getText().toString());
                volunteering.setHour(timeTv.getText().toString());
                volunteering.setType(spinnerText);
                volunteering.setName(userName);
                volunteering.setDescription(descriptionEt.getText().toString());
                volunteering.setOldUID(firebaseUser.getUid());

                volunteering.setLocationCity(cityEt.getText().toString());
                volunteering.setLocationStreet(streetEt.getText().toString());

                // 2. add it to the volunteerList
                volunteerList.add(volunteering);

                // 3. save it in the firebase
                volunteersDB.setValue(volunteerList);

                // 4. if ok go to the volunteering page
//                // going to dashboard on back pressed inside the volunteerFragment
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DashboardFragment()).commit();

                Fragment fragment = new VolunteeringFragment(volunteering);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.replace(R.id.drawerLayout_activitymainapp, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        return view;
    }

    // Check if GPS is ON
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Attention")
                        .setMessage("The application must have location permission in order to get location!")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:"+getActivity().getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        final double lat = location.getLatitude() , lng = location.getLongitude();

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                    final Address bestAddress = addresses.get(0);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            cityEt.setText(bestAddress.getLocality()+"");
                            streetEt.setText(bestAddress.getThoroughfare()+"");
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override
    public void onProviderEnabled(String provider) { }
    @Override
    public void onProviderDisabled(String provider) { }

}
