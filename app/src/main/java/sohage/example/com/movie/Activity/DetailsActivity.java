package sohage.example.com.movie.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sohage.example.com.movie.Adapter.MovieAdapter;
import sohage.example.com.movie.Adapter.ReviewAdapter;
import sohage.example.com.movie.Model.MovieModel;
import sohage.example.com.movie.Model.ReviewModel;
import sohage.example.com.movie.R;

public class DetailsActivity extends YouTubeBaseActivity implements View.OnClickListener {
    private Activity activity;
    private ImageView MoveImage;
    private TextView title, overview, language, companies, revenue, rate, Rdate, genresTxt;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayer.OnInitializedListener listener;
    private List<MovieModel> newMovies;
    private RecyclerView SimilarMoviesRV, Reviews_RV;
    private MovieAdapter adapter;
    private LinearLayout layout;
    private YouTubePlayerView playerView;
    private Button PlayVideoButton;

    public DetailsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        layout = findViewById(R.id.activity_details);
        activity = this;
        title = findViewById(R.id.DMovieTitle);
        overview = findViewById(R.id.DMovieOverview);
        language = findViewById(R.id.DLanguage);
        companies = findViewById(R.id.DProduction_companies);
        revenue = findViewById(R.id.Drevenue);
        rate = findViewById(R.id.DMovieRate);
        Rdate = findViewById(R.id.DMovieRDate);
        MoveImage = findViewById(R.id.DMovieImage);
        SimilarMoviesRV = findViewById(R.id.SimilarMovieRV);
        SimilarMoviesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Reviews_RV = findViewById(R.id.ReviewRV);
        Reviews_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        genresTxt = findViewById(R.id.DMovieGenres);
        playerView = findViewById(R.id.YouTubeView);
        PlayVideoButton = findViewById(R.id.PlayVideoBut);
        PlayVideoButton.setOnClickListener(this);
        String movieID = getIntent().getStringExtra("MovieID");
        if (movieID != null || !movieID.isEmpty())
            GetMovieDetails(movieID);
    }

    private void GetMovieDetails(final String MovieID) {
        AndroidNetworking.get("https://api.themoviedb.org/3/movie/{movieID}?api_key=####&append_to_response=similar_movies,videos,reviews")
                .setPriority(Priority.LOW)
                .addPathParameter("movieID", MovieID)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final String
                                    vote_average = "Average Rating: " + response.optString("vote_average"),
                                    poster_path = response.optString("poster_path"),
                                    original_language = response.optString("original_language"),
                                    original_title = response.optString("original_title"),
                                    MovieOverview = response.optString("overview"),
                                    release_date = "Released Date: " + response.optString("release_date"),
                                    Revenue = "Revenue: " + response.optString("revenue");
                            Picasso.with(getBaseContext())
                                    .load("https://image.tmdb.org/t/p/w185" + poster_path)
                                    .into(MoveImage);
                            layout.setVisibility(View.VISIBLE);
                            title.setText(original_title);
                            language.setText(original_language);
                            overview.setText(MovieOverview);
                            rate.setText(vote_average);
                            Rdate.setText(release_date);
                            revenue.setText(Revenue);
                            Get_similar_movies(response.optJSONObject("similar_movies").optJSONArray("results"));
                            Get_production_companies(response.optJSONArray("production_companies"));
                            JSONArray results = response.optJSONObject("videos").optJSONArray("results");
                            String MovieVideoID = "";
                            if (results.length() > 0)
                                MovieVideoID = results.optJSONObject(0).optString("key");
                            PlayVideo(MovieVideoID);
                            GetReviews(response.getJSONObject("reviews").optJSONArray("results"));
                            GetGenres(response.optJSONArray("genres"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                          //  Toast.makeText(getBaseContext(), "Error Getting Data", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.NoConnection), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void GetGenres(JSONArray genres) {
        if (genres != null) {
            int size = genres.length();
            String result = "";
            for (int i = 0; i < size; i++) {
                try {
                    result += genres.getJSONObject(i).optString("name") + " ,";
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.NoConnection), Toast.LENGTH_LONG).show();
                }
            }
            if (result.length() > 0)
                result = result.substring(0, result.length() - 1) + ".";
            genresTxt.setText(result);
        }
    }

    private void GetReviews(JSONArray jsonArray) {
        if (jsonArray != null) {
            List<ReviewModel> list = new ArrayList<>();
            int size = jsonArray.length();
            try {
                for (int i = 0; i < size; i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    list.add(new ReviewModel(object.optString("author"), object.optString("content")));
                }
                Reviews_RV.setAdapter(new ReviewAdapter(getBaseContext(), list));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, getResources().getString(R.string.NoConnection), Toast.LENGTH_LONG).show();
                Log.d("Error", "Cant Get Similar Movies");
            }
        }
    }

    private void Get_similar_movies(JSONArray results) {
        if (results != null) {
            int size = results.length();
            newMovies = new ArrayList<>();
            try {
                for (int i = 0; i < size; i++) {
                    JSONObject movie = results.getJSONObject(i);
                    String
                            vote_average = movie.optString("vote_average"),
                            id = movie.optString("id"),
                            poster_path = movie.optString("poster_path"),
                            original_title = movie.optString("original_title");
                    MovieModel myMovie = new MovieModel(vote_average, poster_path, original_title, id);
                    newMovies.add(myMovie);
                }
                adapter = new MovieAdapter(this, newMovies);
                SimilarMoviesRV.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, getResources().getString(R.string.NoConnection), Toast.LENGTH_LONG).show();
                Log.d("Error", "Cant Get Similar Movies"+e.toString());
            }
        }
    }

    private void Get_production_companies(JSONArray production_companies) {
        if (production_companies != null) {
            String MovieCompanies = "";
            int size = production_companies.length();
            for (int i = 0; i < size; i++) {
                JSONObject company = null;
                try {
                    company = production_companies.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, getResources().getString(R.string.NoConnection), Toast.LENGTH_LONG).show();
                    Log.d("Error", "Cant Get Similar Movies"+e.toString());

                }
                MovieCompanies += company.optString("name") + " ,";
            }
            if (MovieCompanies.length() > 0)
                MovieCompanies = MovieCompanies.substring(0, MovieCompanies.length() - 1) + ".";
            companies.setText(MovieCompanies);
        }
    }

    private void PlayVideo(final String s) {
        listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(s);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.PlayVideoError), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if ((v.getId() == PlayVideoButton.getId()) && (listener != null)) {
            playerView.initialize("####", listener);
            v.setVisibility(View.GONE);
        }
    }
}



