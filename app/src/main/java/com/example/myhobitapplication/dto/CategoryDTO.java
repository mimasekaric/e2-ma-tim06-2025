package com.example.myhobitapplication.dto;

public class CategoryDTO {


    private Integer Id;
    private String Name;

    private String Colour;


    public CategoryDTO(Integer id, String name, String colour) {
        Id = id;
        Name = name;
        Colour = colour;
    }

    public CategoryDTO(String name, String colour) {
        Name = name;
        Colour = colour;
    }

    public CategoryDTO() {
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getColour() {
        return Colour;
    }

    public void setColour(String colour) {
        Colour = colour;
    }
}
