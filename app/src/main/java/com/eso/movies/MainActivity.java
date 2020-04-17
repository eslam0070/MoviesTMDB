package com.eso.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.eso.movies.adapter.MovieAdapter;
import com.eso.movies.model.MovieDBResponse;
import com.eso.movies.model.Movie;
import com.eso.movies.service.RetrofitClient;
import com.eso.movies.util.PaginationScrollListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener{

    MovieAdapter adapter;
    List<Movie> movieList = new ArrayList<>();
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 500;
    private int currentPage = PAGE_START;
    ProgressBar progressBar;
    RecyclerView rv;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Popular Movies Today");
        rv = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.main_progress);
        adapter = new MovieAdapter(this);
        linearLayoutManager = new GridLayoutManager(this, 2);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        loadFirstPage();
    }

    private void loadFirstPage() {
        RetrofitClient.getInstance().getApi()
                .getPopularMovies(getString(R.string.app_key),"en_US",currentPage).enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(@NotNull Call<MovieDBResponse> call, @NotNull Response<MovieDBResponse> response) {
                movieList = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                adapter.addAll(movieList);
                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(@NotNull Call<MovieDBResponse> call, @NotNull Throwable t) {

            }
        });
    }

    private void loadNextPage() {
        RetrofitClient.getInstance().getApi().getPopularMovies
                (getString(R.string.app_key),"en_US",currentPage)
                .enqueue(new Callback<MovieDBResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<MovieDBResponse> call, @NotNull Response<MovieDBResponse> response) {
                        adapter.removeLoadingFooter();
                        isLoading = false;
                        movieList = fetchResults(response);
                        adapter.addAll(movieList);
                        if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                        else isLastPage = true;
                    }

                    @Override
                    public void onFailure(@NotNull Call<MovieDBResponse> call, @NotNull Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private List<Movie> fetchResults(Response<MovieDBResponse> response) {
        MovieDBResponse topRatedMovies = response.body();
        return topRatedMovies.getMovies();
    }

    @Override
    public void onClick(int position) {
        if (position != RecyclerView.NO_POSITION){
            Movie selectedMovie = movieList.get(position);
            Intent intent=new Intent(this, DetailsActivity.class);
            intent.putExtra("movie",selectedMovie);
            startActivity(intent);
        }
    }
}
