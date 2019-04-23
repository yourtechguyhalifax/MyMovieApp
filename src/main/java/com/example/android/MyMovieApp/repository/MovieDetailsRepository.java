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
import com.example.android.MyMovieApp.service.MoviesService;
import com.example.android.MyMovieApp.service.RetrofitInstance;
import com.example.android.MyMovieApp.service.Review;
import com.example.android.MyMovieApp.service.ReviewsResponse;
import com.example.android.MyMovieApp.service.Trailer;
import com.example.android.MyMovieApp.service.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsRepository {
    private static final String API_KEY = BuildConfig.MoviesApiKey;
    public static final String TAG = "MovieDetailsRepository";

    private MovieDao movieDao;
    private AppDatabase appDatabase;
    private LiveData<List<Movie>> favoritesMovies;


    private MutableLiveData<ArrayList<Review>> reviewsFromResponse;
    private MutableLiveData<ArrayList<Trailer>> trailersFromResponse;

    private MoviesService moviesService;
    private int totalPages;
    //page parameters is for reviews call only
    private int page;

    //Below status will be updated asynchronously so they can't be returned from any method as such.
    //therefore, it is best to make them live data that can be observed for the change of value.
    private MutableLiveData<StatusCodes> trailers_status;
    private MutableLiveData<StatusCodes> reviews_status;

    public MovieDetailsRepository(Application application){


        //Get the database reference
        appDatabase = AppDatabase.getDatabase(application);
        movieDao = appDatabase.movieDao();
        favoritesMovies = movieDao.getAll();

        //Reviews stuff
        reviewsFromResponse = new MutableLiveData<ArrayList<Review>>();
        reviewsFromResponse.setValue(new ArrayList<Review>());
        this.page = 1;
        this.totalPages=1;
        //Trailers stuff
        trailersFromResponse = new MutableLiveData<>();
        trailersFromResponse.setValue(new ArrayList<Trailer>());

        //Initialise network status live data
        trailers_status = new MutableLiveData<>();
        reviews_status = new MutableLiveData<>();
    }


    ////Below methods are database related for favorites database
    public LiveData<List<Movie>> getFavoritesMovies() {
        return favoritesMovies;
    }

    public void insertNewFavoriteMovie(Movie m){
        new InsertAsyncTask(movieDao).execute(m);
    }

    public void deleteFavoriteMovie(Movie m){
        new DeleteAsyncTask(movieDao).execute(m);
    }

    private static class InsertAsyncTask extends AsyncTask<Movie,Void,Void>{
        private MovieDao movieDao;

        public InsertAsyncTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            movieDao.insert(movies[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG,"insertion done!");

        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Movie,Void,Void>{
        private MovieDao movieDao;

        public DeleteAsyncTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            movieDao.delete(movies[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG,"deletion done!");
        }
    }


    //Below methods are for retrieving data from the network
    private void loadReviewsFromNetwork(int id){

        Log.d(TAG,"loadReviewsFromNetwork:"+page);
        moviesService = RetrofitInstance.getService();
        Call<ReviewsResponse> callBackend;
        callBackend = moviesService.getReviews(id,API_KEY,page);

        Log.d(TAG,callBackend.request().url().toString());


        callBackend.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if(response.isSuccessful() && response.body().getResults().size()>0){
                    totalPages = response.body().getTotalPages();
                    Log.d(TAG, "Reviews Total Pages:" + totalPages);
                    Log.d(TAG, "Reviews Page:" + page+" Items:"+response.body().getResults().size());

                    ArrayList<Review> currentResult = reviewsFromResponse.getValue();
                    currentResult.addAll(response.body().getResults());
                    reviewsFromResponse.setValue(currentResult);
                    reviews_status.setValue(StatusCodes.SUCCESS);
                    page++;

                }else if(response.body().getResults().size()==0 && page==1){
                    reviews_status.setValue(StatusCodes.EMPTY);
                }else if(response.body().getResults().size()==0 && page>1){
                    reviews_status.setValue(StatusCodes.END_OF_DATA);
                }

            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                Log.d(TAG,"Network error:"+t.getMessage() );
                reviews_status.setValue(StatusCodes.NETWORK_FAILURE);
            }});
    }

    //Below methods are for retrieving data from the network
    private void loadTrailersFromNetwork(int id){

        Log.d(TAG,"loadTrailersFromNetwork:"+page);
        moviesService = RetrofitInstance.getService();
        Call<TrailerResponse> callBackend;
        callBackend = moviesService.getTrailers(id,API_KEY);

        Log.d(TAG,callBackend.request().url().toString());


        callBackend.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                Log.d(TAG, "Response:" + response.message());
                if(response.isSuccessful() && response.body().getResults().size()>0){
                    ArrayList<Trailer> currentResult = trailersFromResponse.getValue();
                    currentResult.addAll(response.body().getResults());
                    trailersFromResponse.setValue(currentResult);
                    trailers_status.setValue(StatusCodes.SUCCESS);
                }else if(response.body().getResults().size()==0){
                    trailers_status.setValue(StatusCodes.EMPTY);
                }

            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.d(TAG,"Network error:"+t.getMessage() );
                trailers_status.setValue(StatusCodes.NETWORK_FAILURE);
            }});
    }

    public LiveData<ArrayList<Review>> getReviews(){
        return reviewsFromResponse;
    }
    public LiveData<ArrayList<Trailer>> getTrailers(){
        return trailersFromResponse;
    }

    public LiveData<StatusCodes> loadReviews(int id){

        //No need to fetch empty response again in the same browsing session.
        if(reviews_status!=null && reviews_status.getValue()==StatusCodes.EMPTY) {
            return reviews_status;
        }

        if(page<=totalPages){
            loadReviewsFromNetwork(id);
        }else if(page > totalPages){
            reviews_status.setValue(StatusCodes.END_OF_DATA);
        }

        return reviews_status;
    }

    public LiveData<StatusCodes> loadTrailers(int id){

        //No need to fetch empty response again in the same browsing session.
        if(trailers_status!=null && trailers_status.getValue()==StatusCodes.EMPTY) {
            return trailers_status;
        }

        loadTrailersFromNetwork(id);
        return trailers_status;
    }
}
