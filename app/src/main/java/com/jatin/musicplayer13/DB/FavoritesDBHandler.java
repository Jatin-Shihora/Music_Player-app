package com.jatin.musicplayer13.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritesDBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    //creating the fields for the database
    public static final String TABLE_SONGS           = "favorites";
    public static final String COLUMN_ID             = "songID";
    public static final String COLUMN_TITLE          = "title";
    public static final String COLUMN_SUBTITLE       = "subtitle";
    public static final String COLUMN_PATH           = "songpath";

    //giving the type of inputs to the fields
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_SONGS + " (" + COLUMN_ID
            + " INTEGER, " + COLUMN_TITLE + " TEXT, " + COLUMN_SUBTITLE
            + " TEXT, " + COLUMN_PATH + " TEXT PRIMARY KEY " + ")";

    public FavoritesDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);//creates a raw query of table

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);//on upgrade(addition or deletion from favourites tabs) drop the previous table and start a new one
        db.execSQL(TABLE_CREATE);
    }
}