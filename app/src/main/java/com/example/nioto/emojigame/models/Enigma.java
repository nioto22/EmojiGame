package com.example.nioto.emojigame.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Enigma {

   private String uid;
   private String userUid;
   private String enigma;
   private String solution;
   private String category;
   @Nullable private String message;
   private List<String> resolvedUserUid;

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
    public String getEnigma() { return enigma; }
    public String getSolution() { return solution; }
    public String getCategory() { return category;}
    @Nullable public String getMessage() { return message;}
    public List<String> getResolvedUserUid() { return resolvedUserUid; }

    //   ---- SETTERS   ----

    public void setUid(String uid) { this.uid = uid; }
    public void setUserUid(String userUid) { this.userUid = userUid; }
    public void setEnigma(String enigma) { this.enigma = enigma;  }
    public void setSolution(String solution) {  this.solution = solution; }
    public void setCategory(String category) { this.category = category;  }
    public void setMessage(@Nullable String message) { this.message = message; }
    public void setResolvedUserUid(List<String> resolvedUserUid) { this.resolvedUserUid = resolvedUserUid;}
    public void addResolvedUserUid (String userUid) { this.resolvedUserUid.add(userUid); }
}
