package com.example.nioto.emojigame.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String uid;
    private String username;
    private int points;
    @Nullable private String urlPicture;
    private List<String> userEnigmaUidList;
    private List<String> userResolvedEnigmaUidList;

    public User(){}

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.points = 0;
        userEnigmaUidList = new ArrayList<>();
        userResolvedEnigmaUidList = new ArrayList<>();
    }

    // ----- GETTERS -----

    public String getUid() { return uid;}
    public String getUsername() { return username;}
    public int getPoints() { return points;}
    @Nullable public String getUrlPicture() { return urlPicture;}
    public List<String> getUserEnigmeUidList() { return userEnigmaUidList;}
    public List<String> getUserResolveEnigmaUidList() { return userResolvedEnigmaUidList;}


    // ---- SETTERS ----

    public void setUid(String uid) { this.uid = uid;}
    public void setUsername(String username) { this.username = username;}

    public void setPoints(int points) { this.points = points;}
    public void addPoints (int addingPoints) {
        this.setPoints(this.getPoints() + addingPoints);
    }

    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }

    public void setUserEnigmaUidList(List<String> userEnigmeUidList) { this.userEnigmaUidList = userEnigmeUidList; }
    public void addUserEnigmaUidList (String addingUid) {
        this.getUserEnigmeUidList().add(addingUid);
    }
    public void setUserResolvedEnigmaUidList(List<String> userResolveEnigmeUidList) { this.userResolvedEnigmaUidList = userResolveEnigmeUidList; }
    public void addUserResolvedEnigmaUidList (String addingResolvedUid) {
        this.getUserEnigmeUidList().add(addingResolvedUid);
    }
}