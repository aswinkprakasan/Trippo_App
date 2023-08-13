package com.example.trippoapp.model;

public class AdminRecycleClass {
    String name, season, id;

    public AdminRecycleClass(String name, String season, String id) {
        this.name = name;
        this.season = season;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
