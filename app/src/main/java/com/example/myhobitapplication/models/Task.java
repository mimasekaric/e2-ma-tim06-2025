package com.example.myhobitapplication.models;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Task {

    private Integer Id;
    private String Name;

    private String Description;

    private Integer Difficulty;

    private Integer Importance;

    private String CategoryColour;

    private LocalTime executionTime;

    private LocalDate finishedDate;
    private LocalDate creationDate;


    public Task(){}
    public Task(Integer id, String name, String description, Integer difficulty, Integer importance, String categoryColour, LocalTime executionTime, LocalDate finishedDate, LocalDate creationDate) {
        Id = id;
        Name = name;
        Description = description;
        Difficulty = difficulty;
        Importance = importance;
        CategoryColour = categoryColour;
        this.executionTime = executionTime;
        this.finishedDate = finishedDate;
        this.creationDate = creationDate;
    }
    public Task(String name, String description, Integer difficulty, Integer importance, String categoryColour, LocalTime executionTime, LocalDate finishedDate, LocalDate creationDate) {
        Name = name;
        Description = description;
        Difficulty = difficulty;
        Importance = importance;
        CategoryColour = categoryColour;
        this.executionTime = executionTime;
        this.finishedDate = finishedDate;
        this.creationDate = creationDate;
    }




    public Integer getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public Integer getDifficulty() {
        return Difficulty;
    }

    public Integer getImportance() {
        return Importance;
    }

    public String getCategoryColour() {
        return CategoryColour;
    }

    public LocalTime getExecutionTime() {
        return executionTime;
    }


    public void setId(Integer id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setDifficulty(Integer difficulty) {
        Difficulty = difficulty;
    }

    public void setImportance(Integer importance) {
        Importance = importance;
    }

    public void setCategoryColour(String categoryColour) {
        CategoryColour = categoryColour;
    }

    public void setExecutionTime(LocalTime executionTime) {
        this.executionTime = executionTime;
    }

    public LocalDate getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(LocalDate finishedDate) {
        this.finishedDate = finishedDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}
