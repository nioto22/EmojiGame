package com.example.nioto.emojigame.api;

import com.example.nioto.emojigame.models.Enigma;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EnigmaHelper {

    private static final String TAG = "EnigmaHelper";

    private static final String COLLECTION_NAME = "users";

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

    // --- ADD ---

    public static Task<DocumentReference> addResolvedUserUid (String userUid, String uid){
        return EnigmaHelper.getEnigmaCollection()
                .document(uid)
                .collection("resolvedUserUid")
                .add(userUid);
    }

    // --- DELETE ---

    public static Task<Void> deleteEnigma(String uid) {
        return EnigmaHelper.getEnigmaCollection().document(uid).delete();
    }

}
