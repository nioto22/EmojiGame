package com.example.nioto.emojigame.models;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Enigma {
    private static final String TAG = "Enigma";

    private String uid;
    private String userUid;
    private Date dateCreated;
    private String enigma;
    private String solution;
    private String category;
    @Nullable private String message;
    private List<String> resolvedUserUid;
    private int numbersOfResolvedUserUid;
    private int dificulty;

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
        setDificulty();
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
    public int getDificulty() { return (this.dificulty);}


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
    public void setDificulty() {
        if (this.message != null) {
            String[] messageSplit = this.message.split("_");
            int numberOfSplit = messageSplit.length;
            //int enigmaLenght = this.enigma.length();
            //int additionalPoints = (numberOfSplit > 3 ) ? (enigmaLenght / numberOfSplit < 2) ? 0 : (enigmaLenght / numberOfSplit < 4) ? enigmaLenght/numberOfSplit*25 : 50 : 0;
            this.dificulty = (numberOfSplit * 25)  ;  // + additionalPoints
        } else {
            this.dificulty = 25;
        }
    }

    public static class DifficultyComparatorAsc implements Comparator<Enigma> {
        @Override
        public int compare(Enigma e1, Enigma e2) {
            return Integer.compare(e1.dificulty, e2.dificulty);
        }
    }

    public static class DifficultyComparatorDesc implements Comparator<Enigma> {
        @Override
        public int compare(Enigma e1, Enigma e2) {
            return Integer.compare(e2.dificulty, e1.dificulty);
        }
    }

    public static class DateComparatorAsc implements Comparator<Enigma> {
        @Override
        public int compare(Enigma e1, Enigma e2) {
            return e1.dateCreated.compareTo(e2.dateCreated);
        }
    }

    public static class DateComparatorDesc implements Comparator<Enigma> {
        @Override
        public int compare(Enigma e1, Enigma e2) {
            return e2.dateCreated.compareTo(e1.dateCreated);
        }
    }

    public static class PlayerComparatorAsc implements Comparator<Enigma> {
        @Override
        public int compare(Enigma e1, Enigma e2) {
            return e1.userUid.compareTo(e2.userUid);
        }
    }

    public static class PlayerComparatorDesc implements Comparator<Enigma> {
        @Override
        public int compare(Enigma e1, Enigma e2) {

            return e2.userUid.compareTo(e1.userUid);
        }
    }

}
