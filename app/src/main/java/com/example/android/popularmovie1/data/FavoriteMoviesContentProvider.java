package com.example.android.popularmovie1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class FavoriteMoviesContentProvider extends ContentProvider {

    public static final int FAVORITE_MOVIES = 100;
    public static final int FAVORITE_MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoritesContract.AUTHORITY,FavoritesContract.PATH_FAVORITES,FAVORITE_MOVIES);
        uriMatcher.addURI(FavoritesContract.AUTHORITY,FavoritesContract.PATH_FAVORITES + "/#",FAVORITE_MOVIES_WITH_ID);

        return uriMatcher;
    }


    private FavoritesDBHelper mDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mDbHelper = new FavoritesDBHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnURI;

        switch (match){
            case FAVORITE_MOVIES:
                long id = db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME,null,values);
                if (id>0){
                    returnURI = ContentUris.withAppendedId(FavoritesContract.FavoritesEntry.CONTENT_URI,id);
                }else{
                    throw new android.database.SQLException("failed to insert row: " + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnURI;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor  retCursor =null;

        switch (match){
            case FAVORITE_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "movieID=?";
                String[] mSelectionArgs = new String[]{id};
                retCursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME, projection,mSelection,mSelectionArgs,null,null,sortOrder);

                break;
            case FAVORITE_MOVIES:
                retCursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME, projection,selection,selectionArgs,null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return retCursor;

        //throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        int moviesDeleted;

        switch (match) {
            case FAVORITE_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(FavoritesContract.FavoritesEntry.TABLE_NAME, "movieID=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


}
