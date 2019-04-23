package com.example.android.MyMovieApp.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.MyMovieApp.R;
import com.example.android.MyMovieApp.adapter.ItemClickListener;
import com.example.android.MyMovieApp.adapter.MoviesAdapter;
import com.example.android.MyMovieApp.database.Movie;
import com.example.android.MyMovieApp.model.Helper;
import com.example.android.MyMovieApp.model.MovieDetailsParcelable;
import com.example.android.MyMovieApp.model.MoviesViewModel;
import com.example.android.MyMovieApp.repository.LoadTypes;
import com.example.android.MyMovieApp.repository.StatusCodes;
import com.example.android.MyMovieApp.service.Result;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private MoviesViewModel viewModel;
    private TextView textView;
    private ProgressBar progressBar;
    String sort_order;
    private ObserveNetworkData observeNetworkData;
    private ObserveDatabaseData observeDatabaseData;
    private LoadTypes loadType;
    private NetworkDataScrollListener networkScrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialised observers
        observeNetworkData = new ObserveNetworkData();
        observeDatabaseData = new ObserveDatabaseData();
        networkScrollListener = new NetworkDataScrollListener();

        mRecyclerView = (RecyclerView) findViewById(R.id.movie_recycle_view);
        textView = (TextView) findViewById(R.id.message);
        textView.setTextSize(20);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(16,16,16,16);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(this);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            mLayoutManager = new GridLayoutManager(this,2);

        }else{
            mLayoutManager = new GridLayoutManager(this,3);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MoviesAdapter();
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        // specify an adapter (see also next example)
        viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);

        //initialise loadType here from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        String currentValue = sharedPreferences.getString(getString(R.string.sort_order_key),
                getString(R.string.most_popular_value));

        loadType = Helper.convertValueToLoadType(getApplicationContext(), currentValue);
        viewModel.setCurrentPreference(loadType);
        //Below method starts the first load when main activity is created
        progressBar.setVisibility(View.VISIBLE);
        viewModel.loadData().observe(this, new Observer<StatusCodes>() {
            @Override
            public void onChanged(@Nullable StatusCodes statusCodes) {
                showStatusMessage(statusCodes);
            }
        });
        signUpForObserver();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onClick(View view, int position) {
        Intent i = new Intent(this, MovieDetailsActivity.class);
        MovieDetailsParcelable movieDetailsParcelable = mAdapter.getMovieDetailsParcelableAt(position);
        i.putExtra("selected_movie", movieDetailsParcelable);
        startActivity(i);
    }

    private void showStatusMessage(StatusCodes statusCodes){
        switch(statusCodes){
            case SUCCESS:
                textView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                break;
            case EMPTY:
                if(loadType == LoadTypes.FAVORITES) {

                    textView.setText(R.string.database_empty_message);
                }else{
                    textView.setText(R.string.network_empty_message);
                }

                textView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                break;
            case NETWORK_FAILURE:
                textView.setText(R.string.network_error);
                textView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                break;
        }
        //hide progress bar when status code is updated
        progressBar.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Intent i = new Intent(this,SettingsActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(getString(R.string.sort_order_key))){

            //clear adapter data to ensure duplication
            mAdapter.clearData();

            //remove observers
            if(viewModel.getLiveDataObject().hasObservers()){
                viewModel.getLiveDataObject().removeObservers(this);
            }
            if(viewModel.getFavoriteMovies()!=null && viewModel.getFavoriteMovies().hasObservers()){
                viewModel.getFavoriteMovies().removeObservers(this);
            }
            //get new sort order from shared preferences
            sort_order = sharedPreferences.getString(getString(R.string.sort_order_key),getString(R.string.most_popular_value));
            loadType = Helper.convertValueToLoadType(getApplicationContext(), sort_order);

            textView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            viewModel.notifyPreferenceChanged(loadType);

            signUpForObserver();
        }
    }

    private void signUpForObserver(){

        //sign up for live data based on preferences
        if(loadType == LoadTypes.FAVORITES){
            viewModel.getFavoriteMovies().observe(this,observeDatabaseData);
            //remove scroll listener for pagination
            mRecyclerView.removeOnScrollListener(networkScrollListener);
        }else{
            viewModel.getLiveDataObject().observe(this,observeNetworkData);
            mRecyclerView.addOnScrollListener(networkScrollListener);
        }
    }

    private class ObserveNetworkData implements Observer<ArrayList<Result>>{
        @Override
        public void onChanged(@Nullable ArrayList<Result> results) {
            if(results!=null && results.size()>0){
                mAdapter.setData(Helper.convertResultToMovie(results));
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class ObserveDatabaseData implements Observer<List<Movie>>{
        @Override
        public void onChanged(@Nullable List<Movie> movies) {
            if(movies!=null && movies.size()>0){
                mAdapter.setData((ArrayList<Movie>) movies);
                mAdapter.notifyDataSetChanged();
            }else if(movies.size()==0){
               mAdapter.clearData();
               mAdapter.notifyDataSetChanged();
               viewModel.setStatusCode(StatusCodes.EMPTY);
            }
        }
    }

    private class NetworkDataScrollListener extends RecyclerView.OnScrollListener {
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                && firstVisibleItemPosition >= 0
                ) {
            progressBar.setVisibility(View.VISIBLE);
            viewModel.loadData();
            Log.d(TAG,"First visible Item:"+firstVisibleItemPosition+"  Total Item count:"+totalItemCount+ " Visible Item count:"+visibleItemCount);
        }
    }
}
}

