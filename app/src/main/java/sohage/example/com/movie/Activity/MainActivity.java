package sohage.example.com.movie.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sohage.example.com.movie.Adapter.MovieAdapter;
import sohage.example.com.movie.Model.MovieModel;
import sohage.example.com.movie.R;

public class MainActivity extends AppCompatActivity {
    List<MovieModel> Movies2;
    Activity activity;
    RecyclerView Upcoming, TopRated, Popular;
    MovieAdapter UpcomingAdapter, TopRatedAdapter, PopularAdapter;
    LinearLayout layout;
    Snackbar snackbar;
    CoordinatorLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = (CoordinatorLayout) findViewById(R.id.mainLayout);
        if (savedInstanceState != null && !savedInstanceState.getBoolean("build")) {
            PicassoSaveImages();
        }
        AndroidNetworking.initialize(getApplicationContext());
        layout = (LinearLayout) findViewById(R.id.activity_main);
        layout.setVisibility(View.INVISIBLE);
        Movies2 = new ArrayList<>();
        activity = this;
        SetSnackBar();
        Upcoming = (RecyclerView) findViewById(R.id.UpcomingMovies);
        TopRated = (RecyclerView) findViewById(R.id.TopRatedMovies);
        Popular = (RecyclerView) findViewById(R.id.popularMovies);
        Upcoming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        TopRated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Popular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        GetData("popular", 1);
        GetData("top_rated", 2);
        GetData("upcoming", 3);
     }

    private void SetSnackBar() {
        snackbar = Snackbar
                .make(view, getResources().getString(R.string.NoConnection)
                        , Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GetData("popular", 1);
                        GetData("top_rated", 2);
                        GetData("upcoming", 3);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.SearchAble);
        SearchView sv = new SearchView(this.getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                intent.putExtra("Query", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void GetData(final String MovieType, final int id) {
        final List<MovieModel> newMovies = new ArrayList<>();
        AndroidNetworking.get("https://api.themoviedb.org/3/movie/{type}?api_key=####")
                .setPriority(Priority.LOW)
                .addPathParameter("type", MovieType)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            int size = results.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject movie = results.getJSONObject(i);
                                String vote_average = movie.optString("vote_average"),
                                        id = movie.optString("id"),
                                        poster_path = movie.optString("poster_path"),
                                        original_title = movie.optString("title");
                                MovieModel myMovie = new MovieModel(vote_average, poster_path, original_title, id);
                                newMovies.add(myMovie);
                            }
                            layout.setVisibility(View.VISIBLE);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (id == 1) {
                                        PopularAdapter = new MovieAdapter(getBaseContext(), newMovies);
                                        Popular.setAdapter(PopularAdapter);
                                    } else if (id == 2) {
                                        TopRatedAdapter = new MovieAdapter(getBaseContext(), newMovies);
                                        TopRated.setAdapter(TopRatedAdapter);
                                    } else if (id == 3) {
                                        UpcomingAdapter = new MovieAdapter(getBaseContext(), newMovies);
                                        Upcoming.setAdapter(UpcomingAdapter);
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (!snackbar.isShown()) {
                                snackbar.show();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (!snackbar.isShown()) {
                            snackbar.show();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("build", true);
    }

    private void PicassoSaveImages() {
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}


