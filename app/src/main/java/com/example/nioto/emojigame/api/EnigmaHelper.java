package com.example.nioto.emojigame.api;

import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;
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

    public static Query getAEnigma(String sortType){
        Query query;
        switch (sortType){
            case Constants.SORT_DIFICULTY_ASC :
                query = EnigmaHelper.getEnigmaCollection().orderBy("dificulty", Query.Direction.ASCENDING);
                break;
            case Constants.SORT_DIFICULTY_DESC :
                query = EnigmaHelper.getEnigmaCollection().orderBy("dificulty", Query.Direction.DESCENDING);
                break;
            case Constants.SORT_DATE_ASC :
                query = EnigmaHelper.getEnigmaCollection().orderBy("dateCreated", Query.Direction.ASCENDING);
                break;
            case Constants.SORT_DATE_DESC :
                query = EnigmaHelper.getEnigmaCollection().orderBy("dateCreated", Query.Direction.DESCENDING);
                break;
            case Constants.SORT_PLAYER_ASC :
                query = EnigmaHelper.getEnigmaCollection().orderBy("userUid", Query.Direction.ASCENDING);
                break;
            case Constants.SORT_PlAYER_DESC :
                query = EnigmaHelper.getEnigmaCollection().orderBy("userUid", Query.Direction.DESCENDING);
                break;
            default:
                query = EnigmaHelper.getEnigmaCollection().orderBy("dificulty", Query.Direction.ASCENDING);
                break;
        }
        return query;
    }

    private static Query getSortEnigma(Query queryFilteringEnigma, String sortType) {
        switch (sortType){
            case Constants.SORT_DIFICULTY_ASC :
                return queryFilteringEnigma.orderBy("dificulty", Query.Direction.ASCENDING);
            case Constants.SORT_DIFICULTY_DESC :
                return queryFilteringEnigma.orderBy("dificulty", Query.Direction.DESCENDING);
            case Constants.SORT_DATE_ASC :
                return queryFilteringEnigma.orderBy("dateCreated", Query.Direction.ASCENDING);
            case Constants.SORT_DATE_DESC :
                return queryFilteringEnigma.orderBy("dateCreated", Query.Direction.DESCENDING);
            case Constants.SORT_PLAYER_ASC :
                return queryFilteringEnigma.orderBy("userUid", Query.Direction.ASCENDING);
            case Constants.SORT_PlAYER_DESC :
                return queryFilteringEnigma.orderBy("userUid", Query.Direction.DESCENDING);
            default:
                return queryFilteringEnigma.orderBy("dificulty", Query.Direction.ASCENDING);
        }
    }

    public static Query getAllEnigma(String filterWay){
        Query query;
        switch (filterWay){
            case Constants.FILTER_CATEGORY_ALL :
                query =  EnigmaHelper.getEnigmaCollection();
                return query;
            case Constants.FILTER_CATEGORY_PERSONAGE :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category", Constants.FIREBASE_CATEGORY_PERSONAGE_TEXT);
                return query;
            case Constants.FILTER_CATEGORY_CINEMA :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category",  Constants.FIREBASE_CATEGORY_CINEMA_TEXT);
                return query;
            case Constants.FILTER_CATEGORY_MUSIC :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category",  Constants.FIREBASE_CATEGORY_MUSIC_TEXT);
                return query;
            case Constants.FILTER_CATEGORY_EXPRESSION :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category",  Constants.FIREBASE_CATEGORY_EXPRESSION_TEXT);
                return query;
            case Constants.FILTER_CATEGORY_OBJECT :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category",  Constants.FIREBASE_CATEGORY_OBJECT_TEXT);
                return query;
            case Constants.FILTER_CATEGORY_WORD :
                query =  EnigmaHelper.getEnigmaCollection()
                        .whereEqualTo("category",  Constants.FIREBASE_CATEGORY_WORD_TEXT);
                return query;
            case Constants.FILTER_CATEGORY_OTHER :
                query =  EnigmaHelper.getEnigmaCollection()
                        .orderBy("category")
                        .endBefore("Cinéma");
                return query;
            default:
                query =  EnigmaHelper.getEnigmaCollection()
                        .orderBy("category", Query.Direction.ASCENDING);
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
