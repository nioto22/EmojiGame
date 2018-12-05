package com.example.nioto.emojigame.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatHelper {

    public static final String COLLECTION_NAME = "chats";

    // ----   COLECTION REFERENCE   ---

    public static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

}
