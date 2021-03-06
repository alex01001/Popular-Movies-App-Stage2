package com.example.android.popularmovie1;


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkTools {

    /**
     * Builds the URL
     */
    private static final String API_KEY = BuildConfig.API_KEY;
    public static URL buildUrl(int sortPopular) {

        if (sortPopular==2){
            return null;
        }

        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendPath(sortPopular==0 ? Constants.POPULAR_MOVIES_URL:Constants.TOP_MOVIES_URL)
                .appendQueryParameter(Constants.API_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
// builds full poster URL
    public static URL buildPosterUrl(String posterPath) {
        Uri builtUri = Uri.parse(Constants.IMAGE_URL).buildUpon()
                .appendEncodedPath(posterPath)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // builds URL for individual movie
    public static URL buildMovieUrl(String movieID) {
        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendEncodedPath(movieID)
                .appendQueryParameter(Constants.API_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // builds full trailers request URL
    public static URL buildTrailersUrl(String movieID) {
        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendEncodedPath(movieID)
                .appendEncodedPath("videos")
                .appendQueryParameter(Constants.API_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // builds full trailers request URL
    public static URL buildReviewsUrl(String movieID) {
        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendEncodedPath(movieID)
                .appendEncodedPath("reviews")
                .appendQueryParameter(Constants.API_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
