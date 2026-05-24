package com.example.quanlychitieu;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert
    void insert(UserEntity user);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int isUserExists(String username);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username AND password = :password")
    int checkUser(String username, String password);
}
