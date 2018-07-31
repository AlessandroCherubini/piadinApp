package com.example.ale.piadinapp.classi;

public class User {
    public long userId;
    public String nickname;
    public String email;
    public String password;
    public String phone;

    public User(long userId, String username, String password, String email){
        this.userId = userId;
        this.nickname = username;
        this.password = password;
        this.email = email;
    }

    public String toString(){
        return userId + " " + nickname + " " + password + " " + email;
    }
}