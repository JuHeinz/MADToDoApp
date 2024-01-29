package org.julheinz.entities;

import com.google.gson.annotations.SerializedName;


public class UserEntity {

    public UserEntity(String password, String email) {
        this.email = email;
        this.password = password;
    }

    @SerializedName("pwd")
    private String password;

    private String email;
}
