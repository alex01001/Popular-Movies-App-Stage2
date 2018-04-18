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

import com.example.android.popularmovie1.data.AsyncTaskCompleteListenerT;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerItemClickListener , LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.iv_thumbnail) ImageView dvThumbnail;
    @BindView(R.id.tv_original_title) TextView dvOriginalTitle;
    @BindView(R.id.tv_release_date) TextView dvReleaseDate;
    @BindView(R.id.tv_user_rating) TextView dvRating;
    @BindView(R.id.tv_overview) TextView dvOverview;
    @BindView(R.id.btn_favorite) ImageButton favButton;
    @BindView(R.id.rv_trailersList) RecyclerView mRecyclerViewTrailers;
    @BindView(R.id.rv_reviewsList) RecyclerView mRecyclerViewReviews;

    private TrailersAdapter adapterTrailers;
    private ReviewsAdapter adapterReviews;

    private SQLiteDatabase fDB;
    private static final int FAVORITES_LOADER_ID = 35;
    boolean markedAsFavorite;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        favButton.setBackgroundColor(Color.TRANSPARENT);
        int backgroundOpacity = 1 * 0x01000000;
        favButton.setBackgroundColor(backgroundOpacity + 0xff0000);

        Intent intent = getIntent();
        if (intent.hasExtra("movie")) {
            movie = getIntent().getParcelableExtra("movie");

            dvOriginalTitle.setText(movie.getTitle());
            dvReleaseDate.setText(movie.getReleaseDate());
            dvOverview.setText(movie.getOverview());
            dvRating.setText("Rating: "+movie.getVoteAverage().toString());
            URL posterURL = NetworkTools.buildPosterUrl(movie.getPosterPath());
            Picasso.with(getBaseContext()).load(posterURL.toString()).fit().into(dvThumbnail);

            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ViewGroup.LayoutParams lp;
                lp = dvThumbnail.getLayoutParams();

                lp.width = Resources.getSystem().getDisplayMetrics().widthPixels/4;
                lp.height  = 270*lp.width /185;
            } else {
                ViewGroup.LayoutParams lp;
                lp = dvThumbnail.getLayoutParams();
                lp.height = Resources.getSystem().getDisplayMetrics().heightPixels /3;
                lp.width  = 185*lp.height /270;

            }
            // loading trailers
            adapterTrailers = new TrailersAdapter(getBaseContext(), this);
            mRecyclerViewTrailers.setAdapter(adapterTrailers);
            mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));


            if((isOnline())) {
                URL trailersURL = NetworkTools.buildTrailersUrl(movie.getId().toString());
                new FetchMyTrailersTask(this, new FetchMyTrailersCompleteListener()).execute(trailersURL);
            }

            // loading reviews
            adapterReviews = new ReviewsAdapter(getBaseContext());
            mRecyclerViewReviews.setAdapter(adapterReviews);
            mRecyclerViewReviews.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

            if((isOnline())) {
                URL reviewsURL = NetworkTools.buildReviewsUrl(movie.getId().toString());
                new FetchMyReviewsTask(this, new FetchMyReviewsCompleteListener()).execute(reviewsURL);
            }

            // ADD QUERY TO DB and cnaging the button style if necessary
            markedAsFavorite = false;
            checkMovieIsInFavorites(movie);

        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onTrailerItemClick(int clickedItemIndex) {
        Trailer trailer = (Trailer) adapterTrailers.getItem(clickedItemIndex);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
        startActivity(intent);
    }

    public class FetchMyTrailersCompleteListener implements AsyncTaskCompleteListenerT<ArrayList<Trailer>> {

        @Override
        public void onTaskComplete(ArrayList<Trailer> result)
        {
            if (result != null) {
                adapterTrailers.setMovieData(result);
            }
        }
    }

    public class FetchMyReviewsCompleteListener implements AsyncTaskCompleteListenerT<ArrayList<Review>> {

        @Override
        public void onTaskComplete(ArrayList<Review> result)
        {
            if (result != null) {
                adapterReviews.setMovieData(result);
            }
        }
    }

    public void onClickFavorite(View view) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME, movie.getOriginalTitle());

        if (markedAsFavorite) {
            // remove from favorites

            Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movie.getId().toString()).build();

            int n = getContentResolver().delete(uri,null,null);
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

    }


    public void checkMovieIsInFavorites(Movie movie){
        Bundle queryBundle = new Bundle();
        queryBundle.putString("query", String.valueOf(movie.getId()));
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> queryLoader = loaderManager.getLoader(FAVORITES_LOADER_ID);

        if (queryLoader == null) {
            loaderManager.initLoader(FAVORITES_LOADER_ID, queryBundle, this);
        } else {
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
