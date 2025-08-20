package com.example.myhobitapplication.models;

public class User {
    public Integer Id;
    public String Username;
    public String Password;
    public String Email;
    public Integer AvatarId;

    public User(Integer id, String email, String username, String password, Integer avatarId) {
        Id= id;
        Email = email;
        Username = username;
        Password = password;
        AvatarId = avatarId;
    }

    public User() {
    }

    public String getPassword() {
        return Password;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmail() {
        return Email;
    }

    public Integer getAvatarId() {
        return AvatarId;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
