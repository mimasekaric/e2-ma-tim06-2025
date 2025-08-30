package com.example.myhobitapplication.models;


import java.util.Date;

public class User {
    public String id;
    public String username;
    public String password;
    public String email;
    public String avatarName;
    public Boolean isRegistered;
    public Date registrationDate;

    public User(String id, String email, String username, String password, String avatarName,Date registrationDate) {
        id= id;
        email = email;
        username = username;
        password = password;
        avatarName = avatarName;
        registrationDate = registrationDate;
        isRegistered = false;
    }

    public User( String email, String username, String password, String avatarName, Date registrationDate) {
        email = email;
        username = username;
        password = password;
        avatarName = avatarName;
        registrationDate = registrationDate;
        isRegistered = false;
    }

    public User() {
    }

    public Boolean getRegistered() {
        return isRegistered;
    }

    public void setRegistered(Boolean registered) {
        isRegistered = registered;
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
        password = password;
    }

    public void setemail(String email) {
        email = email;
    }
}
