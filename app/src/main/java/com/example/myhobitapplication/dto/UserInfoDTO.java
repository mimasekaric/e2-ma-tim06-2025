package com.example.myhobitapplication.dto;

import java.util.Date;

public class UserInfoDTO {

    public String username;

    public String avatarName;

    public UserInfoDTO(String username,String avatarName) {
        this.username = username;
        this.avatarName = avatarName;
    }

    public UserInfoDTO() {
    }

    public String getusername() {
        return username;
    }


    public String getavatarName() {
        return avatarName;
    }


}

