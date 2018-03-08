package sohage.example.com.movie.Activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sohage.example.com.movie.Adapter.MovieAdapter;
import sohage.example.com.movie.Model.MovieModel;
import sohage.example.com.movie.R;

public class SearchActivity extends AppCompatActivity {
    RecyclerView r;
    Activity activity;
    MovieAdapter adapter;
    Snackbar snackbar;
    CoordinatorLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        r = (RecyclerView) findViewById(R.id.searchRV);
        r.setLayoutManager(new GridLayoutManager(this, 2));
        if (getIntent().hasExtra("Query")) {
            GetData(getIntent().getStringExtra("Query"));
        }
        activity = this;
        view = (CoordinatorLayout) findViewById(R.id.activity_search);
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
                GetData(query);
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

    private void DisplaySnackBar(String message) {
        snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
    }

    private void GetData(final String Query) {
        final List<MovieModel> newMovies = new ArrayList<>();
        AndroidNetworking.get("https://api.themoviedb.org/3/search/movie?api_key=####&query={Query}")
                .setPriority(Priority.LOW)
                .addPathParameter("Query", Query)
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
                                        original_title = movie.optString("original_title");
                                MovieModel myMovie = new MovieModel(vote_average, poster_path, original_title, id);
                                newMovies.add(myMovie);
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (newMovies.size() == 0) {
                                        DisplaySnackBar(getResources().getString(R.string.EmptyList));
                                        snackbar.show();
                                    }
                                    adapter = new MovieAdapter(getBaseContext(), newMovies);
                                    r.setAdapter(adapter);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            DisplaySnackBar(getResources().getString(R.string.NoConnection));
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        DisplaySnackBar(getResources().getString(R.string.NoConnection));
                        snackbar.show();
                    }
                });
    }
}