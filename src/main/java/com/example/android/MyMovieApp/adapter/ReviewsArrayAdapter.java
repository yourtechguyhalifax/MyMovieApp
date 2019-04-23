package com.example.android.MyMovieApp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.MyMovieApp.R;
import com.example.android.MyMovieApp.service.Review;

import java.util.ArrayList;

public class ReviewsArrayAdapter extends ArrayAdapter<Review>
{
    private Context mContext;
    private int mResource;
    ArrayList<Review> userReviews;
    private static final String TAG="ReviewsArrayAdapter";

    public ReviewsArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Review> objects) {
        super(context, 0, objects);
        mContext = context;
        mResource = resource;
        userReviews = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem==null){
            listItem = LayoutInflater.from(mContext).inflate(mResource,parent,false);
        }

        TextView userIdView = listItem.findViewById(R.id.userId);
        TextView reviewView = listItem.findViewById(R.id.reviewText);
        userIdView.setText(userReviews.get(position).getAuthor());
        reviewView.setText(userReviews.get(position).getContent());
        return listItem;
    }

    @Override
    public int getCount() {
        return userReviews.size();
    }

    public void setData(ArrayList<Review> data){
        this.userReviews = data;
    }
}
