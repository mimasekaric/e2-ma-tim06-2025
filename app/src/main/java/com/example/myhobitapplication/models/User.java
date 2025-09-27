package com.example.myhobitapplication.models;


import java.util.Date;

public class User {
    public String uid;
    public String username;
    public String password;
    public String email;
    public String avatarName;
    public Boolean isRegistered;
    public Date registrationDate;

    public User(String uid, String email, String username, String password, String avatarName,Date registrationDate) {
        this.uid= uid;
        this.email = email;
        this.username = username;
        this.password = password;
        this.avatarName = avatarName;
        this.registrationDate = registrationDate;
        isRegistered = false;
    }

    public User( String email, String username, String password, String avatarName, Date registrationDate) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.avatarName = avatarName;
        this.registrationDate = registrationDate;
        isRegistered = false;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getRegistered() {
        return isRegistered;
    }

    public void setRegistered(Boolean registered) {
        this.isRegistered = registered;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getpassword() {
        return password;
    }

    public String getusername() {
        return username;
    }

    public String getemail() {
        return email;
    }

    public String getavatarName() {
        return avatarName;
    }

    public void setpassword(String password) {
        this.password = password;
    }

    public void setemail(String email) {
        this.email = email;
    }
}
