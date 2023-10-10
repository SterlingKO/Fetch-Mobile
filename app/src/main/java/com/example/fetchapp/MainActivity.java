package com.example.fetchapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl("https://fetch-hiring.s3.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ItemService itemService = retrofit.create(ItemService.class);

        Call<List<Item>> call = itemService.getItems();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    List<Item> items = response.body();
                    displayItems(items);
                }
            }

            @Override
            public void onFailure( @NonNull Call<List<Item>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void displayItems(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        Map<Integer, List<Item>> groupedItems = new TreeMap<>();
        for (Item item : items) {
            if (item.getName() != null && !item.getName().isEmpty()) {
                if (!groupedItems.containsKey(item.getListId())) {
                    groupedItems.put(item.getListId(), new ArrayList<>());
                }
                groupedItems.get(item.getListId()).add(item);
            }
        }

        List<String> sortedItemList = new ArrayList<>();
        for (Map.Entry<Integer, List<Item>> entry : groupedItems.entrySet()) {
            Collections.sort(entry.getValue(), Comparator.comparing(Item::getName));
            for (Item item : entry.getValue()) {
                sortedItemList.add("ListId: " + entry.getKey() + ", Name: " + item.getName());
            }
        }
        adapter.clear();
        adapter.addAll(sortedItemList);
        adapter.notifyDataSetChanged();
    }
}