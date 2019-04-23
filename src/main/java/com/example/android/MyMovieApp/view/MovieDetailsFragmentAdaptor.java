package com.example.android.MyMovieApp.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.MyMovieApp.model.MovieDetailsParcelable;

public class MovieDetailsFragmentAdaptor extends FragmentPagerAdapter {

    private String title[];

    private MovieDetailsParcelable movieDetailsParcelable;

    public MovieDetailsFragmentAdaptor(FragmentManager fm, String[] title) {
        super(fm);
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch(position){
            case 0:
                f = OverviewFragment.newInstance(movieDetailsParcelable);
                break;
            case 1:
                f = TrailersFragment.newInstance(movieDetailsParcelable);
                break;
            case 2:
                f = ReviewsFragment.newInstance(movieDetailsParcelable);
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    public void setMovieDetailsParcelable(MovieDetailsParcelable movieDetailsParcelable){
        this.movieDetailsParcelable = movieDetailsParcelable;

    }
}
