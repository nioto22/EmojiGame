package com.example.nioto.emojigame.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private String message;
    private Date dateCreated;
    private User userSender;
    private String enigmaUid;

    public Message() { }

    public Message(String message, User userSender, String enigmaUid) {
        this.message = message;
        this.userSender = userSender;
        this.enigmaUid = enigmaUid;
    }


    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public String getEnigmaUid() { return enigmaUid; }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
    public void setEnigmaChat(String enigmaUid) { this.enigmaUid = enigmaUid; }
}

