package com.example.myhobitapplication.models;

import java.util.UUID;

public class Boss {

    private Integer Id;
    private Integer HP;

    private String UserId;

    private Integer CurrentHP;

    private Boolean IsDefeated;

    private Integer BossLevel;

    private Integer CoinsReward;

    public Boss(Integer HP) {

        this.HP = HP;
    }

    public Boss() {
    }

    public Boss(Integer id, Integer HP, String userId, Integer currentHP, Boolean isDefeated, Integer bossLevel, Integer coinsReward) {
        Id = id;
        this.HP = HP;
        UserId = userId;
        CurrentHP = currentHP;
        IsDefeated = isDefeated;
        BossLevel = bossLevel;
        CoinsReward = coinsReward;
    }

    public Boss(Integer HP, String userId, Integer currentHP, Boolean isDefeated, Integer bossLevel, Integer coinsReward) {
        this.HP = HP;
        UserId = userId;
        CurrentHP = currentHP;
        IsDefeated = isDefeated;
        BossLevel = bossLevel;
        CoinsReward = coinsReward;
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

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Integer getCurrentHP() {
        return CurrentHP;
    }

    public void setCurrentHP(Integer currentHP) {
        CurrentHP = currentHP;
    }

    public Boolean getDefeated() {
        return IsDefeated;
    }

    public void setDefeated(Boolean defeated) {
        IsDefeated = defeated;
    }

    public Integer getBossLevel() {
        return BossLevel;
    }

    public void setBossLevel(Integer bossLevel) {
        BossLevel = bossLevel;
    }

    public void calculateHP(){

        setHP(getHP()*2+getHP()/2);
    }

    public Integer getCoinsReward() {
        return CoinsReward;
    }

    public void setCoinsReward(Integer coinsReward) {
        CoinsReward = coinsReward;
    }
}
