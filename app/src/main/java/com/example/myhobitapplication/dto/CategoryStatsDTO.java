package com.example.myhobitapplication.dto;

public class CategoryStatsDTO {
    private final String name;
    private final String colour;
    private final int count;

    public CategoryStatsDTO(String name, String colour, int count) {
        this.name = name;
        this.colour = colour;
        this.count = count;
    }

    public String getName() { return name; }
    public String getColour() { return colour; }
    public int getCount() { return count; }
}
