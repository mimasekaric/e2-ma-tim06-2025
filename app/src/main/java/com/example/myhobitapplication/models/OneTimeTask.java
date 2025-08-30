package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.OneTimeTaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class OneTimeTask extends Task{

    private OneTimeTaskStatus status;

    public OneTimeTask(Integer id, String name, String description, Integer difficulty, Integer importance, String categoryId, LocalTime executionTime, OneTimeTaskStatus status) {
        super(id, name, description, difficulty, importance, categoryId, executionTime);
        this.status = status;
    }

    public OneTimeTaskStatus getStatus() {
        return status;
    }
    public void setStatus(OneTimeTaskStatus status) {
        this.status = status;
    }


}
