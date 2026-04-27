package com.example.task71plostandfoundapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;

public class ItemListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ItemAdapter adapter;
    DatabaseHelper db;
    Spinner spinnerFilter;
    List<Item> allItems;
    List<Item> filteredItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinnerFilter = findViewById(R.id.spinnerFilter);

        db = new DatabaseHelper(this);

        allItems = db.getAllItemsList();
        filteredItems = new ArrayList<>(allItems);

        adapter = new ItemAdapter(this, filteredItems);
        recyclerView.setAdapter(adapter);

        String[] categories = {"All", "Electronics", "Pets", "Wallets"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterItems(categories[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void filterItems(String category) {
        filteredItems.clear();

        if (category.equals("All")) {
            filteredItems.addAll(allItems);
        } else {
            for (Item item : allItems) {
                if (item.getCategory().equals(category)) {
                    filteredItems.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}