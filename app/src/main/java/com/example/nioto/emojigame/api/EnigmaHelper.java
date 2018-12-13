package com.example.nioto.emojigame.api;

import android.util.Log;

import com.example.nioto.emojigame.models.Enigma;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class EnigmaHelper {

    private static final String TAG = "EnigmaHelper";

    private static final String COLLECTION_NAME = "enigmas";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getEnigmaCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createEnigma(String uid, String userUid, String enigma, String solution, String category, String message) {
        Enigma enigmaToCreate = new Enigma(uid, userUid, enigma, solution, category, message);
        return EnigmaHelper.getEnigmaCollection().document(uid).set(enigmaToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getEnigma(String uid){
        return EnigmaHelper.getEnigmaCollection().document(uid).get();
    }

    // ----   GET   ----

    public static Query getAllEnigma(String filterWay){
        Query query;
        switch (filterWay){
            case "byDateDesc" :
                query =  EnigmaHelper.getEnigmaCollection()
                        .orderBy("dateCreated", Query.Direction.DESCENDING)
                        .limit(50);
                return query;
            case "byCategoryPersonage" :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category", "Personnage" )
                        .limit(50);
                return query;
            case "byCategoryCinema" :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category", "Cinéma");
                return query;
            case "byCategoryMusic" :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category", "Musique");
                return query;
            case "byCategoryExpressions" :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category", "Expressions");
                return query;
            case "byCategoryObject" :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category", "Objet");
                return query;
            case "byCategoryOther" :
                query =  EnigmaHelper.getEnigmaCollection()
                        .orderBy("category")
                        .endBefore("Cinéma")
                        .limit(50);
                return query;
            default:
                query =  EnigmaHelper.getEnigmaCollection()
                        .orderBy("dateCreated", Query.Direction.DESCENDING)
                        .limit(50);
                return query;
        }
    }


    // --- UPDATE ---

    public static Task<Void> updateEnigma(String enigma, String uid) {
        return EnigmaHelper.getEnigmaCollection().document(uid).update("enigma", enigma);
    }

    public static Task<Void> updateSolution(String solution, String uid) {
        return EnigmaHelper.getEnigmaCollection().document(uid).update("solution", solution);
    }

    public static Task<Void> updateCategory(String category, String uid) {
        return EnigmaHelper.getEnigmaCollection().document(uid).update("category", category);
    }

    public static Task<Void> updateMessage(String message, String uid) {
        return EnigmaHelper.getEnigmaCollection().document(uid).update("message", message);
    }

    // --- ADD ---

    public static Task<Void> updateResolvedUserUidList(List<String> resolvedUserUidList, String uid) {
        return EnigmaHelper.getEnigmaCollection().document(uid).update("resolvedUserUid", resolvedUserUidList);
    }

    // --- DELETE ---

    public static Task<Void> deleteEnigma(String uid) {
        return EnigmaHelper.getEnigmaCollection().document(uid).delete();
    }

}
