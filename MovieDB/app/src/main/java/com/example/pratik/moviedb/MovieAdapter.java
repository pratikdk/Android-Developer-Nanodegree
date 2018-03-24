package com.example.pratik.moviedb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Pratik on 17-03-2018.
 */

/**
 * {@link MovieAdapter} exposes a list of Movies to a
 * {@link android.support.v7.widget.RecyclerView}
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovieList;

    /*
     * An on-click handler defined to make it easy for an Activity to interface with
     * the RecyclerView
     */
    private final MovieAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(String title, Double averageRating, String releaseDate, String synopsis, String thumbnailPath);
    }


    /**
     * Creates a {@link MovieAdapter} object
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a {@link Movie} object list item.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mMovieTitleTextView;
        public final ImageView mMovieThumbnailImageView;
        public final TextView mMovieYearTextView;

        public final String mTmdbImageBaseUrl = "http://image.tmdb.org/t/p/w200";

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieTitleTextView = (TextView) view.findViewById(R.id.movie_title);
            mMovieThumbnailImageView = (ImageView) view.findViewById(R.id.thumbnail_image);
            mMovieYearTextView = (TextView) view.findViewById(R.id.movie_thumb_date);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            Movie clickedMovie = mMovieList.get(adapterPosition);

            String title = clickedMovie.getmTitle();
            Double averageRating = clickedMovie.getmAverageRating();
            String releaseDate = clickedMovie.getmReleaseDate();
            String synopsis = clickedMovie.getmSynopsis();
            String thumbnailPath = clickedMovie.getmThumbnailPath();

            mClickHandler.onClick(title, averageRating, releaseDate, synopsis, thumbnailPath);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new MovieAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.thumbnail;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the Movie
     * Thumbnail with its title for this particular position, using the "position" argument that is
     * conveniently passed into us.
     *
     * @param movieAdapterViewHolder The ViewHolder which should be updated to represent the
     *                               contents of the item at the given position in the data set.
     * @param position               The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        Movie clickedMovie = mMovieList.get(position);
        Context context = movieAdapterViewHolder.mMovieThumbnailImageView.getContext();

        String title = clickedMovie.getmTitle();

        String releaseDate = clickedMovie.getmReleaseDate();
        String releaseDateArray[] = releaseDate.split("-");
        String releaseYear = releaseDateArray[0];

        String thumbnailPath = clickedMovie.getmThumbnailPath();

        movieAdapterViewHolder.mMovieTitleTextView.setText(title);
        movieAdapterViewHolder.mMovieYearTextView.setText(releaseYear);

        // TODO : Set ThumbNail For Movie using picasso
        Picasso.with(context)
                .load("" + movieAdapterViewHolder.mTmdbImageBaseUrl + clickedMovie.getmThumbnailPath())
                .into(movieAdapterViewHolder.mMovieThumbnailImageView);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our mMovieList
     */
    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    /**
     * This method is used to set the mMovieList on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param movieList The list of movies to be displayed.
     */

    public void setmMovieList(ArrayList<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    public void clearListForUpdate() {
        if (mMovieList != null && !mMovieList.isEmpty()) {
            mMovieList.clear();
        }
    }

    public void dataSetChanged() {
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getCurrentList() {
        return mMovieList;
    }
}
