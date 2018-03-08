package sohage.example.com.movie.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import sohage.example.com.movie.Activity.DetailsActivity;
import sohage.example.com.movie.Model.MovieModel;
import sohage.example.com.movie.R;
 
/**
 * Created by Mohamed_Ahmed on 23/02/2018.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    private Context context;
    private List<MovieModel> movies;

    public MovieAdapter(Context c, List<MovieModel> list) {
        movies = list;
        context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_layout, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final MovieAdapter.MyViewHolder holder, final int position) {
        Picasso.with(context).load("https://image.tmdb.org/t/p/w185" + movies.get(position).getPoster_path())
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(context)
                                .load(movies.get(position).getPoster_path())
                                .into(holder.imageView);
                    }
                });
        if (movies.get(position).getOriginal_title().length() > 15) {
            movies.get(position).setOriginal_title(movies.get(position).getOriginal_title().substring(0, 14) + "..");
        }
        holder.title.setText(movies.get(position).getOriginal_title());
        holder.rate.setText(movies.get(position).getVote_average());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, rate;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.MoveImage);
            title = itemView.findViewById(R.id.MovieTitle);
            rate = itemView.findViewById(R.id.MovieRate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("MovieID", movies.get(getAdapterPosition()).getMovieID());
                    context.startActivity(intent);
                }
            });
        }
    }
}

