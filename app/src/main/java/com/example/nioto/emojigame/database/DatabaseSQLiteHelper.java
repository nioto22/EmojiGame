package com.example.nioto.emojigame.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseSQLiteHelper sqliteHelperInstance;

    public static synchronized DatabaseSQLiteHelper getInstance(Context context) {
        if (sqliteHelperInstance == null) { sqliteHelperInstance = new DatabaseSQLiteHelper(context); }
        return sqliteHelperInstance;
    }

    private DatabaseSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(EnigmaPlayedManager.CREATE_TABLE_ENIGMA_PLAYED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // Mise à jour de la base de données
        // méthode appelée sur incrémentation de DATABASE_VERSION
        // on peut faire ce qu'on veut ici, comme recréer la base :
        // onCreate(sqLiteDatabase);
    }

}
