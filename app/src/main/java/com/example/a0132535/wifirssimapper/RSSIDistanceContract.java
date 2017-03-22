package com.example.a0132535.wifirssimapper;

import android.provider.BaseColumns;

/**
 * Created by a0132535 on 3/15/2017.
 */

public final class RSSIDistanceContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.    private FeedReaderContract() {}
    private RSSIDistanceContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "RSSIvDistance";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_RSSI = "rssi";
        public static final String COLUMN_NAME_DISTANCE = "distance";

    }
}
