package com.example.trippoapp.model;

import android.graphics.Bitmap;

public class RecycleSeasonClass {

    String name, location, id;
    int pic;

    public RecycleSeasonClass(String name, String location, String id, int pic) {
        this.name = name;
        this.location = location;
        this.id = id;
        this.pic = pic;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
