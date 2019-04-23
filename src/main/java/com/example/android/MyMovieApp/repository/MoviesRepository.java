package com.example.android.MyMovieApp.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.MyMovieApp.BuildConfig;
import com.example.android.MyMovieApp.database.AppDatabase;
import com.example.android.MyMovieApp.database.Movie;
import com.example.android.MyMovieApp.database.MovieDao;
import com.example.android.MyMovieApp.service.Movies;
import com.example.android.MyMovieApp.service.MoviesService;
import com.example.android.MyMovieApp.service.Result;
import com.example.android.MyMovieApp.service.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesRepository {
    private static final String API_KEY = BuildConfig.MoviesApiKey;
    public static final String TAG = "MoviesRepository";

    private MutableLiveData<ArrayList<Result>> movieItemsFromResponse;
    private MoviesService moviesService;
    private int page=1;
    private int totalPages=1;
    private MutableLiveData<StatusCodes> statusCodes;
    private LoadTypes loadType;

    //database stuff
    private MovieDao movieDao;
    private AppDatabase appDatabase;
    private LiveData<List<Movie>> favoritesMovies;
    private Application application;

    public MoviesRepository(Application application){
        movieItemsFromResponse = new MutableLiveData<ArrayList<Result>>();
        movieItemsFromResponse.setValue(new ArrayList<Result>());
        statusCodes = new MutableLiveData<>();
        this.page = 1;
        this.application = application;
    }

    //Below methods are for retrieving data from the network
    private void loadTitlesFromNetwork(LoadTypes loadType, int page){

        Log.d(TAG,"loadTitlesFromNetwork:"+page);
        moviesService = RetrofitInstance.getService();
        Call<Movies> callBackend;

        if(loadType == LoadTypes.HIGHEST_RATED){
            callBackend = moviesService.getMoviesByTopRating(API_KEY,page);

        }else{
            callBackend = moviesService.getMoviesByPopularity(API_KEY,page);
        }

        Log.d(TAG,callBackend.request().url().toString());


            callBackend.enqueue(new Callback<Movies>() {
                @Override
                public void onResponse(Call<Movies> call, Response<Movies> response) {
                    Log.d(TAG, "Response:" + response.message());
                    if(response.isSuccessful() && response.body().getResults().size()>0){
                        totalPages = response.body().getTotalPages();
                        ArrayList<Result> currentResult = movieItemsFromResponse.getValue();
                        currentResult.addAll(response.body().getResults());
                        movieItemsFromResponse.setValue(currentResult);
                        statusCodes.setValue(StatusCodes.SUCCESS);
                    }else if(response.isSuccessful() && response.body().getResults().size()==0){
                        statusCodes.setValue(StatusCodes.EMPTY);
                    }

                }

                @Override
                public void onFailure(Call<Movies> call, Throwable t) {
                    Log.d(TAG,"Network error:"+t.getMessage() );
                    statusCodes.setValue(StatusCodes.NETWORK_FAILURE);
                }});
    }

    public LiveData<ArrayList<Result>> getLiveDataObject(){
        return movieItemsFromResponse;
    }

    public LiveData<StatusCodes> loadData(){


        if (loadType == LoadTypes.FAVORITES) {

            loadFavoritesFromDatabase();
        }else{
            if(page<=totalPages){
                loadTitlesFromNetwork(loadType, page++);
            }else if(page > totalPages){
                statusCodes.setValue(StatusCodes.END_OF_DATA);
            }
        }

        return statusCodes;
    }

    private void loadFavoritesFromDatabase() {

        //Get the database reference
        if(appDatabase==null) {
            appDatabase = AppDatabase.getDatabase(application);
        }
        if (appDatabase!=null && movieDao == null) {
            movieDao = appDatabase.movieDao();
        }

        if(movieDao!=null){
            favoritesMovies = movieDao.getAll();
            //start the task to determine the count on favorites table and set the status accordingly
            DetermineCountAsyncTask d = new DetermineCountAsyncTask();
            d.execute();
        }

        //database doesn't exist. handle empty state here.
        //it will happen when there are no favorite movies initially
        if(appDatabase==null){
                statusCodes.setValue(StatusCodes.EMPTY);
        }

    }



    public void preferenceChanged(LoadTypes loadType){

        if(loadType == null){
            return;
        }
        //clear previously loaded data and initialise page parameters
        movieItemsFromResponse.getValue().clear();
        //set private member to new value of load type here
        this.loadType = loadType;
        //reset page no
        this.page = 1;
        this.totalPages = 1; // totalPages will change anyway depending on response but good to reset it here
        //call entry method for loading data now
        loadData();


    }

    //database methods
    ////Below methods are database related for favorites database
    public LiveData<List<Movie>> getFavoritesMovies() {
        return favoritesMovies;
    }

    public void setCurrentPreference(LoadTypes loadType){
        this.loadType = loadType;
    }


    private class DetermineCountAsyncTask extends AsyncTask<Void,Void,Integer>{
        @Override
        protected Integer doInBackground(Void... voids) {
            return movieDao.getCount();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer.intValue()==0){
                statusCodes.setValue(StatusCodes.EMPTY);
            }else{
                statusCodes.setValue(StatusCodes.SUCCESS);
            }
        }
    }

    public void setStatusCode(StatusCodes statusCode){
        this.statusCodes.setValue(statusCode);
    }
}
