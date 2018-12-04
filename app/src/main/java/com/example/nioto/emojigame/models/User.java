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
    private Boolean hasChangedPicture;

    public User(){}

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.points = 0;
        userEnigmaUidList = new ArrayList<>();
        userResolvedEnigmaUidList = new ArrayList<>();
        hasChangedPicture = false;
    }

    // ----- GETTERS -----

    public String getUid() { return uid;}
    public String getUsername() { return username;}
    public int getPoints() { return points;}
    @Nullable public String getUrlPicture() { return urlPicture;}
    public List<String> getUserEnigmaUidList() { return userEnigmaUidList;}
    public List<String> getUserResolvedEnigmaUidList() { return userResolvedEnigmaUidList;}
    public Boolean getHasChangedPicture() { return hasChangedPicture; }


// ---- SETTERS ----

    public void setUid(String uid) { this.uid = uid;}
    public void setUsername(String username) { this.username = username;}

    public void setPoints(int points) { this.points = points;}
    public void addPoints (int addingPoints) {
        this.setPoints(this.getPoints() + addingPoints);
    }

    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }

    public void setUserEnigmaUidList(List<String> userEnigmaUidList) { this.userEnigmaUidList = userEnigmaUidList; }
    public void addUserEnigmaUidList (String addingUid) {
        this.getUserEnigmaUidList().add(addingUid);
    }
    public void setUserResolvedEnigmaUidList(List<String> userResolvedEnigmaUidList) { this.userResolvedEnigmaUidList = userResolvedEnigmaUidList; }
    public void addUserResolvedEnigmaUidList (String addingResolvedUid) {
        this.getUserEnigmaUidList().add(addingResolvedUid);
    }

    public void setHasChangedPicture(Boolean hasChangedPicture) { this.hasChangedPicture = hasChangedPicture; }
}
