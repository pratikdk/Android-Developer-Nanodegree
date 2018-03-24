package com.example.pratik.moviedb;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pratik on 17-03-2018.
 */

public class Movie implements Parcelable {
    private String mTitle;
    private Double mAverageRating;
    private String mReleaseDate;
    private String mSynopsis;
    private String mThumbnailPath;

    public Movie(String mTitle, Double mAverageRating, String mReleaseDate, String mSynopsis, String mThumbnailPath) {
        this.mTitle = mTitle;
        this.mAverageRating = mAverageRating;
        this.mReleaseDate = mReleaseDate;
        this.mSynopsis = mSynopsis;
        this.mThumbnailPath = mThumbnailPath;
    }

    private Movie(Parcel in) {
        mTitle = in.readString();
        mAverageRating = in.readDouble();
        mReleaseDate = in.readString();
        mSynopsis = in.readString();
        mThumbnailPath = in.readString();
    }

    public String getmTitle() {
        return mTitle;
    }

    public Double getmAverageRating() {
        return mAverageRating;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public String getmSynopsis() {
        return mSynopsis;
    }

    public String getmThumbnailPath() {
        return mThumbnailPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeDouble(mAverageRating);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mSynopsis);
        parcel.writeString(mThumbnailPath);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
