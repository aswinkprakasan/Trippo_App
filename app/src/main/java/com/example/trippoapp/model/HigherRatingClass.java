package com.example.trippoapp.model;

public class HigherRatingClass {

    String name, id, rat;
    int pic;

    public HigherRatingClass(String name, String id, String rat, int pic) {
        this.name = name;
        this.id = id;
        this.rat = rat;
        this.pic = pic;
    }

    public String getRat() {
        return rat;
    }

    public void setRat(String rat) {
        this.rat = rat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }
}
