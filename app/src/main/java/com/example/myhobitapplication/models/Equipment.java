package com.example.myhobitapplication.models;

public abstract class Equipment {

    private Integer id;

    private Boolean activated;


    public Equipment(Integer id, Boolean activated) {
        this.id = id;
        this.activated = activated;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }
}
