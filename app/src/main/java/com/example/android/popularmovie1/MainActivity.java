package com.example.android.popularmovie1;

import android.app.Activity;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovie1.data.FavoritesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener, LoaderManager.LoaderCallbacks<String> {

    int sortMethod; // 0 = Popular Movies, 1 = top rated, 2 - favorites form phone's DB.
    public ArrayList<Movie> movieList;

    @BindView(R.id.tv_error_message_diaplay) TextView errorMessageTextView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.rv_postersGrid) RecyclerView mRecyclerView;

    private MovieAdapter adapter;
    private static final int FAVORITES_LOADER_ID = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sortMethod = 0;

        adapter = new MovieAdapter(getBaseContext(), this);
        mRecyclerView.setAdapter(adapter);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 4));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
        }
        // avoid reloading the movie list from the internet on device rotate
        if(savedInstanceState!=null){
            movieList = savedInstanceState.getParcelableArrayList("MOVIE_LIST");
            adapter.setMovieData(movieList);
            showPosterGrid();
        }
        else{
            makeSearchQuery();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("MOVIE_LIST", movieList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    // check if we are connected to a network
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    private void makeSearchQuery() {
        if(sortMethod == 2){
            // load from DB
            if (isOnline()) {
                String searchResults = null;
                Bundle queryBundle = new Bundle();
                queryBundle.putString("query", "LOAD_FROM_DB");
                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<String> queryLoader = loaderManager.getLoader(FAVORITES_LOADER_ID);

                if (queryLoader == null) {
                    loaderManager.initLoader(FAVORITES_LOADER_ID, queryBundle, this);
                } else {
                    loaderManager.restartLoader(FAVORITES_LOADER_ID, queryBundle, this);
                }
            }
        }
        else if(sortMethod==0 || sortMethod==1) {

            if (isOnline()) {
                URL MovieUrl = NetworkTools.buildUrl(sortMethod);
                String searchResults = null;
                Bundle queryBundle = new Bundle();
                queryBundle.putString("query", MovieUrl.toString());
                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<String> queryLoader = loaderManager.getLoader(FAVORITES_LOADER_ID);

                if(queryLoader == null){
                    loaderManager.initLoader(FAVORITES_LOADER_ID, queryBundle, this);
                } else{
                    loaderManager.restartLoader(FAVORITES_LOADER_ID, queryBundle, this);
                }

            } else {
                showErrorMessage();
            }
        }
    }

    private void showPosterGrid(){
        mRecyclerView.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.GONE);
    }
    // shows error when unable to load data
    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.GONE);
    }
    private void showLoadingIndicator(){
        mRecyclerView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMovieItemClick(int clickedItemIndex, ImageView posterImg) {
        Context context = MainActivity.this;
        Class detActivity = DetailActivity.class;
        Intent intent = new Intent(context,detActivity);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,(View) posterImg, "sharedPoster");
        intent.putExtra("movie", movieList.get(clickedItemIndex));
        startActivity(intent,optionsCompat.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItem = item.getItemId();
        if (selectedMenuItem == R.id.action_sortMP) {
            sortMethod = 0;
        }else if (selectedMenuItem == R.id.action_sortTR) {
            sortMethod = 1;
        }else if (selectedMenuItem == R.id.action_sortFav) {
            sortMethod = 2;
        }
        makeSearchQuery();
        return super.onOptionsItemSelected(item);
    }

    //  LOADER METHODS
    @Override
    public Loader<String> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                if(bundle==null){
                    return;
                }
                showLoadingIndicator();
                forceLoad();
            }

            @Override
            public String loadInBackground() {

                String queryURLString = bundle.getString("query");
                if(queryURLString=="LOAD_FROM_DB"){
                    // assemble imitation of JSON response for the list of favorite movies
                    Cursor cursor = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    String movieID = "";
                    String JSONresponse = "{\"page\":1,\"total_results\":19930,\"total_pages\":997,\"results\":[";
                    while(cursor.moveToNext()){
                        movieID = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID));
                        try{
                            URL movieUrl = NetworkTools.buildMovieUrl(movieID);
                            String movieJSON = NetworkTools.getResponseFromHttpUrl(movieUrl);
                            JSONresponse = JSONresponse + movieJSON + ",";
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    // remove last comma form the string
                    JSONresponse = JSONresponse.substring(0,JSONresponse.length()-1);
                    JSONresponse += "]}";
                    cursor.close();

                    return JSONresponse;
                }
                else {
                    if (queryURLString == null || TextUtils.isEmpty(queryURLString)) {
                        return null;
                    }
                    try {
                        URL searchUrl = new URL(queryURLString);
                        return NetworkTools.getResponseFromHttpUrl(searchUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        if(s==null){
            showErrorMessage();
            return;
        }
        // parsing the response.
        movieList = new ArrayList<Movie>();

        if(s!=null && !s.equals("")) {
            JSONObject movieJSON;
            JSONArray movies;
            try {
                movieJSON = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            try {
                movies = movieJSON.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            for (int i=0; i<movies.length(); i++) {
                Movie movie = new Movie();
                JSONObject movieItem;
                try {
                    movieItem =  movies.getJSONObject(i);
                    movie.setVoteCount(movieItem.getInt("vote_count"));
                    movie.setId(movieItem.getInt("id"));
                    movie.setVoteAverage(movieItem.getDouble("vote_average"));
                    movie.setTitle(movieItem.getString("title"));
                    movie.setPopularity(movieItem.getDouble("popularity"));
                    movie.setPosterPath(movieItem.getString("poster_path"));
                    movie.setOriginalTitle(movieItem.getString("original_title"));
                    movie.setOverview(movieItem.getString("overview"));
                    movie.setReleaseDate(movieItem.getString("release_date"));

                } catch (JSONException e) {
                    // if one of the necessary fields doesn't exist, don't include the movie in the list
                    e.printStackTrace();
                    return;
                }

                // checking secondary (less important fields. I something is not there - move on
                try {
                    movie.setVideo(movieItem.getBoolean("video"));
                    movie.setOriginalLanguage(movieItem.getString("original_language"));

                    List<Integer> genIDs = new ArrayList<Integer>();
                    JSONArray genre_ids = movieItem.getJSONArray("genre_ids");
                    for (int j=0; j<genre_ids.length(); j++) {
                        genIDs.add(genre_ids.getInt(j));
                    }
                    movie.setGenreIds(genIDs);
                    movie.setBackdropPath(movieItem.getString("backdrop_path"));
                    movie.setAdult(movieItem.getBoolean("adult"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                movieList.add(movie);

            }
            adapter.setMovieData(movieList);
            showPosterGrid();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}
