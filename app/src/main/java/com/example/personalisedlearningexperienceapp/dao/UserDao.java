package com.example.personalisedlearningexperienceapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.personalisedlearningexperienceapp.model.User;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User loginByUsername(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findByUsername(String username);

    @Query("UPDATE users SET interests = :interests WHERE username = :username")
    void updateInterests(String username, String interests);
}





