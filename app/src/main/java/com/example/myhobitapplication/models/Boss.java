package com.example.myhobitapplication.models;

public class Boss {

    private Integer Id;
    private Integer HP;

    private Integer UserId;

    private Integer CurrentHP;

    private Boolean IsDefeated;

    private Integer BossLevel;

    public Boss(Integer HP) {

        this.HP = HP;
    }

    public Boss() {
    }

    public Boss(Integer HP, Integer currentHP, Integer userId, Boolean isDefeated, Integer bossLevel) {
        this.HP = HP;
        CurrentHP = currentHP;
        UserId = userId;
        IsDefeated = isDefeated;
        BossLevel = bossLevel;
    }

    public Boss(Integer id, Integer HP, Integer userId, Integer currentHP, Boolean isDefeated, Integer bossLevel) {
        Id = id;
        this.HP = HP;
        UserId = userId;
        CurrentHP = currentHP;
        IsDefeated = isDefeated;
        BossLevel = bossLevel;
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

    public void calculateHP(){

        setHP(getHP()*2+getHP()/2);
    }
}
