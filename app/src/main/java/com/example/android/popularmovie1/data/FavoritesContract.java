package com.example.android.popularmovie1.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.security.PublicKey;

public class FavoritesContract {

    public static final String AUTHORITY = "com.example.android.popularmovie1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    static public final class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();


    }
}

