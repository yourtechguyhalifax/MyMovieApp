package com.example.android.MyMovieApp.model;

import android.content.Context;
import android.net.Uri;

import com.example.android.MyMovieApp.R;
import com.example.android.MyMovieApp.database.Movie;
import com.example.android.MyMovieApp.repository.LoadTypes;
import com.example.android.MyMovieApp.service.Result;
import com.example.android.MyMovieApp.service.Trailer;

import java.util.ArrayList;

public class Helper {

    public static LoadTypes convertValueToLoadType(Context context, String value){
        LoadTypes loadType=null;

        if(value.equalsIgnoreCase(context.getString(R.string.highest_rated_value))){
            loadType = LoadTypes.HIGHEST_RATED;
        }else if(value.equalsIgnoreCase(context.getString(R.string.most_popular_value))){
            loadType = LoadTypes.MOST_POPULAR;

        }else if(value.equalsIgnoreCase(context.getString(R.string.favorites_value))){
            loadType = LoadTypes.FAVORITES;
        }

        return loadType;
    }

    public static ArrayList<Movie> convertResultToMovie(ArrayList<Result> results){
        ArrayList<Movie> movies = new ArrayList<>();
        Movie m;

        for(Result r:results){
            m = new Movie();

            movies.add(convertResultToMovie(r));
        }

        return movies;
    }

    public static Movie convertResultToMovie(Result r){
        Movie m;

        m = new Movie();
        m.setId(r.getId());
        m.setImageURL(r.getAbsolutePosterPath());
        m.setPlot(r.getOverview());
        m.setRating(String.valueOf(r.getVoteAverage()));
        m.setReleaseDate(r.getReleaseDate());
        m.setTitle(r.getTitle());


        return m;
    }



    public static Uri getWebUri(Trailer trailer){
        //get the key first
        String key = trailer.getKey();
        String site = trailer.getSite();
        //Check if site is youtube
        if(!key.isEmpty() && !site.isEmpty() && site.equalsIgnoreCase("youtube")){
            //Build Uri as https://www.youtube.com/watch?v="SUXWAEX2jlg
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("www.youtube.com")
                    .appendPath("watch")
                    .appendQueryParameter("v",key);
            return builder.build();
        }else{
            return null;
        }

    }

    public static Uri getAppUri(Trailer trailer){
        //get the key first
        String key = trailer.getKey();
        String site = trailer.getSite();
        //Check if site is youtube
        if(!key.isEmpty() && !site.isEmpty() && site.equalsIgnoreCase("youtube")){
            //Build Uri as Uri.parse( "vnd.youtube:" + videoId));

            return Uri.parse("vnd.youtube:" +key);
        }else{
            return null;
        }

    }
}
