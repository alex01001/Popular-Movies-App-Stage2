package com.example.android.popularmovie1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovie1.data.FavoritesContract;
import com.example.android.popularmovie1.data.FavoritesDBHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerItemClickListener , LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView dvThumbnail;
    private TextView dvOriginalTitle;
    private TextView dvReleaseDate;
    private TextView dvRating;
    private TextView dvOverview;
    // private ListView mReview;
    //private ListView mTrailers;
    // private TrailersAdapter mTrailersAdapter;

    private RecyclerView mRecyclerViewTrailers;
    private TrailersAdapter adapterTrailers;

    private RecyclerView mRecyclerViewReviews;
    private ReviewsAdapter adapterReviews;
    private SQLiteDatabase fDB;

    private ImageButton favButton;

    private static final int FAVORITES_LOADER_ID = 35;

    boolean markedAsFavorite;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dvThumbnail = (ImageView) findViewById(R.id.iv_thumbnail);
        dvOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        dvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        dvRating = (TextView) findViewById(R.id.tv_user_rating);
        dvOverview = (TextView) findViewById(R.id.tv_overview);

        favButton = (ImageButton) findViewById(R.id.btn_favorite);
        favButton.setBackgroundColor(Color.TRANSPARENT);
        // favButton.setImageAlpha(50);

        int backgroundOpacity = 10 * 0x01000000;

        favButton.setBackgroundColor(backgroundOpacity + 0xff0000);

        //        FavoritesDBHelper dbHelper = new FavoritesDBHelper(this);
//        mDB = dbHelper.getWritableDatabase();


        Intent intent = getIntent();
        if (intent.hasExtra("movie")) {
            movie = getIntent().getParcelableExtra("movie");

            dvOriginalTitle.setText(movie.getTitle());
            dvReleaseDate.setText(movie.getReleaseDate());
            dvOverview.setText(movie.getOverview());
            dvRating.setText(movie.getVoteCount().toString());
            URL posterURL = NetworkTools.buildPosterUrl(movie.getPosterPath());
            Picasso.with(getBaseContext()).load(posterURL.toString()).fit().into(dvThumbnail);

            if (getBaseContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ViewGroup.LayoutParams lp;
                lp = dvThumbnail.getLayoutParams();
                lp.width = 185 * Resources.getSystem().getDisplayMetrics().heightPixels / (4 * 270) - 16;
            } else {
                ViewGroup.LayoutParams lp;
                lp = dvThumbnail.getLayoutParams();
                lp.height = 270 * Resources.getSystem().getDisplayMetrics().widthPixels / (2 * 185) - 16;
            }
            // loading trailers
            mRecyclerViewTrailers = (RecyclerView) findViewById(R.id.rv_trailersList);
            adapterTrailers = new TrailersAdapter(getBaseContext(), this);
            mRecyclerViewTrailers.setAdapter(adapterTrailers);
            mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));

//            if(((MainActivity)getParent()).isOnline()) {
            URL trailersURL = NetworkTools.buildTrailersUrl(movie.getId().toString());
            new TrailerQueryTask().execute(trailersURL);
//            }

            // loading reviews
            mRecyclerViewReviews = (RecyclerView) findViewById(R.id.rv_reviewsList);
            adapterReviews = new ReviewsAdapter(getBaseContext());
            mRecyclerViewReviews.setAdapter(adapterReviews);
            mRecyclerViewReviews.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

//            if(((MainActivity)getParent()).isOnline()) {
            URL reviewsURL = NetworkTools.buildReviewsUrl(movie.getId().toString());
            new ReviewsQueryTask().execute(reviewsURL);
//            }

            // ADD QUERY TO DB and cnaging the button style if necessary
            markedAsFavorite = false;
            checkMovieIsInFavorites(movie);

        }

    }

    @Override
    public void onTrailerItemClick(int clickedItemIndex) {

        Trailer trailer = (Trailer) adapterTrailers.getItem(clickedItemIndex);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
        startActivity(intent);
    }

    public class TrailerQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoadingIndicator();
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
            final String OMD_Type = "type";
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
                //              String type;
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
            // Log.i("scroon", String.valueOf(resultStr.size()));

            if (resultStr != null) {
                adapterTrailers.setMovieData(resultStr);
            }
        }
    }


    public class ReviewsQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoadingIndicator();
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
                //              String type;
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

            Log.i("scroon", String.valueOf(resultStr.size()));
            if (resultStr != null) {
                adapterReviews.setMovieData(resultStr);

            }
        }
    }

    public void onClickFavorite(View view) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, movie.getId());

        if (markedAsFavorite) {
            // remove from favorites

            String[] mID = new String[]{movie.getId().toString()};
            String mSelection = "movieID=?";

            Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movie.getId().toString()).build();

            int n = getContentResolver().delete(uri,null,null);
        //    getContentResolver().delete(uri, null, null);
            favButton.setImageResource(R.drawable.star_empty);
            markedAsFavorite = false;
            Toast toast = Toast.makeText(getApplicationContext(), "Removed from favorites.",
                    Toast.LENGTH_SHORT);
            toast.show();


        } else {
            // add to favorites
            Uri uri = getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, contentValues);
            favButton.setImageResource(R.drawable.star_filled);
            markedAsFavorite = true;
            Toast toast = Toast.makeText(getApplicationContext(), "Added to favorites.",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

        finish();
    }


    public void checkMovieIsInFavorites(Movie movie){
        Log.i("scroon ", " checking movie in fav");
        String searchResults = null;
        Bundle queryBundle = new Bundle();
        queryBundle.putString("query", String.valueOf(movie.getId()));

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> queryLoader = loaderManager.getLoader(FAVORITES_LOADER_ID);

        if (queryLoader == null) {
            Log.i("scroon null", "null");
            loaderManager.initLoader(FAVORITES_LOADER_ID, queryBundle, this);
        } else {
            Log.i("scroon not", "null");
            loaderManager.restartLoader(FAVORITES_LOADER_ID, queryBundle, this);
        }



    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle bundle) {

        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                if(bundle==null){
                    return;
                }
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {

                String movieID = bundle.getString("query");
                if(movieID!=null & !movieID.isEmpty()) {
                    String[] mID = new String[]{movieID};
                    String mSelection = "movieID=?";


                    Cursor cursor = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                            null,
                            mSelection,
                            mID,
                            null);
                    Log.i("Croon count", String.valueOf(cursor.getCount()));
                    while(cursor.moveToNext()){
                        Log.i("Croon sel", cursor.getString(0));
                    }
                    if(cursor.getCount()==0){
                        return null;
                    }else {
                        return cursor;
                    }

                }
                return null;
            }


        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data !=null){
            markedAsFavorite = true;
            favButton.setImageResource(R.drawable.star_filled);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
