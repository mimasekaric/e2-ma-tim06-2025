package com.example.myhobitapplication.models;

public class User {
    public String id;
    public String username;
    public String password;
    public String email;
    public Integer avatarid;

    public User(String id, String email, String username, String password, Integer avatarid) {
        id= id;
        email = email;
        username = username;
        password = password;
        avatarid = avatarid;
    }

    public User( String email, String username, String password, Integer avatarid) {
        email = email;
        username = username;
        password = password;
        avatarid = avatarid;
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

    public Integer getavatarid() {
        return avatarid;
    }

    public void setpassword(String password) {
        password = password;
    }

    public void setemail(String email) {
        email = email;
    }
}
