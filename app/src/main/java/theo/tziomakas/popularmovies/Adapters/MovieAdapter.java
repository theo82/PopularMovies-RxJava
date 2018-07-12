package theo.tziomakas.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import theo.tziomakas.popularmovies.Model.MoviesResponse;
import theo.tziomakas.popularmovies.R;
import theo.tziomakas.popularmovies.Model.Movie;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MoviesViewHolder>{

    public static final String POSTER_PATH = "http://image.tmdb.org/t/p/w185//";
    private Context context;
    private List<Movie> movieList;

    final private MoviesAdapterOnClickHandler mClickHandler;



    public interface MoviesAdapterOnClickHandler{
        void onClick(String movieId);
    }

    public MovieAdapter(Context context, List<Movie> movieList, MoviesAdapterOnClickHandler mClickHandler){
        this.context = context;
        this.movieList = movieList;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movies_row;
        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttactToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem,parent,shouldAttactToParentImmediately);


        MoviesViewHolder moviesViewHolder = new MoviesViewHolder(view);

        return moviesViewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {


        String posterPath = POSTER_PATH + movieList.get(position).getPosterPath();

        Picasso.with(context)
                .load(posterPath)
                .resize(506, 759)
                .centerCrop()
                .into(holder.mMovieImage);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void setMoviesData(List<Movie> moviesList){
        this.movieList = moviesList;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = this.movieList.size();
        this.movieList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mMovieImage;

        public MoviesViewHolder(View itemView) {
            super(itemView);

            mMovieImage = itemView.findViewById(R.id.movieImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String movieId = String.valueOf(movieList.get(adapterPosition).getId());
            Log.v("MovieAdapter", String.valueOf(movieId));
            mClickHandler.onClick(movieId);
        }
    }
}
