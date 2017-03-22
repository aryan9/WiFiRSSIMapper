package com.example.a0132535.wifirssimapper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by a0132535 on 3/15/2017.
 */


public class RSSIDistanceDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RSSIDistanceContract.FeedEntry.TABLE_NAME + " (" +
                    RSSIDistanceContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    RSSIDistanceContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " TEXT," +
                    RSSIDistanceContract.FeedEntry.COLUMN_NAME_RSSI + " TEXT," +
                    RSSIDistanceContract.FeedEntry.COLUMN_NAME_DISTANCE + " TEXT)" ;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RSSIDistanceContract.FeedEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RssiDistance.db";

    public RSSIDistanceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
