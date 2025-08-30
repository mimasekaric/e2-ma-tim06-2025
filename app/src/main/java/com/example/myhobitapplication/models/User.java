package com.example.myhobitapplication.models;

public class User {
    public String id;
    public String username;
    public String password;
    public String email;
    public String avatarName;

    public User(String id, String email, String username, String password, String avatarName) {
        id= id;
        email = email;
        username = username;
        password = password;
        avatarName = avatarName;
    }

    public User( String email, String username, String password, String avatarName) {
        email = email;
        username = username;
        password = password;
        avatarName = avatarName;
    }

    public User() {
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
