package edu.cuhk.csci3310.travelspot;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

import edu.cuhk.csci3310.travelspot.databinding.ActivityMainBinding;
import edu.cuhk.csci3310.travelspot.manager.DatabaseManager;
import edu.cuhk.csci3310.travelspot.manager.GeofenceManager;
import edu.cuhk.csci3310.travelspot.model.Spot;
import edu.cuhk.csci3310.travelspot.service.ForegroundLocationService;
import edu.cuhk.csci3310.travelspot.ui.tutorial.CreateTutorial;
import edu.cuhk.csci3310.travelspot.ults.CommonUlts;

public class MainActivity extends AppCompatActivity {
    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 101;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 102;
    private ActivityMainBinding binding;
    private DatabaseManager databaseManager;
    private GeofenceManager geofenceManager;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String TUTORIAL_SHOWN_KEY = "tutorialShown";

    private boolean firstTimeCreate = false;


    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the flag from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        firstTimeCreate = !prefs.getBoolean(TUTORIAL_SHOWN_KEY, false);

        databaseManager = new DatabaseManager(this);
        geofenceManager = new GeofenceManager(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        checkAndRequestPermissions();
        startLocationService();
        refreshGeofence();

        if (firstTimeCreate) {
            showTutorial();

            // Set the flag to true to indicate the tutorial has been shown
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(TUTORIAL_SHOWN_KEY, true);
            editor.apply();
        }

    }

    public void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
        }

        if (Build.VERSION.SDK_INT >= 29) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                        String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{android.Manifest.permission.POST_NOTIFICATIONS}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
        }
    }

    private void startLocationService() {
        if (!foregroundLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, ForegroundLocationService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    /// check if the tracking location foreground service running
    public boolean foregroundLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(ForegroundLocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void refreshGeofence() {
        List<Spot> savedSpots = databaseManager.getAllSpots();
        for(Spot spot:savedSpots) {
            if (spot.getFinish() == 0) {
                geofenceManager.addGeofence(String.valueOf(spot.getId()), spot.getLatitude(), spot.getLongitude(), CommonUlts.GEOFENCE_RADIUS);
            }
        }
    }

    private void showTutorial() {
        if (builder == null) {
            builder = new AlertDialog.Builder(this);
        }
        new CreateTutorial(builder, R.string.Tutorial, R.string.Tutorial_detail).onClick(null);
    }
}