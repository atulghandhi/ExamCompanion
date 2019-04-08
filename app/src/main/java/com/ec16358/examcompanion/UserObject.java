package com.ec16358.examcompanion;

public class UserObject {

    private String userId;
    private String username;

    //constructor
    public UserObject(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    //default constructor, no arguments
    public UserObject() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
