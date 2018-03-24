package com.example.pratik.moviedb;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>, MovieAdapter.MovieAdapterOnClickHandler, AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TMDB_QUERY_LOADER = 3;
    private Context context;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private Toolbar mToolbar;
    private Spinner mSpinner;
    private ArrayAdapter<String> spinnerAdapter;

    // Flag to control default spinner calls.
    private int check = 0;

    // Variable to store ref for Movie object ArrayList, used by savedInstanceState
    private ArrayList<Movie> currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate Called()");
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // int spanCount = Utility.calculateNoOfColumns(getApplicationContext()); // Number of columns, Dynamic choice
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); //2 or spanCount
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        int spacing = Math.round(4 * getResources().getDisplayMetrics().density);
        boolean includeEdge = true;
        mRecyclerView.addItemDecoration(new Utility.GridSpacingItemDecoration(2, spacing, includeEdge)); //2 or spanCount

        if (savedInstanceState != null && savedInstanceState.containsKey("Movies")) {
            Log.e(TAG, "savedInstanceState used.");
            mMovieAdapter.clearListForUpdate();
            mMovieAdapter.notifyDataSetChanged();
            currentList = savedInstanceState.getParcelableArrayList("Movies");
            mMovieAdapter.setmMovieList(currentList);
        } else {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        if (isConnected) {
            getLoaderManager().initLoader(TMDB_QUERY_LOADER, null, this);
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }

        //Spinner Setup
        mSpinner = (Spinner) findViewById(R.id.spinner);
        spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.custom_spinner_item, getResources().getStringArray(R.array.movie_sort_array));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        currentList = mMovieAdapter.getCurrentList();
        outState.putParcelableArrayList("Movies", currentList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        Log.e(TAG, "onCreateLoader Called()");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = sharedPrefs.getString(
                getString(R.string.settings_sort_by_key),
                getString(R.string.settings_sort_by_value_default));

        String releaseYear = sharedPrefs.getString(
                getString(R.string.settings_release_year_key),
                getString(R.string.settings_release_year_default));
        Log.e(TAG, "SortBy value used: " + sortBy);
        Log.e(TAG, "releaseYear value used: " + releaseYear);
        return new MovieLoader(this, sortBy, releaseYear);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        Log.e(TAG, "onLoadFinished Called()");
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        mMovieAdapter.clearListForUpdate();
        mMovieAdapter.notifyDataSetChanged();

        if (movies != null && !movies.isEmpty()) {
            mMovieAdapter.setmMovieList(new ArrayList<Movie>(movies));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        Log.e(TAG, "onLoadFinished Called()");
        mMovieAdapter.clearListForUpdate();
        mMovieAdapter.notifyDataSetChanged();
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     */
    @Override
    public void onClick(String title, Double averageRating, String releaseDate, String synopsis, String thumbnailPath) {
        Context context = this;
        Class destinationClass = MovieInfoActivity.class;
        Intent intentToStartMovieInfoActivity = new Intent(context, destinationClass);
        intentToStartMovieInfoActivity.putExtra("title", title);
        intentToStartMovieInfoActivity.putExtra("averageRating", Double.toString(averageRating));
        intentToStartMovieInfoActivity.putExtra("releaseDate", releaseDate);
        intentToStartMovieInfoActivity.putExtra("synopsis", synopsis);
        intentToStartMovieInfoActivity.putExtra("thumbnailPath", thumbnailPath);
        startActivity(intentToStartMovieInfoActivity);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (++check > 1) {
            int selectedItemIndex = i;
            String selectedItemText = mSpinner.getSelectedItem().toString().toLowerCase();

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            String sortByString;
            String releaseYearString;
            //Toast.makeText(context, "Spinner iTem: " + selectedItem, Toast.LENGTH_LONG).show();

            switch (selectedItemIndex) {
                case 0:
                    sortByString = getString(R.string.settings_sort_by_value_popular);
                    releaseYearString = getString(R.string.settings_release_year_latest);
                    break;
                case 1:
                    sortByString = getString(R.string.settings_sort_by_value_top_rated);
                    releaseYearString = getString(R.string.settings_release_year_latest);
                    break;
                case 2:
                    sortByString = getString(R.string.settings_sort_by_value_popular);
                    releaseYearString = getString(R.string.settings_release_year_all_time);
                    break;
                case 3:
                    sortByString = getString(R.string.settings_sort_by_value_top_rated);
                    releaseYearString = getString(R.string.settings_release_year_all_time);
                    break;
                default:
                    sortByString = getString(R.string.settings_sort_by_value_default);
                    releaseYearString = getString(R.string.settings_release_year_default);
                    break;
            }
            // Update Shared Preferences
            QueryUtils.modifyPreference(
                    context,
                    sharedPrefs,
                    sortByString,
                    releaseYearString);
            // TODO: Refresh the View
            mMovieAdapter.clearListForUpdate();
            refreshLoader();
            mMovieAdapter.notifyDataSetChanged();
            sharedPrefs.edit().putInt("sortType", mSpinner.getSelectedItemPosition()).apply();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public boolean refreshLoader() {
        LoaderManager loaderManager = getLoaderManager();
        Loader<String> tmdbSearchLoader = loaderManager.getLoader(TMDB_QUERY_LOADER);
        if (tmdbSearchLoader == null) {
            loaderManager.initLoader(TMDB_QUERY_LOADER, null, this);
        } else {
            loaderManager.restartLoader(TMDB_QUERY_LOADER, null, this);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPrefs != null) {
            int spinnerPosition = sharedPrefs.getInt("sortType", 0);
            mSpinner.setSelection(spinnerPosition);
        }
    }
}
