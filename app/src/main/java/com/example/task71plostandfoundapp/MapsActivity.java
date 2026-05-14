package com.example.task71plostandfoundapp;

import android.os.Bundle;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final double RADIUS_KM = 10.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng deakin = new LatLng(-37.8475, 145.1147);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deakin, 15));

        showItemsWithinRadius();
    }

    @SuppressLint("MissingPermission")
    private void showItemsWithinRadius() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                return;
            }

            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

            DatabaseHelper db = new DatabaseHelper(this);
            List<Item> itemList = db.getAllItemsList();

            for (Item item : itemList) {
                double itemLat = item.getLatitude();
                double itemLng = item.getLongitude();

                if (itemLat == 0.0 && itemLng == 0.0) {
                    continue;
                }

                float[] results = new float[1];

                Location.distanceBetween(
                        location.getLatitude(),
                        location.getLongitude(),
                        itemLat,
                        itemLng,
                        results
                );

                double distanceKm = results[0] / 1000.0;

                if (distanceKm <= RADIUS_KM) {
                    LatLng itemLocation = new LatLng(itemLat, itemLng);

                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(itemLocation)
                                    .title(item.getName())
                                    .snippet(item.getDescription())
                    );
                }
            }
        });
    }
}