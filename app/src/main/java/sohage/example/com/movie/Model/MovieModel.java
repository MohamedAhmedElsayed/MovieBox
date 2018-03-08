package sohage.example.com.movie.Model;

/**
 * Created by Mohamed_Ahmed on 23/02/2018.
 */
public class MovieModel {
    private String vote_count,
            vote_average,
            popularity,
            poster_path,
            original_language,
            original_title,
            overview,
            release_date,
            MovieID;

    public MovieModel(String vote_average, String poster_path,
                      String original_title, String movieID) {
        this.vote_count = "";
        this.vote_average = vote_average;
        this.popularity = "";
        this.poster_path = poster_path;
        this.original_language = "";
        this.original_title = original_title;
        this.overview = "";
        this.release_date = "";
        MovieID = movieID;
    }

    public MovieModel(String vote_count, String vote_average, String popularity, String poster_path,
                      String original_language, String original_title, String overview, String release_date, String movieID) {
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.release_date = release_date;
        MovieID = movieID;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getMovieID() {
        return MovieID;
    }

    public void setMovieID(String movieID) {
        MovieID = movieID;
    }
}