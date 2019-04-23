package com.example.android.MyMovieApp.view;

import android.preference.PreferenceManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.content.SharedPreferences;
import com.example.android.MyMovieApp.R;
import com.example.android.MyMovieApp.database.Movie;
import com.example.android.MyMovieApp.databinding.MovieOverviewBinding;
import com.example.android.MyMovieApp.model.Helper;
import com.example.android.MyMovieApp.model.MovieDetailsParcelable;
import com.example.android.MyMovieApp.model.MovieDetailsViewModel;
import com.example.android.MyMovieApp.service.Trailer;
import java.util.ArrayList;
import java.util.List;


public class OverviewFragment extends Fragment implements OnRatingBarChangeListener{

    private static final String TAG="OverviewFragment";
    private static final String MOVIE_DETAILS = "movie_details";
    private MovieDetailsParcelable movieDetailsParcelable;
    ImageButton imageButton;
    MovieDetailsViewModel movieDetailsViewModel;
    Movie movieEntity;
    SharedPreferences wmbPreference1;
    SharedPreferences.Editor editor;
    List<Movie> latestFavoriteMovies;
    RatingBar ratingBar;
    String firstTrailer;

    public OverviewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(MovieDetailsParcelable movieDetailsParcelable) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE_DETAILS, movieDetailsParcelable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieDetailsParcelable = getArguments().getParcelable(MOVIE_DETAILS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MovieOverviewBinding bindings = MovieOverviewBinding.inflate(inflater,container,false);
        final View view = bindings.getRoot();
        wmbPreference1 = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        imageButton = view.findViewById(R.id.favoriteButton);
        ratingBar= view.findViewById(R.id.ratingBar);

        //find view model
        movieDetailsViewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);

        //build movie entity object
        movieEntity = new Movie();
        movieEntity.setId(movieDetailsParcelable.getId());
        movieEntity.setTitle(movieDetailsParcelable.getTitle());
        movieEntity.setImageURL(movieDetailsParcelable.getImageURL());
        movieEntity.setPlot(movieDetailsParcelable.getPlot());
        movieEntity.setRating(String.valueOf(ratingBar.getRating()));
        movieEntity.setReleaseDate(movieDetailsParcelable.getReleaseDate());

        //Bind data to view here
        bindings.setMovie(movieEntity);
        float rating = wmbPreference1.getFloat(String.valueOf(movieEntity.getId()), 0f);
        ratingBar.setRating(rating);
        movieDetailsViewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                 updateFavoriteStatus(movies,movieEntity);
                 latestFavoriteMovies = movies;
            }
        });
        movieDetailsViewModel.loadTrailers(movieEntity.getId());

        // Set ChangeListener to Rating Bar
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                editor = wmbPreference1.edit();
                editor.putFloat(String.valueOf(movieEntity.getId()), rating);
                editor.commit();
            }
        });

        //handle favorite button
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFavorite(latestFavoriteMovies,movieEntity)){
                    Log.d(TAG, "Delete:"+movieEntity.getTitle());
                    movieDetailsViewModel.deleteFavoriteMovie(movieEntity);
                }else{
                    Log.d(TAG, "Insert:"+movieEntity.getTitle());
                    movieDetailsViewModel.insertFavoriteMovie(movieEntity);
                }
            }
        });

        movieDetailsViewModel.getTrailers().observe(this, new Observer<ArrayList<Trailer>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Trailer> trailers) {
                if(trailers!=null && trailers.size()>0){
                    firstTrailer = Helper.getWebUri(trailers.get(0)).toString();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean isFavorite(List<Movie> movies, Movie m){
        boolean flag=false;

        if(movies.contains(m)){
            flag=true;
        }else{
            flag=false;
        }
        return flag;
    }

    //check the status of favorite and show the star image accordingly
    private void updateFavoriteStatus(List<Movie> movies, Movie m){
        if(isFavorite(movies, m)){
            imageButton.setImageResource(R.drawable.ic_star_black_24dp);
        }else{
            imageButton.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

    }
}
