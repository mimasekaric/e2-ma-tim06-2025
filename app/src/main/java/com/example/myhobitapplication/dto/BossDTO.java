package com.example.myhobitapplication.dto;

import com.example.myhobitapplication.models.Boss;

public class BossDTO {

    private Integer Id;
    private Integer HP;

    private String UserId;

    private Integer CurrentHP;

    private Boolean IsDefeated;

    private Integer BossLevel;

    private Integer CoinsReward;

    public BossDTO(Integer HP) {

        this.HP = HP;
    }

    public BossDTO() {
    }

    public BossDTO(Integer id, Integer HP, String userId, Integer currentHP, Boolean isDefeated, Integer bossLevel, Integer coinsReward) {
        Id = id;
        this.HP = HP;
        UserId = userId;
        CurrentHP = currentHP;
        IsDefeated = isDefeated;
        BossLevel = bossLevel;
        CoinsReward = coinsReward;
    }

    public BossDTO(Integer HP, String userId, Integer currentHP, Boolean isDefeated, Integer bossLevel, Integer coinsReward) {
        this.HP = HP;
        UserId = userId;
        CurrentHP = currentHP;
        IsDefeated = isDefeated;
        BossLevel = bossLevel;
        CoinsReward = coinsReward;
    }

    public BossDTO(Boss boss) {
        Id = boss.getId();
        this.HP = boss.getHP();
        UserId = boss.getUserId();
        CurrentHP = boss.getCurrentHP();
        IsDefeated = boss.getDefeated();
        BossLevel = boss.getBossLevel();
        CoinsReward = boss.getCoinsReward();
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


    public Integer getCoinsReward() {
        return CoinsReward;
    }

    public void setCoinsReward(Integer coinsReward) {
        CoinsReward = coinsReward;
    }
}
