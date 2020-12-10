package com.example.meusamigos2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MeusAmigos.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE Amigos (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "Nome TEXT NOT NULL, " +
            "Celular TEXT NOT NULL, " +
            "Status INTEGER, " +
            "Sincronizado INTEGER," +
            "Foto BLOB);";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
