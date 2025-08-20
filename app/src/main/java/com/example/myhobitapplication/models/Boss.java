package com.example.myhobitapplication.models;

public class Boss {

    private Integer Id;
    private Integer HP;

    public Boss(Integer HP) {

        this.HP = HP;
    }

    public Integer getId() {
        return Id;
    }

    public Integer getHP() {
        return HP;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public void setHP(Integer HP) {
        this.HP = HP;
    }
}
