package com.eso.movies.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.eso.movies.R;
import com.eso.movies.model.Movie;
import com.eso.movies.model.MovieDBResponse;

import java.util.ArrayList;
import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private List<Movie> movieResults;
    private OnItemClickListener mClickListener;
    private boolean isLoadingAdded = false;

    public MovieAdapter(OnItemClickListener mClickListener) {
        this.movieResults = new ArrayList<>();
        this.mClickListener = mClickListener;
    }

    public List<Movie> getMovies() {
        return movieResults;
    }

    public void setMovies(List<Movie> movieResults) {
        this.movieResults = movieResults;
    }
    @Override
    public int getItemCount() {
        if (movieResults == null) return 0;
        return movieResults.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View view = inflater.inflate(R.layout.movie_list_item, parent, false);
                viewHolder = new myViewHolder(view);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.layout_item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Movie movie = movieResults.get(position);
        switch (getItemViewType(position)){
            case ITEM:
                final myViewHolder holder = (myViewHolder) viewHolder;
                holder.mTvTitle.setText(movie.getTitle());
                holder.mTvRating.setText(Double.toString(movie.getVoteAverage()));
                String imagePath = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
                Glide.with(holder.itemView.getContext()).load(imagePath)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.mProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.mProgress.setVisibility(View.GONE);
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                        .centerCrop()
                        .into(holder.mIvMovie);
                break;
            case LOADING:
//                Do nothing
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mIvMovie;
        TextView mTvTitle,mTvRating;
        ProgressBar mProgress;

        myViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mProgress = itemView.findViewById(R.id.movie_progress);
            mIvMovie = itemView.findViewById(R.id.movie_poster);
            mTvTitle = itemView.findViewById(R.id.movie_title);
            mTvRating = itemView.findViewById(R.id.movie_rate);

        }

        @Override
        public void onClick(View v) {
            mClickListener.onClick(getAdapterPosition());
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


    public interface OnItemClickListener {
        void onClick(int position);
    }

     /*
   Helpers
   _________________________________________________________________________________________________
    */
     public void add(Movie r) {
         movieResults.add(r);
         notifyItemInserted(movieResults.size() - 1);
     }

    public void addAll(List<Movie> moveResults) {
        for (Movie result : moveResults) {
            add(result);
        }
    }

    public void remove(Movie r) {
        int position = movieResults.indexOf(r);
        if (position > -1) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Movie());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResults.size() - 1;
        Movie result = getItem(position);

        if (result != null) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Movie getItem(int position) {
        return movieResults.get(position);
    }

}
