package com.example.android.classi;

public class User {
    public long userId;
    public String nickname;
    public String email;
    public String password;
    public String phone;

    public User(long userId, String username, String password, String email, String phone){
        this.userId = userId;
        this.nickname = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public String toString(){

        return userId + " " + nickname + " " + password + " " + email + " " + phone;
    }
}