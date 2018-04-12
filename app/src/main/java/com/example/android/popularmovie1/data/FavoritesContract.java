package com.example.android.popularmovie1.data;

import android.provider.BaseColumns;

public class FavoritesContract {


    static public final class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movieID";


    }
}
