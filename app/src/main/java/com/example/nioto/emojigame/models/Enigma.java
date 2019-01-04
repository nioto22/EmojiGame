package com.example.nioto.emojigame.models;

import android.support.annotation.Nullable;

import com.example.nioto.emojigame.api.UserHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Enigma {

    private String uid;
    private String userUid;
    private Date dateCreated;
    private String enigma;
    private String solution;
    private String category;
    @Nullable private String message;
    private List<String> resolvedUserUid;
    private int numbersOfResolvedUserUid;
    private String difficultyFormarted;

    public Enigma() {}

    public Enigma(String uid, String userUid, String enigma, String solution, String category) {
        this (uid, userUid, enigma, solution, category, null);
    }

    public Enigma(String uid, String userUid, String enigma, String solution, String category, String message) {
        this.uid = uid;
        this.userUid = userUid;
        this.enigma = enigma;
        this.solution = solution;
        this.category = category;
        this.resolvedUserUid = new ArrayList<>();
        this.message = message;
    }

    //  ----  GETTERS   ----

    public String getUid() { return uid; }
    public String getUserUid() { return userUid;}
    @ServerTimestamp public Date getDateCreated() { return dateCreated; }
    public String getEnigma() { return enigma; }
    public String getSolution() { return solution; }
    public String getCategory() { return category;}
    @Nullable public String getMessage() { return message;}
    public List<String> getResolvedUserUid() { return resolvedUserUid; }
    public int getNumbersOfResolvedUserUid() { return this.resolvedUserUid.size(); }
    public String getDifficultyFormarted() {
        return ( "" + getNumbersOfResolvedUserUid() + " fois");
    }


    //   ---- SETTERS   ----

    public void setUid(String uid) { this.uid = uid; }
    public void setUserUid(String userUid) { this.userUid = userUid; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setEnigma(String enigma) { this.enigma = enigma;  }
    public void setSolution(String solution) {  this.solution = solution; }
    public void setCategory(String category) { this.category = category;  }
    public void setMessage(@Nullable String message) { this.message = message; }
    public void setResolvedUserUid(List<String> resolvedUserUid) { this.resolvedUserUid = resolvedUserUid;}
    public void addResolvedUserUid (String userUid) { this.resolvedUserUid.add(userUid); }
    public void setNumbersOfResolvedUserUid(int numbersOfResolvedUserUid) {
        this.numbersOfResolvedUserUid = numbersOfResolvedUserUid;
    }
    public void setDifficultyFormarted(String difficultyFormarted) {
        this.difficultyFormarted = difficultyFormarted;
    }
}
