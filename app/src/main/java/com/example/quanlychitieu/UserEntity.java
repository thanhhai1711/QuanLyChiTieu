package com.example.quanlychitieu;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey
    @NonNull
    public String username;

    public String password;

    public UserEntity(@NonNull String username, String password) {
        this.username = username;
        this.password = password;
    }
}
