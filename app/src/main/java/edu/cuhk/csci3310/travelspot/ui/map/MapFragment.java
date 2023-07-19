package edu.cuhk.csci3310.travelspot.ui.map;

import static edu.cuhk.csci3310.travelspot.ults.CommonUlts.getMarkerIcon;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import edu.cuhk.csci3310.travelspot.R;
import edu.cuhk.csci3310.travelspot.manager.DatabaseManager;
import edu.cuhk.csci3310.travelspot.manager.GeofenceManager;
import edu.cuhk.csci3310.travelspot.ui.spotdetail.SpotDetailActivity;
import edu.cuhk.csci3310.travelspot.model.Spot;
import edu.cuhk.csci3310.travelspot.ults.CommonUlts;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapFragment";
    private static final String DEFAULT_COLOR = "cyan";
    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 101;
    private static final int ZOOM_MARGIN = 150;
    private static final int ZOOM_BUTTON = 1;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 102;
    private DatabaseManager databaseManager;
    private Geocoder geocoder;
    private GoogleMap mMap;
    private Activity activity;
    private Context context;
    private Marker newMarker;
    private Circle newCircle;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
        context = this.getContext();
        geocoder = new Geocoder(context);
        databaseManager = new DatabaseManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SearchView searchView = view.findViewById(R.id.idSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || location.equals("")) {
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 180, 0);

        View zoomControls = mapFragment.getView().findViewById(ZOOM_BUTTON);

        if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams params_zoom = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

            params_zoom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params_zoom.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            params_zoom.setMargins(margin, margin, margin, margin + ZOOM_MARGIN);

        }

        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng home = new LatLng(22.3193, 114.1713);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 18));
        enableUserLocation();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        loadSavedSpot();
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Intent intent = new Intent(context, SpotDetailActivity.class);

        LatLng markerPosition = marker.getPosition();
        Double lat = markerPosition.latitude;
        Double lng = markerPosition.longitude;
        Integer ID = -1;
        if (marker.getTag() != null) {
            ID = (Integer) marker.getTag();
        }

        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String markerLocation = addressList.get(0).getFeatureName();

        intent.putExtra("markerLocation", markerLocation);
        intent.putExtra("markerLat", lat);
        intent.putExtra("markerLng", lng);
        intent.putExtra("spotID", ID);

        context.startActivity(intent);
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        } else {
            handleMapLongClick(latLng);
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new
                    String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
        }

        mMap.setMyLocationEnabled(true);
    }

    private void handleMapLongClick(LatLng latLng) {
        newMarker = addMarker(latLng);
        newCircle = addCircle(latLng, CommonUlts.GEOFENCE_RADIUS);
    }

    private Marker addMarker(LatLng latLng) {
        if (newMarker != null) {
            newMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(getMarkerIcon(DEFAULT_COLOR));

        return mMap.addMarker(markerOptions);
    }

    private Marker addMarker(Spot spot) {
        LatLng latLng = new LatLng(spot.getLatitude(), spot.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(spot.getTitle())
                .icon(getMarkerIcon(spot.getColor()));

        return mMap.addMarker(markerOptions);
    }

    private void loadSavedSpot() {
        List<Spot> savedSpots = databaseManager.getAllSpots();
        for(Spot spot:savedSpots) {
            Marker marker = addMarker(spot);
            marker.setTag(spot.getId());
        }
    }

    private Circle addCircle(LatLng latLng, float radius) {
        if (newCircle != null) {
            newCircle.remove();
        }

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 128,128,128));
        circleOptions.fillColor(Color.argb(64, 169,169,169));
        circleOptions.strokeWidth(4);

        return mMap.addCircle(circleOptions);
    }

}
