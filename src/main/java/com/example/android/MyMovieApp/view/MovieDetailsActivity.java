package com.example.android.MyMovieApp.view;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.MyMovieApp.R;
import com.example.android.MyMovieApp.model.MovieDetailsParcelable;


public class MovieDetailsActivity extends AppCompatActivity {

   private static final String TAG="MovieDetailsActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Log.d(TAG,"onCreate");
        Intent intent = getIntent();
        MovieDetailsParcelable movieDetailsParcelable = intent.getParcelableExtra("selected_movie");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        FragmentManager fm = this.getSupportFragmentManager();
        MovieDetailsFragmentAdaptor adapter = new MovieDetailsFragmentAdaptor(fm,new String[]{getString(R.string.tab_one),
                getString(R.string.tab_two),
                getString(R.string.tab_three)});
        adapter.setMovieDetailsParcelable(movieDetailsParcelable);
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

    }


}
