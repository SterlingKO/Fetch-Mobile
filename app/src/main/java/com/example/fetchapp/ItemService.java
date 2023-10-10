package com.example.fetchapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ItemService {
    @GET("hiring.json")
    Call<List<Item>> getItems();
    }

