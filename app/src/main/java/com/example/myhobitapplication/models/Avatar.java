package com.example.myhobitapplication.models;

public class Avatar {


    private int image;

    private String name;

    public Avatar(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public Avatar(){}

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
