package edu.cuhk.csci3310.travelspot.ui.spotdetail;

import static edu.cuhk.csci3310.travelspot.ults.CommonUlts.getMarkerIcon;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import edu.cuhk.csci3310.travelspot.MainActivity;
import edu.cuhk.csci3310.travelspot.R;
import edu.cuhk.csci3310.travelspot.manager.DatabaseManager;
import edu.cuhk.csci3310.travelspot.manager.GeofenceManager;
import edu.cuhk.csci3310.travelspot.model.Spot;
import edu.cuhk.csci3310.travelspot.ults.CommonUlts;

public class SpotDetailActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private static final String TAG = "SpotDetailActivity";
    private EditText editTitle;
    private EditText editNote;
    private Button buttonSave;
    private Button buttonCancel;
    private RadioGroup radioGroupColor;
    private RadioButton radioButtonRed;
    private RadioButton radioButtonBlue;
    private RadioButton radioButtonYellow;
    private GoogleMap mMap;
    private Integer spotID;
    private Double markerLat;
    private Double markerLng;
    private String markerLocation;
    private String Title;
    private String Color;
    private String Note;
    private int finish;
    private DatabaseManager databaseManager;
    private GeofenceManager geofenceManager;
    private Geocoder geocoder;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);

        databaseManager = new DatabaseManager(this);
        geofenceManager = new GeofenceManager(this);
        geocoder = new Geocoder(this);

        Intent intent = getIntent();

        markerLocation = intent.getStringExtra("markerLocation");
        markerLat = intent.getDoubleExtra("markerLat", 0);
        markerLng = intent.getDoubleExtra("markerLng", 0);
        spotID = intent.getIntExtra("spotID", -1);
        Log.d(TAG, "spotID: " + spotID);

        editTitle = findViewById(R.id.editTitle);
        editNote = findViewById(R.id.editNote);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        radioGroupColor = findViewById(R.id.radioGroupColor);
        radioButtonRed = findViewById(R.id.radioButtonRed);
        radioButtonBlue = findViewById(R.id.radioButtonBlue);
        radioButtonYellow = findViewById(R.id.radioButtonYellow);

        setDetail(spotID);

        radioGroupColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (marker != null) {
                    String color = getCheckedColor();
                    marker.setIcon(getMarkerIcon(color));
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);

        mapFragment.getMapAsync(this);
    }

    private String getCheckedColor() {
        String color;
        switch (radioGroupColor.getCheckedRadioButtonId()) {
            case R.id.radioButtonRed:
                color = "red";
                break;
            case R.id.radioButtonBlue:
                color = "blue";
                break;
            case R.id.radioButtonYellow:
                color = "yellow";
                break;
            default:
                color = "";
                break;
        }
        return color;
    }

    public void saveSpot(View view) {
        String title = String.valueOf(editTitle.getText());
        String note = String.valueOf(editNote.getText());
        String color = getCheckedColor();

        LatLng latLng = marker.getPosition();
        Double lat = latLng.latitude;
        Double lang = latLng.longitude;

        Log.d(TAG, "old lat lng" + markerLat + " " + markerLng);
        Log.d(TAG, "new lat lng" + lat + " " + lang);


        if (spotID > -1) {
            Spot spot = new Spot(spotID, title, lat, lang, note, markerLocation, color, finish);
            databaseManager.updateSpot(spot);
            geofenceManager.addGeofence(String.valueOf(spotID), lat, lang, CommonUlts.GEOFENCE_RADIUS);
        } else {
            Spot spot = new Spot(title, lat, lang, note, markerLocation, color, 0);
            int ID = (int) databaseManager.insertSpot(spot);
            if (ID > -1) {
                geofenceManager.addGeofence(String.valueOf(ID), lat, lang, CommonUlts.GEOFENCE_RADIUS);
            }
        }

        Intent intent = new Intent(SpotDetailActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void cancelSpot(View view) {
        editTitle.setText(Title);
        editNote.setText(Note);
        switch (Color){
            case "red":
                radioGroupColor.check(radioButtonRed.getId());
                break;
            case "blue":
                radioGroupColor.check(radioButtonBlue.getId());
                break;
            case "yellow":
                radioGroupColor.check(radioButtonYellow.getId());
                break;
            default:
                radioGroupColor.clearCheck();
                break;
        }
        LatLng latLng = new LatLng(markerLat, markerLng);
        marker.setPosition(latLng);
    }

    public void setDetail(int id) {
        if (id > -1) {
            Spot spot = databaseManager.getSpot(id);
            finish = spot.getFinish();
            Log.d(TAG, "finish :" + finish);
            Title = spot.getTitle();
            editTitle.setText(Title);
            Note = spot.getNote();
            editNote.setText(Note);
            Color = spot.getColor();
            switch (spot.getColor()) {
                case "red":
                    radioGroupColor.check(radioButtonRed.getId());
                    break;
                case "blue":
                    radioGroupColor.check(radioButtonBlue.getId());
                    break;
                case "yellow":
                    radioGroupColor.check(radioButtonYellow.getId());
                    break;
                default:
                    radioGroupColor.clearCheck();
                    break;
            }
        } else {
            Title = "Title";
            Note = "Please write your note here";
            editTitle.setText(Title);
            editNote.setText(Note);
            Color = "";
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng pos = new LatLng(markerLat, markerLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));
        marker = mMap.addMarker(new MarkerOptions().position(pos).title(markerLocation));
        String color = getCheckedColor();
        marker.setIcon(getMarkerIcon(color));
        marker.showInfoWindow();
        marker.setDraggable(true);
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("editName", String.valueOf(editTitle.getText()));
        savedInstanceState.putString("editNote", String.valueOf(editNote.getText()));
        switch (radioGroupColor.getCheckedRadioButtonId()) {
            case R.id.radioButtonRed:
                savedInstanceState.putString("color", "red");
                break;
            case R.id.radioButtonBlue:
                savedInstanceState.putString("color", "blue");
                break;
            case R.id.radioButtonYellow:
                savedInstanceState.putString("color", "yellow");
                break;
            default:
                savedInstanceState.putString("color", "");
                break;
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            editTitle.setText(savedInstanceState.getString("editName"));
            editNote.setText(savedInstanceState.getString("editNote"));
            Color = savedInstanceState.getString("color");
            Title = savedInstanceState.getString("editName");
            Note = savedInstanceState.getString("editNote");
            switch (Color) {
                case "red":
                    radioGroupColor.check(radioButtonRed.getId());
                    break;
                case "blue":
                    radioGroupColor.check(radioButtonBlue.getId());
                    break;
                case "yellow":
                    radioGroupColor.check(radioButtonYellow.getId());
                    break;
                default:
                    radioGroupColor.clearCheck();
                    break;
            }
        }
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        markerLocation = addressList.get(0).getFeatureName();
        marker.setTitle(markerLocation);
        marker.showInfoWindow();
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
    }
}
