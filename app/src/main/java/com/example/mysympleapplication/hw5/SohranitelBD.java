package com.example.mysympleapplication.hw5;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SohranitelBD extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "graficDb";
    static final String TABLE_VALUES = "anyValues";

    static final String KEY_ID = "_id";
    static final String KEY_VALUEX = "valueX";
    static final String KEY_VALUEY = "noteY";


    public SohranitelBD(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_VALUES + "(" + KEY_ID
                + " integer primary key,"
                + KEY_VALUEY + " float," + KEY_VALUEX + " float" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_VALUES);
        onCreate(db);
    }
}
