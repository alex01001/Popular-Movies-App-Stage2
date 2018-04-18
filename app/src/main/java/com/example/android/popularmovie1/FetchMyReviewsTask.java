package com.example.android.popularmovie1;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovie1.data.AsyncTaskCompleteListenerT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class FetchMyReviewsTask extends AsyncTask<URL, Integer, String> {
    private Context context;
    private AsyncTaskCompleteListenerT<ArrayList<Review>> listener;

    public FetchMyReviewsTask(Context ctx, AsyncTaskCompleteListenerT<ArrayList<Review>> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL searchUrl = urls[0];
        String searchResults = null;
        try {
            searchResults = NetworkTools.getResponseFromHttpUrl(searchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s == null) {
            return;
        }
        // parsing the response.
        final String OMD_Results = "results";
        final String OMD_ID = "id";
        final String OMD_Author = "author";
        final String OMD_Content = "content";
        final String OMD_URL = "url";

        JSONObject reviewsJson;
        try {
            reviewsJson = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JSONArray reviewsArray;
        try {
            reviewsArray = reviewsJson.getJSONArray(OMD_Results);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        ArrayList<Review> resultStr = new ArrayList<Review>();

        for (int i = 0; i < reviewsArray.length(); i++) {
            String id;
            String author;
            String content;
            String url;
            try {
                JSONObject reviews = reviewsArray.getJSONObject(i);

                author = reviews.getString(OMD_Author);
                id = reviews.getString(OMD_ID);
                content = reviews.getString(OMD_Content);
                url = reviews.getString(OMD_URL);

                Review rev = new Review();
                rev.setId(id);
                rev.setAuthor(author);
                rev.setContent(content);
                rev.setUrl(url);
                resultStr.add(rev);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (resultStr != null) {
            listener.onTaskComplete(resultStr);
        }
    }


}
