package com.eso.movies.service;

import com.eso.movies.model.MovieDBResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieInterface {

    @GET("movie/popular")
    Call<MovieDBResponse> getPopularMovies(@Query("api_key") String apiKey,@Query("language") String language,
                                           @Query("page") int pageIndex);
}
