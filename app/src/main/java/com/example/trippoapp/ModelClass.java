package com.example.trippoapp;

import android.graphics.Bitmap;

public class ModelClass {
    private String name, email, num, pass;

    public ModelClass(String name, String email, String num, String pass) {
        this.name = name;
        this.email = email;
        this.num = num;
        this.pass = pass;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }


    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
