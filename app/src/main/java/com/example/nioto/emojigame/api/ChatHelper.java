package com.example.nioto.emojigame.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChatHelper {

    public static final String COLLECTION_NAME = "chats";

    // ----   COLECTION REFERENCE   ---

    public static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getChat(String uid){
        return EnigmaHelper.getEnigmaCollection().document(uid).get();
    }

}
