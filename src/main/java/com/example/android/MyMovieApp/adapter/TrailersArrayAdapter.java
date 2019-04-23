package com.example.android.MyMovieApp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.MyMovieApp.R;
import com.example.android.MyMovieApp.model.Helper;
import com.example.android.MyMovieApp.service.Trailer;

import java.util.ArrayList;

public class TrailersArrayAdapter extends ArrayAdapter<Trailer>
{
    private Context mContext;
    private int mResource;
    ArrayList<Trailer> trailers;


    public TrailersArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Trailer> objects) {
        super(context, 0, objects);
        mContext = context;
        mResource = resource;
        trailers = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem==null){
            listItem = LayoutInflater.from(mContext).inflate(mResource,parent,false);
        }

        TextView name = listItem.findViewById(R.id.name);
        name.setText(trailers.get(position).getName());
        return listItem;
    }

    @Override
    public int getCount() {
        return trailers.size();
    }

    public void setData(ArrayList<Trailer> data){
        this.trailers = data;
    }

    public Uri getWebUri(int position){
        //get the key first
        Trailer trailer = trailers.get(position);

        return Helper.getWebUri(trailer);

    }

    public Uri getAppUri(int position){
        //get the key first
        Trailer trailer = trailers.get(position);

        return Helper.getAppUri(trailer);


    }
}
