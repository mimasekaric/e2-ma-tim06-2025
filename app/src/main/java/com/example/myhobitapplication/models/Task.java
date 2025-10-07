package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.TaskQuote;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private Duration remainingTime;

    private LocalDateTime finishDate;

    private LocalDate startDate;
    private String userUid;

    private boolean isAwarded = false;
    private TaskQuote importanceType;
    private TaskQuote difficultyType;


    public Task(){}
    public Task(Integer id, String name, String description, Integer difficulty, Integer importance, String categoryColour, LocalTime executionTime, LocalDate finishedDate, LocalDate creationDate, LocalDate startDate, String userUid) {
        Id = id;
        Name = name;
        Description = description;
        Difficulty = difficulty;
        Importance = importance;
        CategoryColour = categoryColour;
        this.executionTime = executionTime;
        this.finishedDate = finishedDate;
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.userUid = userUid;
        this.finishDate = LocalDateTime.of(startDate,executionTime).plusDays(3);
        this.remainingTime = Duration.between(LocalDateTime.of(startDate,executionTime), this.finishDate);
    }
    public Task(String name, String description, Integer difficulty, Integer importance, String categoryColour, LocalTime executionTime, LocalDate finishedDate, LocalDate creationDate, LocalDate startDate, String userUid) {
        Name = name;
        Description = description;
        Difficulty = difficulty;
        Importance = importance;
        CategoryColour = categoryColour;
        this.executionTime = executionTime;
        this.finishedDate = finishedDate;
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.userUid = userUid;
        this.finishDate = LocalDateTime.of(startDate,executionTime).plusDays(3);
        this.remainingTime = Duration.between(LocalDateTime.of(startDate,executionTime), this.finishDate);
    }

    public Task(Integer id, String name, String description, Integer difficulty, Integer importance, String categoryColour, LocalTime executionTime, LocalDate finishedDate, LocalDate creationDate, LocalDate startDate, String userUid, LocalDateTime finishDate, Duration remainingTime) {
        Id = id;
        Name = name;
        Description = description;
        Difficulty = difficulty;
        Importance = importance;
        CategoryColour = categoryColour;
        this.executionTime = executionTime;
        this.finishedDate = finishedDate;
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.userUid = userUid;
        this.finishDate = finishDate;
        this.remainingTime = remainingTime;
    }

    public Task(Integer id, String name, String description, Integer difficulty, Integer importance, String categoryColour, LocalTime executionTime, LocalDate finishedDate, LocalDate creationDate, LocalDate startDate, String userUid, LocalDateTime finishDate, Duration remainingTime, boolean isAwarded) {
        Id = id;
        Name = name;
        Description = description;
        Difficulty = difficulty;
        Importance = importance;
        this.isAwarded= isAwarded;
        CategoryColour = categoryColour;
        this.executionTime = executionTime;
        this.finishedDate = finishedDate;
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.userUid = userUid;
        this.finishDate = finishDate;
        this.remainingTime = remainingTime;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public boolean isAwarded() {
        return isAwarded;
    }

    public void setAwarded(boolean awarded) {
        isAwarded = awarded;
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

//    public TaskQuote getDifficultyQuota() {
//
//        if (this.Difficulty == null || this.Importance == null) {
//            return TaskQuote.NO_QUOTA;
//        }
//
//        if (this.Difficulty == 1) {
//            return TaskQuote.VERY_EASY;
//        }
//        if (this.Difficulty == 3) {
//            return TaskQuote.EASY;
//        }
//        if (this.Difficulty == 7) {
//            return TaskQuote.HARD;
//        }
//        if (this.Difficulty == 20) {
//            return TaskQuote.EXTREMELY_HARD;
//        }
//        return TaskQuote.NO_QUOTA;
//    }
//    public TaskQuote getImportanceQuota(){
//        if (this.Importance == null ) {
//            return TaskQuote.NO_QUOTA;
//        }
//
//        if (this.Importance == 1) {
//            return TaskQuote.NORMAL;
//        }
//        if (this.Importance == 3) {
//            return TaskQuote.IMPORTANT;
//        }
//        if (this.Difficulty == 10) {
//            return TaskQuote.EXTREMELY_IMPORTANT;
//        }
//        if (this.Difficulty == 100) {
//            return TaskQuote.SPECIAL;
//        }
//        return TaskQuote.NO_QUOTA;
//    }

    public TaskQuote getImportanceQuota() {
        return importanceType;
    }
    public TaskQuote getDifficultyQuota(){
        return  difficultyType;
    }

    public void setImportanceQuota(TaskQuote importanceType) {
        this.importanceType = importanceType;
    }
    public void setDifficultyQuota(TaskQuote difficultyType){
        this.difficultyType = difficultyType;
    }
    public int getTotalXp() {
        int difficultyXp = (this.Difficulty != null) ? this.Difficulty : 0;
        int importanceXp = (this.Importance != null) ? this.Importance : 0;
        return difficultyXp + importanceXp;
    }

}
