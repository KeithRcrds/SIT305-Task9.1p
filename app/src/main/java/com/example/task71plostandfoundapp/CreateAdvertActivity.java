package com.example.task71plostandfoundapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.net.Uri;
import android.widget.ImageView;

public class CreateAdvertActivity extends AppCompatActivity {

    RadioGroup radioGroupType;
    Spinner spinnerCategory;
    EditText etName, etPhone, etDescription, etDate, etLocation;
    Button btnUploadImage, btnSave;
    ImageView imageView;
    Uri selectedImageUri;
    ActivityResultLauncher<String> imagePickerLauncher;

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

        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSave = findViewById(R.id.btnSave);

        imageView = findViewById(R.id.imageView);

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
                    timestamp
            );
        });
    }
}