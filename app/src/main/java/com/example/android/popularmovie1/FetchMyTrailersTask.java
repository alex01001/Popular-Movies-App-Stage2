package com.example.android.popularmovie1;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android.popularmovie1.data.AsyncTaskCompleteListenerT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class FetchMyTrailersTask extends AsyncTask<URL, Integer, String> {
    private Context context;
    private AsyncTaskCompleteListenerT<ArrayList<Trailer>> listener;

    public FetchMyTrailersTask(Context ctx, AsyncTaskCompleteListenerT<ArrayList<Trailer>> listener)
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
        final String OMD_Name = "name";
        final String OMD_Key = "key";
        final String OMD_Site = "site";
        JSONObject TrailerJson;
        try {
            TrailerJson = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JSONArray TrailerArray;
        try {
            TrailerArray = TrailerJson.getJSONArray(OMD_Results);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        ArrayList<Trailer> resultStr = new ArrayList<Trailer>();

        for (int i = 0; i < TrailerArray.length(); i++) {
            String id;
            String name;
            String key;
            String site;
            try {
                JSONObject Trailers = TrailerArray.getJSONObject(i);

                site = Trailers.getString(OMD_Site);
                if (site.contentEquals("YouTube")) {
                    id = Trailers.getString(OMD_ID);
                    key = Trailers.getString(OMD_Key);
                    name = Trailers.getString(OMD_Name);

                    Trailer tr = new Trailer();
                    tr.setId(id);
                    tr.setKey(key);
                    tr.setName(name);
                    resultStr.add(tr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (resultStr != null) {
            listener.onTaskComplete(resultStr);
        }
    }


}
