package com.example.nioto.emojigame.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {

    private String uid;
    private String username;
    private int points;
    private int smileys;
    @Nullable private String urlPicture;
    private List<String> userEnigmaUidList;
    private List<String> userResolvedEnigmaUidList;
    private ArrayList<String> userMessageList;
    private Boolean hasChangedPicture;

    public User(){}

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.points = 0;
        this.smileys = 5;
        this.userEnigmaUidList = new ArrayList<>();
        this.userResolvedEnigmaUidList = new ArrayList<>();
        this.userMessageList = new ArrayList<>();
        this.hasChangedPicture = false;
    }

    // ----- GETTERS -----

    public String getUid() { return this.uid;}
    public String getUsername() { return this.username;}
    public int getPoints() { return this.points;}
    public int getSmileys() { return this.smileys;}
    @Nullable public String getUrlPicture() { return this.urlPicture;}
    public List<String> getUserEnigmaUidList() { return this.userEnigmaUidList;}
    public List<String> getUserResolvedEnigmaUidList() { return this.userResolvedEnigmaUidList;}
    public ArrayList<String> getUserMessageList() { return this.userMessageList;}
    public Boolean getHasChangedPicture() { return this.hasChangedPicture; }


// ---- SETTERS ----

    public void setUid(String uid) { this.uid = uid;}
    public void setUsername(String username) { this.username = username;}

    private void setSmileys(int smileys) { this.smileys = smileys;}
    public void addSmileys (int addingSmileys) { this.setSmileys(this.getSmileys() + addingSmileys);}
    public void decreaseSmileys (int decreaseSmileys) { this.setSmileys((this.getSmileys() - decreaseSmileys <= 0)? 0 : this.getSmileys() - decreaseSmileys) ;}


    private void setPoints(int points) { this.points = points;}
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
