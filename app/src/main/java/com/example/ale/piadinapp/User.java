package com.example.ale.piadinapp;

public class User {
    public long userId;
    public String nickname;
    public String password;

    public User(long userId, String username, String password){
        this.userId = userId;
        this.nickname = username;
        this.password = password;
    }

}