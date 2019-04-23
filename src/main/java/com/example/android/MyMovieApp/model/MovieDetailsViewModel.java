package com.example.android.MyMovieApp.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.MyMovieApp.database.Movie;
import com.example.android.MyMovieApp.repository.MovieDetailsRepository;
import com.example.android.MyMovieApp.repository.StatusCodes;
import com.example.android.MyMovieApp.service.Review;
import com.example.android.MyMovieApp.service.Trailer;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsViewModel extends AndroidViewModel {
    public static final String TAG = "MovieDetailsViewModel";

    MovieDetailsRepository movieDetailsRepository;
    LiveData<List<Movie>> favoriteMovies;
    Application application;

    public MovieDetailsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        movieDetailsRepository = new MovieDetailsRepository(application);
        Log.d(TAG,"Constructor");
    }

    public LiveData<List<Movie>> getFavoriteMovies() {

        if(movieDetailsRepository==null){
            movieDetailsRepository = new MovieDetailsRepository(application);

        }
        favoriteMovies = movieDetailsRepository.getFavoritesMovies();
        return favoriteMovies;
    }

    //Below load methods are there to cause first or subsequent load of given data
    //Once it fetches data, it will load it in the LiveData object.
    public LiveData<StatusCodes> loadReviews(int id){
         return movieDetailsRepository.loadReviews(id);
    }

    public LiveData<StatusCodes> loadTrailers(int id){
         return movieDetailsRepository.loadTrailers(id);
    }

    //Below get methods will simply return reference to LiveData object. It will not cause any data load.
    public LiveData<ArrayList<Review>> getReviews(){
        return movieDetailsRepository.getReviews();
    }

    public LiveData<ArrayList<Trailer>> getTrailers(){ return movieDetailsRepository.getTrailers(); }

    //Below methods are database related.
    public void insertFavoriteMovie(Movie m){
        movieDetailsRepository.insertNewFavoriteMovie(m);
    }

    public void deleteFavoriteMovie(Movie m){
        movieDetailsRepository.deleteFavoriteMovie(m);
    }


}
