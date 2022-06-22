package com.example.toparkme.ToParkMe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.toparkme.R;
import com.example.toparkme.ToParkMe.classes.AppManager;
import com.example.toparkme.ToParkMe.classes.ParkingLocation;
import com.example.toparkme.ToParkMe.classes.SoundMaker;
import com.example.toparkme.ToParkMe.fragment.MapsFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Activity_Home extends AppCompatActivity implements OnMapReadyCallback {

    private static final String BACKGROUND_URL = "https://pixabay.com/get/gdfef60270a5665c070066c788bc292cafa2d67e92c00caccc92058adbc2e5aa60bfae945a1dbf1b8b5d41a8bc6fec55dad3fc0f20e249cec8df4154f8989ff4f00692f34c40754ffe3c565f45b6c2fb6_1920.jpg";

    private ImageView profile_iv;
    private ImageView location_iv;
    private ImageView search_iv;
    private MaterialButton question_btn;
    private MaterialButton favorite_btn;
    private ImageView waze_iv;
    private EditText search_et;
    private ImageView main_IMG_glide;
    private Intent intent;

    //// Map
    private boolean isPermissionGranter;
    private MapsFragment fragment_map;
    private GoogleMap googleMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    //// Map

    //// Sound
    private SoundMaker sound;
    //// Sound

    //// Firebase
    private AppManager manager;
    private FirebaseUser fUser;
    private DatabaseReference reference;
    private String userID;
    //// Firebase


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //sound
        sound = new SoundMaker();
        sound.setMpAndPlay(this, R.raw.ic_open_app_sound);

        loadFragmentMap();
        setViews();
        checkPermission();


        if (isPermissionGranter) {
            if (checkGooglePlayServices()) {
                SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.park_map_fl, supportMapFragment).commit();
                supportMapFragment.getMapAsync(this);
                setLocation();

            } else {
                Toast.makeText(this, "Google PlayServices is Not available", Toast.LENGTH_LONG).show();
            }
        }

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = fUser.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                manager = snapshot.getValue(AppManager.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Home.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });

        question_btn.setOnClickListener(questionHomeBtn);
        favorite_btn.setOnClickListener(favoriteHomeBtn);
        search_iv.setOnClickListener(searchHomeBtn);
        waze_iv.setOnClickListener(wazeHomeIv);
        location_iv.setOnClickListener(locationHomeIv);
        profile_iv.setOnClickListener(profileHomeIv);

    }


    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 201, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(Activity_Home.this, "User Canceled Dialog ", Toast.LENGTH_LONG).show();
                }
            });
            dialog.show();
        }
        return false;
    }

    /// location

    private void setLocation() {
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = location -> findUserLocation();

    }

    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission")
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void findUserLocation() {
        double lon = 0;
        double lat = 0;

        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGPSEnabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }
        Location location = getLastKnownLocation();

        if (location != null) {
            lon = location.getLongitude();
            lat = location.getLatitude();
        }
        String addressName = "LOCATION";

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addList = geocoder.getFromLocation(lat, lon, 1);
            if (addList.size() == 0){
                lon = 34.7818;
                lat = 32.0853;
                addressName = "Tel Aviv";
            }
            else{
                Address add = addList.get(0);
                addressName = add.getAddressLine(0);
            }
        }catch (IOException e){
            addressName = "LOCATION";
        }


        manager.getPerson().setParkingLocation(new ParkingLocation(lon,lat,addressName));
        saveData();

        LatLng latLng = new LatLng(lat,lon);
        addMarkToMap(latLng,addressName);

    }

    public void saveData(){
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(manager);
    }

    public void addMarkToMap(LatLng latNlng , String address){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(address);
        markerOptions.position(latNlng);
        googleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latNlng,17);
        googleMap.animateCamera(cameraUpdate);

        // toast
        Toast.makeText(Activity_Home.this, "כתובת:  " + address , Toast.LENGTH_LONG).show();
    }

    /// location


    private void checkPermission() {
        int fineLocationStatus = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationStatus = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if ((fineLocationStatus != PackageManager.PERMISSION_GRANTED) &&
                (coarseLocationStatus != PackageManager.PERMISSION_GRANTED)) {
            isPermissionGranter = true;
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        } else {
            isPermissionGranter = true;
        }

    }


    private View.OnClickListener questionHomeBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replaceActivity(1);
        }
    };


    private View.OnClickListener favoriteHomeBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replaceActivity(0);
        }
    };

    private View.OnClickListener searchHomeBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //hide the keyboard
            hideKeyboard(view);

            // move the map
            String location = search_et.getText().toString();
            if (location.equals("")) {
                Toast.makeText(Activity_Home.this, "Type Any Location ", Toast.LENGTH_LONG).show();
            } else {
                Geocoder geocoder = new Geocoder(Activity_Home.this, Locale.getDefault());
                try {
                    List<Address> addList = geocoder.getFromLocationName(location, 1);
                    if (addList.size() > 0) {
                        LatLng latLng = new LatLng(addList.get(0).getLatitude(), addList.get(0).getLongitude());

                        addMarkToMap(latLng,location);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // remove the text from the edit text
            search_et.setText("");
        }
    };

    private View.OnClickListener wazeHomeIv = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            PackageManager managerclock = getPackageManager();
            i = managerclock.getLaunchIntentForPackage("com.waze");
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);

            //to get the last location before opening Waze
            //the 5 second wait is for the GPS to get the location
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    findUserLocation();
                }
            }, 5000);
        }
    };

    private View.OnClickListener locationHomeIv = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //the 5 second wait is for the GPS to get the location
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    findUserLocation();
                }
            }, 5000);
        }
    };

    private View.OnClickListener profileHomeIv = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replaceActivity(2);
        }
    };


    private void replaceActivity(int i) {
        if (i == 1){
            intent = new Intent(this, Activity_Chat.class);
        }else if(i == 0) {
            intent = new Intent(this, Activity_Free.class);
        }else {
            intent = new Intent(this, Activity_Profile.class);
        }
        startActivity(intent);
        finish();
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }


    private void loadFragmentMap() {
        fragment_map = new MapsFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.park_map_fl, fragment_map).commit();

    }


    private void findViews() {
        profile_iv =  findViewById(R.id.park_profile_iv);
        location_iv = findViewById(R.id.park_setting_iv);
        search_iv = findViewById(R.id.park_search_home_iv);
        question_btn = findViewById(R.id.park_question_btn);
        favorite_btn = findViewById(R.id.park_favorite_btn);
        search_et = findViewById(R.id.park_search_home_et);
        waze_iv = findViewById(R.id.park_waze_iv);

    }
    private void setViews() {
        setGlide();
        findViews();

    }

    private void setGlide(){
        main_IMG_glide = findViewById(R.id.park_IMG_background_home);
        Glide
                .with(this)
                .load(BACKGROUND_URL)
                .centerCrop()
                .into(main_IMG_glide);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng latLng = new LatLng(32.0853,34.7818);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,14);
        googleMap.animateCamera(cameraUpdate);
    }

}