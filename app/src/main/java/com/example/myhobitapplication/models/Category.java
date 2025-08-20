package com.example.myhobitapplication.models;

public class Category {

    private Integer Id;
    private String Name;

    private String Colour;


    public Category(String name, String colour) {
        Name = name;
        Colour = colour;
    }

    public Integer getId(){
        return Id;
    }


    public String getName() {
        return Name;
    }


    public String getColour() {
        return Colour;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setColour(String colour) {
        Colour = colour;
    }

    public void setId(Integer id) {
        Id = id;
    }
}
