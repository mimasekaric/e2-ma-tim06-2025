package com.example.myhobitapplication.dto;

import com.example.myhobitapplication.enums.OneTimeTaskStatus;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.models.OneTimeTask;
import com.example.myhobitapplication.models.RecurringTask;

import java.time.LocalDate;
import java.time.LocalTime;

public class OneTimeTaskDTO {


    private Integer Id;
    private String Name;

    private String Description;

    private Integer Difficulty;

    private Integer Importance;

    private String CategoryColour;

    private LocalTime executionTime;

    private LocalDate startDate;

    private OneTimeTaskStatus status;

    private LocalDate FinishedDate;
    private LocalDate CreationDate;



    public OneTimeTaskDTO() {
    }
    public OneTimeTaskDTO(Integer id, String name, String description, Integer difficulty, Integer importance, String categoryColour, LocalTime executionTime, LocalDate startDate, OneTimeTaskStatus status, LocalDate finishedDate, LocalDate creationDate) {
        Id = id;
        Name = name;
        Description = description;
        Difficulty = difficulty;
        Importance = importance;
        CategoryColour = categoryColour;
        this.executionTime = executionTime;
        this.startDate = startDate;
        this.status = status;
        FinishedDate = finishedDate;
        CreationDate = creationDate;
    }

    public OneTimeTaskDTO(String name, String description, Integer difficulty, String categoryColour, Integer importance, LocalTime executionTime, LocalDate startDate, OneTimeTaskStatus status, LocalDate finishedDate, LocalDate creationDate) {
        Name = name;
        Description = description;
        Difficulty = difficulty;
        CategoryColour = categoryColour;
        Importance = importance;
        this.executionTime = executionTime;
        this.startDate = startDate;
        this.status = status;
        FinishedDate = finishedDate;
        CreationDate = creationDate;
    }

    public OneTimeTaskDTO(OneTimeTask taskModel) {
        Id = taskModel.getId();
        Name =  taskModel.getName();
        Description = taskModel.getDescription();
        Difficulty =  taskModel.getDifficulty();
        Importance =  taskModel.getImportance();
        CategoryColour =  taskModel.getCategoryColour();
        this.executionTime =  taskModel.getExecutionTime();
        this.startDate =   taskModel.getStartDate();
        this.status = taskModel.getStatus();
        FinishedDate = taskModel.getFinishedDate();
        CreationDate = taskModel.getCreationDate();
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Integer getImportance() {
        return Importance;
    }

    public void setImportance(Integer importance) {
        Importance = importance;
    }

    public Integer getDifficulty() {
        return Difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        Difficulty = difficulty;
    }

    public String getCategoryColour() {
        return CategoryColour;
    }

    public void setCategoryColour(String categoryColour) {
        CategoryColour = categoryColour;
    }

    public LocalTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalTime executionTime) {
        this.executionTime = executionTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public OneTimeTaskStatus getStatus() {
        return status;
    }

    public void setStatus(OneTimeTaskStatus status) {
        this.status = status;
    }

    public LocalDate getFinishedDate() {
        return FinishedDate;
    }

    public void setFinishedDate(LocalDate finishedDate) {
        FinishedDate = finishedDate;
    }

    public LocalDate getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        CreationDate = creationDate;
    }
}
