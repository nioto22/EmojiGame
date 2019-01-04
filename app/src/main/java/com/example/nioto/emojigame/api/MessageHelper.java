package com.example.nioto.emojigame.api;

import android.util.Log;

import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.Message;
import com.example.nioto.emojigame.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class MessageHelper {
    private static final String TAG = "MessageHelper";
    public static final String COLLECTION_NAME = "message";

    public static Task<DocumentReference> createMessageForChat (String textMessage, String enigmaUid, User userSender) {
        // Create Message object
        Message messaqe = new Message(textMessage, userSender, enigmaUid);

        // Store it to Firestore
        return ChatHelper.getChatCollection()
                .document(enigmaUid)
                .collection(COLLECTION_NAME)
                .add(messaqe);
    }



    // ----   GET   ----

    public static Query getAllMessageForChat(String enigmaUid){
        return ChatHelper.getChatCollection()
                .document(enigmaUid)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }




}
