package com.example.task71plostandfoundapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ItemRemoveActivity extends AppCompatActivity {

    TextView tvPostTitle, tvDate, tvLocation;
    Button btnDelete;

    int itemId;
    String postType, description, date, location, timestamp;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_remove);

        tvPostTitle = findViewById(R.id.tvPostTitle);
        tvDate = findViewById(R.id.tvDate);
        tvLocation = findViewById(R.id.tvLocation);
        btnDelete = findViewById(R.id.btnDelete);

        db = new DatabaseHelper(this);

        itemId = getIntent().getIntExtra("id", -1);
        postType = getIntent().getStringExtra("postType");
        description = getIntent().getStringExtra("description");
        date = getIntent().getStringExtra("date");
        location = getIntent().getStringExtra("location");
        timestamp = getIntent().getStringExtra("timestamp");

        tvPostTitle.setText(postType + " " + description);
        tvDate.setText(date + "\nCreated: " + timestamp);
        tvLocation.setText(location);

        btnDelete.setOnClickListener(v -> {
            db.deleteItem(itemId);
        });
    }
}