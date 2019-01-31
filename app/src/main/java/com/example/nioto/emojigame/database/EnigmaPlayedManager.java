package com.example.nioto.emojigame.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EnigmaPlayedManager {

    // Db NAME
    public static final String TABLE_NAME = "enigma_played";
    // COLUMNS NAMES
    public static final String ENIGMA_ID = "enigma_id";
    public static final String ENIGMA_UID = "enigma_uid";
    public static final String ENIGMA_IS_SOLVED = "enigma_is_solved";
    public static final String ENIGMA_HAS_HINT_ONE = "enigma_has_hint_one";
    public static final String ENIGMA_HAS_HINT_TWO = "enigma_has_hint_two";
    public static final String ENIGMA_HINT_TWO_POSITIONS = "enigma_hint_two_positions";

    // QUERY TABLE CREATION
    public static final String CREATE_TABLE_ENIGMA_PLAYED =
            "CREATE TABLE "+TABLE_NAME+
                    " (" +
                    " "+ENIGMA_ID+" INTEGER primary key," +
                    " "+ENIGMA_UID+" TEXT," +
                    " "+ENIGMA_IS_SOLVED+" INTEGER," +
                    " "+ENIGMA_HAS_HINT_ONE+" INTEGER," +
                    " "+ENIGMA_HAS_HINT_TWO+" INTEGER," +
                    " "+ENIGMA_HINT_TWO_POSITIONS+" TEXT" +
                    ");";

    private DatabaseSQLiteHelper SQLiteHelper;
    private SQLiteDatabase db;

    // Constructeur
    public EnigmaPlayedManager(Context context) {
        SQLiteHelper = DatabaseSQLiteHelper.getInstance(context);
    }

    public void open() {
        db = SQLiteHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public long addEnigmaPlayed(EnigmaPlayed enigmaPlayed) {
        // Ajout d'un enregistrement dans la table

        ContentValues values = new ContentValues();
        values.put(ENIGMA_UID, enigmaPlayed.getEnigmaUid());
        values.put(ENIGMA_IS_SOLVED, enigmaPlayed.getEnigmaIsSolved());
        values.put(ENIGMA_HAS_HINT_ONE, enigmaPlayed.getEnigmaHasHintOne());
        values.put(ENIGMA_HAS_HINT_TWO, enigmaPlayed.getEnigmaHasHintTwo());
        values.put(ENIGMA_HINT_TWO_POSITIONS, enigmaPlayed.getHintTwoPositions());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(TABLE_NAME,null,values);
    }

    public int convertBooleanToInt(Boolean input){
        return input ? 1 : -1;
    }

    public int updateEnigmaPlayed (EnigmaPlayed enigmaPlayed){
        ContentValues values = new ContentValues();
        values.put(ENIGMA_IS_SOLVED, enigmaPlayed.getEnigmaIsSolved());
        values.put(ENIGMA_HAS_HINT_ONE, enigmaPlayed.getEnigmaHasHintOne());
        values.put(ENIGMA_HAS_HINT_TWO, enigmaPlayed.getEnigmaHasHintTwo());
        values.put(ENIGMA_HINT_TWO_POSITIONS, enigmaPlayed.getHintTwoPositions());
        String where = ENIGMA_UID+" = ?";
        String[] whereArgs = {enigmaPlayed.getEnigmaUid()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }


    public int updateEnigmaIsSolved(String enigmaUid, Boolean isSolved) {
        int result = convertBooleanToInt(isSolved);
        ContentValues values = new ContentValues();
        values.put(ENIGMA_IS_SOLVED, result);
        String where = ENIGMA_UID+" = ?";
        String[] whereArgs = {enigmaUid+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int updateEnigmaHasHintOne(String enigmaUid, Boolean hasHintOne) {
        int result = convertBooleanToInt(hasHintOne);
        ContentValues values = new ContentValues();
        values.put(ENIGMA_HAS_HINT_ONE, result);
        String where = ENIGMA_UID+" = ?";
        String[] whereArgs = {enigmaUid+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int updateEnigmaHasHintTwo(String enigmaUid, Boolean hasHintTwo) {
        int result = convertBooleanToInt(hasHintTwo);
        ContentValues values = new ContentValues();
        values.put(ENIGMA_HAS_HINT_TWO, result);
        String where = ENIGMA_UID+" = ?";
        String[] whereArgs = {enigmaUid+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int updateEnigmaHintTwoPositions (String enigmaUid, String hintTwoPosition){
        ContentValues values = new ContentValues();
        values.put(ENIGMA_HINT_TWO_POSITIONS, hintTwoPosition);
        String where = ENIGMA_UID+" = ?";
        String[] whereArgs = {enigmaUid+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public EnigmaPlayed getEnigmaPlayed(String enigmaUid) {

        EnigmaPlayed enigma= new EnigmaPlayed("");

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+ENIGMA_UID+"="+enigmaUid, null);
        if (c.moveToFirst()) {
            enigma.setEnigmaUid(c.getString(c.getColumnIndex(ENIGMA_UID)));
            enigma.setEnigmaIsSolved(c.getInt(c.getColumnIndex(ENIGMA_IS_SOLVED)));
            enigma.setEnigmaHasHintOne(c.getInt(c.getColumnIndex(ENIGMA_HAS_HINT_ONE)));
            enigma.setEnigmaHasHintTwo(c.getInt(c.getColumnIndex(ENIGMA_HAS_HINT_TWO)));
            enigma.setHintTwoPositions(c.getString(c.getColumnIndex(ENIGMA_HINT_TWO_POSITIONS)));
            c.close();
        }

        return enigma;
    }



    public int deleteEnigmaPlayed(EnigmaPlayed enigmaPlayed) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = ENIGMA_UID+" = ?";
        String[] whereArgs = {enigmaPlayed.getEnigmaUid()+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public Boolean isEnigmaExists(String enigmaUid) {
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+ENIGMA_UID+"="+enigmaUid, null);
        return c.moveToFirst();
    }


    public Cursor getAllEnigmasPlayed() {
        return db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
    }


}
