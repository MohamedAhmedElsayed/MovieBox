package sohage.example.com.movie.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sohage.example.com.movie.Model.ReviewModel;
import sohage.example.com.movie.R;

/**
 * Created by Mohamed_Ahmed on 27/02/2018.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    List<ReviewModel> reviews;
    Context c;

    public ReviewAdapter(Context c, List<ReviewModel> list) {
        this.c = c;
        this.reviews = list;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, parent, false);
        return new ReviewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        ReviewModel reviewModel = reviews.get(position);
        holder.review.setText(reviewModel.getContent());
        holder.user.setText(reviewModel.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        TextView user, review;

        public ReviewHolder(View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.AuthorTxt);
            review = itemView.findViewById(R.id.ContentTxt);
        }
    }
}
