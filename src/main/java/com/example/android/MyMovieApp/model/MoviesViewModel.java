package com.example.android.MyMovieApp.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.MyMovieApp.database.Movie;
import com.example.android.MyMovieApp.repository.LoadTypes;
import com.example.android.MyMovieApp.repository.MoviesRepository;
import com.example.android.MyMovieApp.repository.StatusCodes;
import com.example.android.MyMovieApp.service.Result;

import java.util.ArrayList;
import java.util.List;

public class MoviesViewModel extends AndroidViewModel{

    MoviesRepository moviesRepository;

    public MoviesViewModel(@NonNull Application application) {
        super(application);
        moviesRepository = new MoviesRepository(application);
    }

    public LiveData<ArrayList<Result>> getLiveDataObject(){
        return moviesRepository.getLiveDataObject();
    }

    public LiveData<StatusCodes> loadData(){
        return moviesRepository.loadData();
    }

    public void notifyPreferenceChanged(LoadTypes loadType){
        moviesRepository.preferenceChanged(loadType);

    }

    //database stuff
    public LiveData<List<Movie>> getFavoriteMovies() {
        return moviesRepository.getFavoritesMovies();
    }

    public void setCurrentPreference(LoadTypes loadType){
        moviesRepository.setCurrentPreference(loadType);
    }

    //expose the method to set data fetch status here as for database, activity can identify empty status through observers
    public void setStatusCode(StatusCodes statusCode){
        moviesRepository.setStatusCode(statusCode);
    }
}
