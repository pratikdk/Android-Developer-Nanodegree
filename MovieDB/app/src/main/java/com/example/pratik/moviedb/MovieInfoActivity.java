package com.example.pratik.moviedb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MovieInfoActivity extends AppCompatActivity {
    private static final String TAG = MovieInfoActivity.class.getSimpleName();
    private static Context context;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private String posterPath;
    private String title;
    private String averageRating;
    private String releaseDate;
    private String overview;

    private ImageView infoBackdropImageView;
    private ImageView thumbnailImageView;
    private TextView mainTitleTextView;
    private TextView averageRatingTextView;
    private TextView releaseDateTextView;
    private TextView overviewTextView;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Bitmap blurredBitmap = Utility.blur(getAppContext(), bitmap);
            infoBackdropImageView.setImageDrawable(new BitmapDrawable(getResources(), blurredBitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        MovieInfoActivity.context = getApplicationContext();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intentThatStartedThisActivity = getIntent();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);

        infoBackdropImageView = (ImageView) findViewById(R.id.main_backdrop);
        thumbnailImageView = (ImageView) findViewById(R.id.movie_info_main_thumbnail);
        mainTitleTextView = (TextView) findViewById(R.id.movie_info_main_title);
        averageRatingTextView = (TextView) findViewById(R.id.movie_info_main_rating_value);
        releaseDateTextView = (TextView) findViewById(R.id.movie_info_main_date_value);
        overviewTextView = (TextView) findViewById(R.id.movie_info_main_info);

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("title")) {
                posterPath = intentThatStartedThisActivity.getStringExtra("thumbnailPath");
                title = intentThatStartedThisActivity.getStringExtra("title");
                averageRating = intentThatStartedThisActivity.getStringExtra("averageRating");
                releaseDate = intentThatStartedThisActivity.getStringExtra("releaseDate");
                overview = intentThatStartedThisActivity.getStringExtra("synopsis");

                collapsingToolbarLayout.setTitle(title);

                mainTitleTextView.setText(title);
                averageRatingTextView.setText(averageRating);
                releaseDateTextView.setText(releaseDate);
                overviewTextView.setText(overview);

                String fullPosterPath = Utility.generateUrlString(posterPath);
                // Main Thumbnail
                Picasso.with(context)
                        .load(fullPosterPath)
                        .into(thumbnailImageView);
                // Backdrop Thumbnail
                Picasso.with(context)
                        .load(fullPosterPath)
                        .into(target);
            }
        }
    }

    public static Context getAppContext() {
        return MovieInfoActivity.context;
    }

    @Override
    public void onDestroy() {
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }
}
