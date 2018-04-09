package com.example.android.popularmovie1;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView dvThumbnail;
    private TextView dvOriginalTitle;
    private TextView dvReleaseDate;
    private TextView dvRating;
    private TextView dvOverview;
    private ListView mReview;
    private ListView mTrailers;
    private TrailersAdapter mTrailersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dvThumbnail     = (ImageView) findViewById(R.id.iv_thumbnail);
        dvOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        dvReleaseDate   = (TextView) findViewById(R.id.tv_release_date);
        dvRating        = (TextView) findViewById(R.id.tv_user_rating);
        dvOverview      = (TextView) findViewById(R.id.tv_overview);

        Intent intent = getIntent();
        if(intent.hasExtra("movie")){
            Movie movie = getIntent().getParcelableExtra("movie");

            dvOriginalTitle.setText(movie.getTitle());
            dvReleaseDate.setText(movie.getReleaseDate());
            dvOverview.setText(movie.getOverview());
            dvRating.setText(movie.getVoteCount().toString());
            URL posterURL = NetworkTools.buildPosterUrl(movie.getPosterPath());
            Picasso.with(getBaseContext()).load(posterURL.toString()).fit().into(dvThumbnail);

            if(getBaseContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                ViewGroup.LayoutParams lp;
                lp = dvThumbnail.getLayoutParams();
                lp.width = 185*Resources.getSystem().getDisplayMetrics().heightPixels/(4*270)-16;
            }
            else{
                ViewGroup.LayoutParams lp;
                lp = dvThumbnail.getLayoutParams();
                lp.height = 270*Resources.getSystem().getDisplayMetrics().widthPixels/(2*185)-16;
            }

//            ArrayList<Review> R_Initial;
            ArrayList<Trailer> T_Initial ;
//            R_Initial= new ArrayList<>();
//            mReviewAdapter = new ReviewAdapter(getActivity().getApplicationContext(),R.layout.list_item_review,R_Initial);
//            mReview=(ListView) rootView.findViewById(R.id.RevList);
//            mReview.setAdapter(mReviewAdapter);

            T_Initial= new ArrayList<>();
            mTrailersAdapter=new TrailersAdapter(getApplicationContext(),R.layout.trailer_item,T_Initial);
            mTrailers=(ListView) findViewById(R.id.trailerList);
            mTrailers.setAdapter(mTrailersAdapter);

            mTrailers.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Trailer trailer = (Trailer) mTrailersAdapter.getItem(i);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                    startActivity(intent);
                }
            });




            //            if(isOnline()) {
                URL trailersURL = NetworkTools.buildTrailersUrl(movie.getId().toString());
//                Log.i("scroon", trailersURL.toString());
                //URL MovieUrl = NetworkTools.buildUrl(sortPopular);
                //String searchResults = null;
                new TrailerQueryTask().execute(trailersURL);
//            }else {
//
////                showErrorMessage();
//            }


        }

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
            if(s==null){
                //showErrorMessage();
                return;
            }
            // parsing the response.



            final String OMD_Results = "results";
            final String OMD_ID = "id";
            final String OMD_Name ="name";
            final String OMD_Key ="key";
            final String OMD_Site ="site";
            final String OMD_Type ="type";
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
            Trailer[] resultStr = new Trailer[TrailerArray.length()];

            for (int i = 0; i < TrailerArray.length(); i++) {
                String id;
                String name;
                String key;
                String site;
  //              String type;
                try {
                    JSONObject Trailers =TrailerArray.getJSONObject(i);

                    site = Trailers.getString(OMD_Site);
                    if (site.contentEquals("YouTube")) {

                        id = Trailers.getString(OMD_ID);
                        key = Trailers.getString(OMD_Key);
                        name = Trailers.getString(OMD_Name);
//                        type = Trailers.getString(OMD_Type);

                        resultStr[i] = new Trailer();
                        resultStr[i].setId(id);
                        resultStr[i].setKey(key);
                        resultStr[i].setName(name);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("scroon", String.valueOf(resultStr.length));

            if (resultStr != null) {

                mTrailersAdapter.clear();
                for(Trailer MovieStr : resultStr) {
                    mTrailersAdapter.add(MovieStr);
                }

            }
        }
    }

}
