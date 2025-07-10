package com.example.sih;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HrActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_CODE = 1234;
    private static final String GEOFENCE_ID = "MY_GEOFENCE";
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    private GoogleMap mMap;
    private static final LatLng GEOFENCE_CENTER = new LatLng(28.82423078083373, 77.15252689999998);
    private static final float GEOFENCE_RADIUS = 100;

    private TextView statusTextView;
    private TextView checkInTimeTextView;
    private TextView checkOutTimeTextView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr);

        // Initialize the drawer layout and navigation view
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Menu");

        // Enable the hamburger icon
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Set up the ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up the navigation view item selected listener
        navigationView.setNavigationItemSelectedListener(this::handleNavigationItemSelected);

        statusTextView = findViewById(R.id.statusTextView);
        checkInTimeTextView = findViewById(R.id.checkInTimeTextView);
        checkOutTimeTextView = findViewById(R.id.checkOutTimeTextView);

        geofencingClient = LocationServices.getGeofencingClient(this);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (checkPermissions()) {
            setupGeofence();
        } else {
            requestPermissions();
        }
        updateUI();

        // Ensure the ActionBar is available
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); // Make sure ic_menu is available
        }
    }

    private boolean handleNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.nav_home) {
            // Handle the home action
            Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
        }
//        else if (id == R.id.nav_profile) {
//            // Handle the profile action
//            Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show();
//        }
//        else if (id == R.id.nav_settings) {
////            // Handle the settings action
////            Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
////        }
        else if (id == R.id.nav_logout) {
            // Handle the logout action

        } else if (id == R.id.nav_daily_logs) {
            // Handle the daily logs action
            intent = new Intent(this, DailyLogsActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
//Toast.makeText(this, "Logout selected", Toast.LENGTH_SHORT).show();
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Use the GEOFENCE_CENTER constant
        mMap.addMarker(new MarkerOptions().position(GEOFENCE_CENTER).title("Geofence Center"));

        // Draw a circle to represent the geofence
        mMap.addCircle(new CircleOptions()
                .center(GEOFENCE_CENTER)
                .radius(GEOFENCE_RADIUS)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(70, 150, 50, 50)));

        // Move camera to the geofence location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GEOFENCE_CENTER, 15));

        // Add device location marker if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                },
                PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupGeofence();
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupGeofence() {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(GEOFENCE_CENTER.latitude, GEOFENCE_CENTER.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        geofencePendingIntent = getGeofencePendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
                .addOnSuccessListener(this, aVoid -> Toast.makeText(this, "Geofence added", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(this, e -> Toast.makeText(this, "Failed to add geofence", Toast.LENGTH_SHORT).show());
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        return geofencePendingIntent;
    }
    private BroadcastReceiver checkInOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerCheckInOutReceiver();
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(checkInOutReceiver);
    }

    private void registerCheckInOutReceiver() {
        checkInOutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUI();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.geofencecheckinout.CHECK_IN");
        filter.addAction("com.example.geofencecheckinout.CHECK_OUT");
        registerReceiver(checkInOutReceiver, filter);
    }

    private void updateUI() {
        SharedPreferences prefs = getSharedPreferences("GeofencePrefs", MODE_PRIVATE);
        String checkInTime = prefs.getString("checkInTime", "N/A");
        String checkOutTime = prefs.getString("checkOutTime", "N/A");

        checkInTimeTextView.setText("Check-in Time: " + checkInTime);
        checkOutTimeTextView.setText("Check-out Time: " + checkOutTime);

        if (checkInTime.equals("N/A") && checkOutTime.equals("N/A")) {
            statusTextView.setText("Status: Outside geofence");
        } else if (!checkInTime.equals("N/A") && checkOutTime.equals("N/A")) {
            statusTextView.setText("Status: Inside geofence");
        } else {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date checkInDate = format.parse(checkInTime);
                Date checkOutDate = format.parse(checkOutTime);
                long diff = checkOutDate.getTime() - checkInDate.getTime();
                long hours = diff / (60 * 60 * 1000);
                long minutes = (diff % (60 * 60 * 1000)) / (60 * 1000);

                String totalWorkingHour = String.format("%d hours %d minutes", hours, minutes);
                String status = String.format("Status: Last check-out at %s (Total working time: %s)", checkOutTime, totalWorkingHour);
                statusTextView.setText(status);

                // Log the calculated times for debugging
                Log.d("TimeCalculation", "Check-in: " + checkInTime + ", Check-out: " + checkOutTime + ", Total: " + totalWorkingHour);
            } catch (ParseException e) {
                e.printStackTrace();
                statusTextView.setText("Status: Error calculating working time");
                Log.e("TimeCalculation", "Error parsing dates", e);
            }
        }
    }
}