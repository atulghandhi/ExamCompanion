package com.ec16358.examcompanion;

/*
*
* Used by Home.java to create a user object for each new user and upload to database. Data here is
* then used by Leaderboard.java to populate its listview of users of the application.
*
* */

public class UserObject {

    private String userId;
    private String username;
    private int points;
    private String photoURL;

    //constructor
    public UserObject(String userId, String username, int points, String photoURL) {
        this.userId = userId;
        this.username = username;
        this.points = points;
        this.photoURL = photoURL;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

}
