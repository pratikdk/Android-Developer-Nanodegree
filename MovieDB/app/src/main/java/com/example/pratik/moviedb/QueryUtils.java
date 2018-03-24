package com.example.pratik.moviedb;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pratik on 17-03-2018.
 */

// http://api.themoviedb.org/3/discover/movie?api_key=3d27da90a1e868234c34efb8f23aef8e&sort_by=popularity.desc&primary_release_year=2017
// http://api.themoviedb.org/3/movie/popular?api_key=3d27da90a1e868234c34efb8f23aef8e

public class QueryUtils {
    private static final String TAG = QueryUtils.class.getSimpleName();

    /**
     * Base TMDB URL to request movie data
     */
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";

    /* API key */
    private static final String API_KEY = "3d27da90a1e868234c34efb8f23aef8e"; // Apply API Key here

    /* Query Base Path */
    private static final String BASE_PATH_DISCOVER = "discover";
    private static final String BASE_PATH_MOVIE = "movie";

    /* Query Parameters */
    private final static String API_PARAM = "api_key";
    private final static String SORT_PARAM = "sort_by";
    private final static String YEAR_PARAM = "primary_release_year";

    /**
     * Private constructor, for only direct access
     */
    private QueryUtils() {

    }

    /**
     * Query the TMDB API and return an {@link Movie} ArrayList object
     * to represent list of movies based on the criteria applied
     */
    public static List<Movie> fetchMovieDataList(String sortQuery, String yearQuery) {
        Log.e(TAG, "Test: QueryUtils fetchMovieDataList() called.");

        // Create URL Object
        URL url = buildUrl(sortQuery, yearQuery);

        // Perform HTTP request using url and receive a JSON response back
        String movieListJson = null;
        try {
            movieListJson = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            Log.e(TAG, "Error closing the input stream " + e);
        }

        // Extract all movies from the json response and store it in a list.
        List<Movie> movieList = extractMoviesFromJson(movieListJson);

        if (movieList == null) {
            if (movieList == null) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Return the {@ movieList}
        return movieList;
    }


    /**
     * Builds the URL used to talk to the TMDB server using a criteria. This criteria is based on
     * the query capabilities of the TMDB API that we are using.
     *
     * @param sortQuery The sort criteria
     * @param yearQuery The release year
     * @return The URL to use to query the weather server.
     */
    private static URL buildUrl(String sortQuery, String yearQuery) {
        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(BASE_PATH_DISCOVER)
                .appendPath(BASE_PATH_MOVIE)
                .appendQueryParameter(API_PARAM, API_KEY)
                .appendQueryParameter(SORT_PARAM, sortQuery)
                .appendQueryParameter(YEAR_PARAM, yearQuery)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL : " + url);

        return url;
    }

    /**
     * Return a list of {@link Movie} objects by parsing out
     * information from the given json string
     */
    private static List<Movie> extractMoviesFromJson(String movieListJson) {
        // If the JSON string is empty or null return early
        if (TextUtils.isEmpty(movieListJson)) {
            return null;
        }

        // Empty ArrayList for Movie objects
        ArrayList<Movie> movies = new ArrayList<>();

        // Try to Parse json Stored in String movieListJson
        try {
            JSONObject jsonObject = new JSONObject(movieListJson);

            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieObject = resultsArray.getJSONObject(i);

                String title = movieObject.getString("title");
                Double averageRating = movieObject.getDouble("vote_average");
                String releaseDate = movieObject.getString("release_date");
                String synopsis = movieObject.getString("overview");
                String thumbnailPath = movieObject.getString("poster_path");

                Movie movie = new Movie(title, averageRating, releaseDate, synopsis, thumbnailPath);

                movies.add(movie);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the movie JSON results", e);
        }
        return movies;
    }

    public static void modifyPreference(Context context, SharedPreferences prefs, String sortByValue, String releaseYearValue) {
        Log.e(TAG, "modifyPreference Called()");
        String sortByKey = context.getResources().getString(R.string.settings_sort_by_key);
        String releaseYearKey = context.getResources().getString(R.string.settings_release_year_key);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(sortByKey, sortByValue);
        editor.putString(releaseYearKey, releaseYearValue);
        editor.apply();
    }
}
