package org.julheinz.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the entered email and password that should be authenticated.
 */
public class UserEntity {

    public UserEntity(String password, String email) {
        this.email = email;
        this.password = password;
    }

    @SerializedName("pwd")
    private String password;

    private String email;
}
