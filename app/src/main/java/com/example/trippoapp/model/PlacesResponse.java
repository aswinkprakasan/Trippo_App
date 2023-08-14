package com.example.trippoapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesResponse {
    @SerializedName("results")
    private List<Plase> results;

    public List<Plase> getResults() {
        return results;
    }
}
