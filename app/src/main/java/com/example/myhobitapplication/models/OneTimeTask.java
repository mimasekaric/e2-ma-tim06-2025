package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.OneTimeTaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class OneTimeTask extends Task{

    private OneTimeTaskStatus status;

    //private LocalDate startDate;

    public OneTimeTask( ) {

    }



    public OneTimeTask(String name, String description, Integer difficulty, Integer importance, String categoryColour, LocalTime executionTime, LocalDate finishedDate, LocalDate creationDate, OneTimeTaskStatus status, LocalDate startDate) {
        super(name, description, difficulty, importance, categoryColour, executionTime, finishedDate, creationDate, startDate);
        this.status = status;
       // this.startDate = startDate;
    }

    public OneTimeTask(Integer id, String name, String description, Integer difficulty, Integer importance, String categoryId, LocalTime executionTime, OneTimeTaskStatus status, LocalDate finishedDate, LocalDate creationDate, LocalDate startDate) {
        super(id, name, description, difficulty, importance, categoryId, executionTime, finishedDate, creationDate, startDate);
        this.status = status;
       // this.startDate = startDate;
    }

    public OneTimeTaskStatus getStatus() {
        return status;
    }
    public void setStatus(OneTimeTaskStatus status) {
        this.status = status;
    }


}
