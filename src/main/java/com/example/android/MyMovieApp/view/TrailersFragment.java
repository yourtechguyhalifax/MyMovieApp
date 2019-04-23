package com.example.android.MyMovieApp.view;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.MyMovieApp.R;
import com.example.android.MyMovieApp.adapter.TrailersArrayAdapter;
import com.example.android.MyMovieApp.model.MovieDetailsParcelable;
import com.example.android.MyMovieApp.model.MovieDetailsViewModel;
import com.example.android.MyMovieApp.repository.StatusCodes;
import com.example.android.MyMovieApp.service.Trailer;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrailersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrailersFragment extends Fragment {

    private static final String TAG="TrailersFragment";

    private static final String MOVIE_DETAILS = "movie_details";

    private static MovieDetailsParcelable movieDetailsParcelable;
    MovieDetailsViewModel movieDetailsViewModel;
    TrailersArrayAdapter trailersAdapter;
    ListView listView;
    TextView textView;
    ProgressBar progressBar;
    int id;

    private ArrayList<Trailer> trailersList;



    public TrailersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrailersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrailersFragment newInstance(MovieDetailsParcelable movieDetailsParcelable) {
        TrailersFragment fragment = new TrailersFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE_DETAILS, movieDetailsParcelable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieDetailsParcelable = getArguments().getParcelable(MOVIE_DETAILS);

            id = movieDetailsParcelable.getId();

            trailersList = new ArrayList<>();
            trailersAdapter = new TrailersArrayAdapter(getContext(), R.layout.trailer_item, trailersList);

            //find view model
            movieDetailsViewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_view, container, false);

        listView = (ListView) view.findViewById(R.id.list);
        textView = (TextView) view.findViewById(R.id.message);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        listView.setAdapter(trailersAdapter);
        movieDetailsViewModel.loadTrailers(id).observe(this, new Observer<StatusCodes>() {
            @Override
            public void onChanged(@Nullable StatusCodes statusCodes) {
                showStatusMessage(statusCodes);
            }
        });

        progressBar.setVisibility(View.VISIBLE);


        movieDetailsViewModel.getTrailers().observe(this, new Observer<ArrayList<Trailer>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Trailer> trailers) {
                trailersList.addAll(trailers);
                trailersAdapter.setData(trailers);
                trailersAdapter.notifyDataSetChanged();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent appIntent = new Intent(Intent.ACTION_VIEW, trailersAdapter.getAppUri(position) );
                Intent webIntent = new Intent(Intent.ACTION_VIEW, trailersAdapter.getWebUri(position));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });
        return view;
    }

    private void showStatusMessage(StatusCodes statusCodes){
        switch(statusCodes){
            case SUCCESS:
                textView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                break;
            case EMPTY:
                textView.setText(R.string.trailers_empty_message);
                textView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                break;
            case NETWORK_FAILURE:
                textView.setText(R.string.network_error);
                textView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                break;
        }
        progressBar.setVisibility(View.GONE);

    }
}
