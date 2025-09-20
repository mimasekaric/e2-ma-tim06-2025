package com.example.myhobitapplication.dto;

import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.models.RecurringTask;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RecurringTaskDTO {


    private Integer Id;
    private String Name;

    private String Description;

    private Integer Difficulty;

    private Integer Importance;

    private String CategoryColour;

    private LocalTime executionTime;

    private int recurrenceInterval;
    private RecurrenceUnit recurrenceUnit;
    private LocalDate startDate;
    private LocalDate endDate;

    private RecurringTaskStatus status;

    private Integer FirstRecurringTaskId;

    private LocalDate FinishedDate;
    private LocalDate CreationDate;
    private String userUid;

    private Duration remainingTime;

    private LocalDateTime finishDate;

    public RecurringTaskDTO(Integer id, String name, String description, Integer difficulty, Integer importance, String categoryColour, LocalTime executionTime, int recurrenceInterval, RecurrenceUnit recurrenceUnit, LocalDate startDate, LocalDate endDate, RecurringTaskStatus status, Integer firstRecurringTaskId, LocalDate finishedDate, LocalDate creationDate, String userUid) {
        Id = id;
        Name = name;
        Description = description;
        Difficulty = difficulty;
        Importance = importance;
        CategoryColour = categoryColour;
        this.executionTime = executionTime;
        this.recurrenceInterval = recurrenceInterval;
        this.recurrenceUnit = recurrenceUnit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        FirstRecurringTaskId = firstRecurringTaskId;
        FinishedDate = finishedDate;
        CreationDate = creationDate;
        this.userUid = userUid;
    }


    public RecurringTaskDTO(RecurringTask taskModel) {
            Id = taskModel.getId();
            Name =  taskModel.getName();
            Description = taskModel.getDescription();
            Difficulty =  taskModel.getDifficulty();
            Importance =  taskModel.getImportance();
            CategoryColour =  taskModel.getCategoryColour();
            this.executionTime =  taskModel.getExecutionTime();
            this.recurrenceInterval =  taskModel.getRecurrenceInterval();
            this.recurrenceUnit =  taskModel.getRecurrenceUnit();
            this.startDate =   taskModel.getStartDate();
            this.endDate =  taskModel.getEndDate();
            this.status =  taskModel.getStatus();
            FirstRecurringTaskId = taskModel.getFirstRecurringTaskId();
            FinishedDate = taskModel.getFinishedDate();
            CreationDate = taskModel.getCreationDate();
            this.userUid = taskModel.getUserUid();
            this.finishDate = taskModel.getFinishDate();
            this.remainingTime = taskModel.getRemainingTime();
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

    public Integer getDifficulty() {
        return Difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        Difficulty = difficulty;
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

    public RecurrenceUnit getRecurrenceUnit() {
        return recurrenceUnit;
    }

    public void setRecurrenceUnit(RecurrenceUnit recurrenceUnit) {
        this.recurrenceUnit = recurrenceUnit;
    }

    public int getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public void setRecurrenceInterval(int recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public RecurringTaskStatus getStatus() {
        return status;
    }

    public void setStatus(RecurringTaskStatus status) {
        this.status = status;
    }

    public Integer getFirstRecurringTaskId() {
        return FirstRecurringTaskId;
    }

    public void setFirstRecurringTaskId(Integer firstRecurringTaskId) {
        FirstRecurringTaskId = firstRecurringTaskId;
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

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }


    public Duration getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Duration remainingTime) {
        this.remainingTime = remainingTime;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }
}
