package com.example.android.MyMovieApp.view;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.MyMovieApp.R;
import com.example.android.MyMovieApp.adapter.ReviewsArrayAdapter;
import com.example.android.MyMovieApp.model.MovieDetailsParcelable;
import com.example.android.MyMovieApp.model.MovieDetailsViewModel;
import com.example.android.MyMovieApp.repository.StatusCodes;
import com.example.android.MyMovieApp.service.Review;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment {
    private static final String TAG="ReviewsFragment";

    private static final String MOVIE_DETAILS = "movie_details";

    private static MovieDetailsParcelable movieDetailsParcelable;
    MovieDetailsViewModel movieDetailsViewModel;
    ReviewsArrayAdapter userReviewAdapter;
    ListView listView;
    int id;
    TextView textView;
    ProgressBar progressBar;

    private ArrayList<Review> reviewsList;
    private StatusCodes statusCodes;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance() {
        return newInstance();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment ReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance(MovieDetailsParcelable movieDetailsParcelable) {
        ReviewsFragment fragment = new ReviewsFragment();
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

            id = movieDetailsParcelable.getId();

            reviewsList = new ArrayList<>();
            userReviewAdapter = new ReviewsArrayAdapter(getContext(), R.layout.review_item, reviewsList);

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

        listView.setAdapter(userReviewAdapter);

        progressBar.setVisibility(View.VISIBLE);
        movieDetailsViewModel.loadReviews(id).observe(this, new Observer<StatusCodes>() {
            @Override
            public void onChanged(@Nullable StatusCodes statusCodes) {
                showStatusMessage(statusCodes);
            }
        });

        movieDetailsViewModel.getReviews().observe(this, new Observer<ArrayList<Review>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Review> reviews) {
                reviewsList.addAll(reviews);
                userReviewAdapter.setData(reviews);
                userReviewAdapter.notifyDataSetChanged();

            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if((firstVisibleItem+visibleItemCount) == totalItemCount && totalItemCount!=0){
                    Log.d(TAG, "First Visible item:" + firstVisibleItem+ " VisibleItemCount:" + visibleItemCount + " TotalITemCount:" + totalItemCount);
                    movieDetailsViewModel.loadReviews(id);
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
                textView.setText(R.string.reviews_empty_message);
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

        //also set the class level status code here for onResume to refer back to
        this.statusCodes = statusCodes;

    }


}
