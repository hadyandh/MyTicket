package com.hunhun.myticket.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String name, username, password, email, bio, url_photo;
    public int user_balance;

    public User() {
    }

    public User(String name, String username, String password, String email, String bio, String url_photo, int user_balance) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.bio = bio;
        this.url_photo = url_photo;
        this.user_balance = user_balance;
    }

    public User(String name, String username, String password, String email, String bio, String url_photo) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.bio = bio;
        this.url_photo = url_photo;
    }

    @Exclude
    public Map<String, Object> toMapUpdate() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("bio", bio);
        result.put("username", username);
        result.put("password", password);
        result.put("email", email);
        result.put("url_photo", url_photo);

        return result;
    }
}
