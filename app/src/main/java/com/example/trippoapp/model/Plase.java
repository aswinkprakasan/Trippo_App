package com.example.trippoapp.model;

import com.google.gson.annotations.SerializedName;

public class Plase {
    @SerializedName("name")
    private String name;
    @SerializedName("geometry")
    private Geometry geometry;

    public String getName() {
        return name;
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
