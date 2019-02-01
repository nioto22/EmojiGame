package com.example.nioto.emojigame.api;

import android.support.annotation.Nullable;

import com.example.nioto.emojigame.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserHelper {
    private static final String TAG = "UserHelper";

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, @Nullable String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Query getAllUser(){
        return EnigmaHelper.getEnigmaCollection()
                .limit(50);
    }



    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateUserPhoto(String urlPicture, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("urlPicture", urlPicture);
    }

    public static Task<Void> updateUserHasChangedPicture(Boolean hasChangedPicture, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("hasChangedPicture", hasChangedPicture);
    }

    public static Task<Void> updateUserEnigmaUidList(List<String> userEnigmaUidList, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("userEnigmaUidList", userEnigmaUidList);
    }

    public static Task<Void> updateUserResolvedEnigmaUidList(List<String>  userResolvedEnigmaUidList, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("userResolvedEnigmaUidList", userResolvedEnigmaUidList);
    }

    public static Task<Void> updateUserMessageList(ArrayList<String> userMessageList, String uid){
        return UserHelper.getUsersCollection().document(uid).update("userMessageList", userMessageList);
    }

    public static Task<Void> updateUserPoints(int points, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("points", points);
    }

    public static Task<Void> updateUserSmileys (int smileys, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("smileys", smileys);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}

