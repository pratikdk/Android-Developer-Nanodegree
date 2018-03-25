package com.example.pratik.moviedb;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Pratik on 18-03-2018.
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    public static final String TAG = MovieLoader.class.getName();

    private String mSortQuery;

    public MovieLoader(Context context, String sortQuery) {
        super(context);
        mSortQuery = sortQuery;
    }

    @Override
    protected void onStartLoading() {
        Log.e(TAG, "Test: MovieLoader onStartLoading() called.");
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        Log.e(TAG, "Test: MovieLoader loadInBackground() called.");
        if (mSortQuery == null) {
            return null;
        }

        List<Movie> movieList = QueryUtils.fetchMovieDataList(mSortQuery);
        return movieList;
    }
}
