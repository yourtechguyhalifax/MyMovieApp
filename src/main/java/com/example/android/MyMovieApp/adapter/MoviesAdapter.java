package com.example.android.MyMovieApp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.MyMovieApp.R;
import com.example.android.MyMovieApp.database.Movie;
import com.example.android.MyMovieApp.model.MovieDetailsParcelable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private ArrayList<Movie> mDataset;
    private ItemClickListener clickListener;
    private Context context;
    public void setData(ArrayList<Movie> mDataset){
        this.mDataset = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MoviesAdapter.MoviesViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_image_item, parent, false);

        MoviesViewHolder vh = new MoviesViewHolder(v);
        context = parent.getContext();
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        // - get element from your dataset at this position


        Picasso.with(context)
                .load(mDataset.get(position).getImageURL())
                .into(holder.imageView);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataset==null)
            return 0;
        return mDataset.size();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public MoviesViewHolder(ImageView itemView) {
            super(itemView);
            imageView = itemView;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void clearData(){
        if(mDataset!=null)
         mDataset.clear();
    }

    public MovieDetailsParcelable getMovieDetailsParcelableAt(int position) {

        MovieDetailsParcelable movieDetailsParcelable = null;
        Movie m = mDataset.get(position);
        int id = m.getId();
        String title = m.getTitle();
        String imageURL = m.getImageURL();
        String plot = m.getPlot();
        String rating = String.valueOf(m.getRating());
        String releaseDate = m.getReleaseDate();


        movieDetailsParcelable = new MovieDetailsParcelable(id, title, releaseDate, rating, plot, imageURL);


        return movieDetailsParcelable;
    }
}
