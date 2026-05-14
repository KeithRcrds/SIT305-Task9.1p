package com.example.task71plostandfoundapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.net.Uri;
import android.widget.ImageView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class CreateAdvertActivity extends AppCompatActivity {

    RadioGroup radioGroupType;
    Spinner spinnerCategory;
    EditText etName, etPhone, etDescription, etDate, etLocation;
    Button btnUploadImage, btnSave, btnGetCurrentLocation;
    ImageView imageView;
    Uri selectedImageUri;
    ActivityResultLauncher<String> imagePickerLauncher;
    FusedLocationProviderClient fusedLocationClient;

    double latitude = 0.0;
    double longitude = 0.0;

    private ActivityResultLauncher<Intent> autocompleteLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                            Place place = Autocomplete.getPlaceFromIntent(result.getData());

                            String address = place.getAddress();
                            LatLng latLng = place.getLatLng();

                            if (address != null) {
                                etLocation.setText(address);
                            } else if (place.getName() != null) {
                                etLocation.setText(place.getName());
                            }

                            if (latLng != null) {
                                latitude = latLng.latitude;
                                longitude = latLng.longitude;
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        radioGroupType = findViewById(R.id.radioGroupType);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);

        etLocation.setFocusable(false);
        etLocation.setOnClickListener(v -> {
            openPlaceAutocomplete();
        });

        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSave = findViewById(R.id.btnSave);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);

        imageView = findViewById(R.id.imageView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        String[] categories = {"Electronics", "Pets", "Wallets"};

        android.widget.ArrayAdapter<String> adapter =
                new android.widget.ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(adapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imageView.setImageURI(uri);
                    }
                }
        );

        btnUploadImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnGetCurrentLocation.setOnClickListener(v -> {
            getCurrentLocation();
        });

        btnSave.setOnClickListener(v -> {

            if (selectedImageUri == null) {
                android.widget.Toast.makeText(this,
                        "Please upload an image",
                        android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = radioGroupType.getCheckedRadioButtonId();
            String postType = "";

            if (selectedId == R.id.radioLost) {
                postType = "Lost";
            } else if (selectedId == R.id.radioFound) {
                postType = "Found";
            }

            String category = spinnerCategory.getSelectedItem().toString();

            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String description = etDescription.getText().toString();
            String date = etDate.getText().toString();
            String location = etLocation.getText().toString();
            String imageUri = selectedImageUri != null ? selectedImageUri.toString() : "";
            java.text.SimpleDateFormat sdf =
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            String timestamp = sdf.format(new java.util.Date());

            DatabaseHelper db = new DatabaseHelper(this);

            db.insertItem(
                    postType,
                    category,
                    name,
                    phone,
                    description,
                    date,
                    location,
                    imageUri,
                    timestamp,
                    latitude,
                    longitude
            );

        });
    }

    private void openPlaceAutocomplete() {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        );

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fields
        ).build(this);

        autocompleteLauncher.launch(intent);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1001);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                etLocation.setText(latitude + ", " + longitude);
            }
        });
    }
}