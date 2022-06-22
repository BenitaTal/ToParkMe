package com.example.toparkme.ToParkMe.classes;

import android.util.Log;

import com.google.gson.Gson;

public class User {

    private String email;
    private String password;

    public User() {
        setEmail("Email");
        setPassword("Password");
    }

    public void setUser(User user){
        this.email = user.getEmail();
        this.password = user.getPassword();
    }


    public User(String eMail, String password) {
        setEmail(eMail);
        setPassword(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}
