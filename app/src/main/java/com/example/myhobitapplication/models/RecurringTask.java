package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RecurringTask extends Task {

    private int recurrenceInterval;
    private RecurrenceUnit recurrenceUnit;
    private LocalDate startDate;
    private LocalDate endDate;

    private RecurringTaskStatus status;

    private Integer FirstRecurringTaskId;

    public RecurringTask(Integer id, String name, String description, Integer difficulty, Integer importance, String categoryId, LocalTime executionTime, int recurrenceInterval, RecurrenceUnit recurrenceUnit, LocalDate startDate, LocalDate endDate, RecurringTaskStatus status, Integer firstRecurringTaskId) {
        super(id, name, description, difficulty, importance, categoryId, executionTime);
        this.recurrenceInterval = recurrenceInterval;
        this.recurrenceUnit = recurrenceUnit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        FirstRecurringTaskId = firstRecurringTaskId;
    }

    public RecurringTask(){

        super();
    }



    public RecurringTask(String name, String description, Integer difficulty, Integer importance, String categoryId, LocalTime executionTime, int recurrenceInterval, RecurrenceUnit recurrenceUnit, LocalDate startDate, LocalDate endDate, RecurringTaskStatus status, Integer firstRecurringTaskId ) {
        super(name, description, difficulty, importance, categoryId, executionTime);
        this.recurrenceInterval = recurrenceInterval;
        this.recurrenceUnit = recurrenceUnit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        FirstRecurringTaskId = firstRecurringTaskId;
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
}
