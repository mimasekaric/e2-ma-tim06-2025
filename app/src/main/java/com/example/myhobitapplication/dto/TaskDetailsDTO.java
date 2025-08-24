package com.example.myhobitapplication.dto;

import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class TaskDetailsDTO {

    private Integer Id;
    private String Name;

    private String Description;

    private Integer Difficulty;

    private Integer Importance;

    private Integer CategoryId;

    private LocalTime executionTime;


    private int recurrenceInterval;
    private RecurrenceUnit recurrenceUnit;
    private LocalDate startDate;
    private LocalDate endDate;

    private RecurringTaskStatus status;

    private String CategoryColour;


    public TaskDetailsDTO(Integer id, String name, String description, Integer difficulty, Integer importance, Integer categoryId, LocalTime executionTime, int recurrenceInterval, RecurrenceUnit recurrenceUnit, LocalDate startDate, LocalDate endDate, RecurringTaskStatus status, String categoryColour) {
        Id = id;
        Name = name;
        Description = description;
        Difficulty = difficulty;
        Importance = importance;
        CategoryId = categoryId;
        this.executionTime = executionTime;
        this.recurrenceInterval = recurrenceInterval;
        this.recurrenceUnit = recurrenceUnit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        CategoryColour = categoryColour;
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

    public Integer getCategoryId() {
        return CategoryId;
    }

    public LocalTime getExecutionTime() {
        return executionTime;
    }

    public int getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public RecurrenceUnit getRecurrenceUnit() {
        return recurrenceUnit;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public RecurringTaskStatus getStatus() {
        return status;
    }

    public String getCategoryColour() {
        return CategoryColour;
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

    public void setCategoryId(Integer categoryId) {
        CategoryId = categoryId;
    }

    public void setExecutionTime(LocalTime executionTime) {
        this.executionTime = executionTime;
    }

    public void setRecurrenceInterval(int recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

    public void setRecurrenceUnit(RecurrenceUnit recurrenceUnit) {
        this.recurrenceUnit = recurrenceUnit;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStatus(RecurringTaskStatus status) {
        this.status = status;
    }

    public void setCategoryColour(String categoryColour) {
        CategoryColour = categoryColour;
    }
}
