package theo.tziomakas.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import theo.tziomakas.popularmovies.Adapters.MovieAdapter;
import theo.tziomakas.popularmovies.Api.ApiClient;
import theo.tziomakas.popularmovies.Api.ApiInterface;
import theo.tziomakas.popularmovies.Model.Movie;
import theo.tziomakas.popularmovies.Model.MoviesResponse;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MoviesAdapterOnClickHandler{

    public static final String POPULAR_MOVIES = "popular";
    public static final String TOP_RATED_MOVIES = "top_rated";
    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences sharedPrefs;
    private String sortingCriteria;
    private Call<MoviesResponse> call;
    ApiInterface apiService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();


    /******************************************************
     * Our UI elements the MoviesAdapter and the GridView *
     ******************************************************/
    private MovieAdapter adapter;
    private RecyclerView recyclerView;

    /****************************************************
     * This will be used for the adapter initialization *
     ****************************************************/


    private ArrayList<Movie> moviesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        moviesArrayList = new ArrayList<>();

        recyclerView = findViewById(R.id.movies_grid_view);

        adapter = new MovieAdapter(this,moviesArrayList, this);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sortingCriteria = sharedPrefs.getString(getString(R.string.pref_sorting_criteria_key), getString(R.string.pref_sorting_criteria_default_value));

            gettingPopularMovies();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void gettingTopRatedMovies(){

        /*
        Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.MOVIESDB_API_KEY);

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                List<Movie> movieList = response.body().getResults();
                recyclerView.setAdapter(adapter);
                adapter.setMoviesData(movieList);
                Log.d(TAG, "Number of top rated movies received: " + movieList.size());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                recyclerView.setAdapter(adapter);
                adapter.setMoviesData(moviesArrayList);
            }
        });

        */

        compositeDisposable.add(apiService.getTopRatedMoviesRxJava(BuildConfig.MOVIESDB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MoviesResponse>() {
                    @Override
                    public void accept(MoviesResponse moviesResponses) throws Exception {
                        List<Movie> movieList = moviesResponses.getResults();

                        recyclerView.setAdapter(adapter);
                        adapter.setMoviesData(movieList);
                        Log.d(TAG, "Number of top rated movies received: " + movieList.size());

                    }
                }));
    }

    private void gettingPopularMovies(){

        call = apiService.getPopularMovies(BuildConfig.MOVIESDB_API_KEY);

        /*
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                List<Movie> movieList = response.body().getResults();
                recyclerView.setAdapter(adapter);
                adapter.setMoviesData(movieList);

                Log.d(TAG, "Number of popular movies received: " + movieList.size());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
        */

        compositeDisposable.add(apiService.getPopularMoviesRxJava(BuildConfig.MOVIESDB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MoviesResponse>() {
                    @Override
                    public void accept(MoviesResponse moviesResponses) throws Exception {
                        List<Movie> movieList = moviesResponses.getResults();

                        recyclerView.setAdapter(adapter);
                        adapter.setMoviesData(movieList);
                        Log.d(TAG, "Number of popular movies received: " + movieList.size());

                    }
                }));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_settings){

            Context context = MainActivity.this;
            Class activityToBeLauched = SettingsActivity.class;

            Intent intent = new Intent(context,activityToBeLauched);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onClick(String movieId) {
        Intent i = new Intent(this,DetailsActivity.class);
        i.putExtra("movieId",movieId);
        startActivity(i);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("key", moviesArrayList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getParcelableArrayList("key");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // don't send events once the activity is destroyed
        compositeDisposable.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sortingCriteria = sharedPrefs.getString(getString(R.string.pref_sorting_criteria_key), getString(R.string.pref_sorting_criteria_default_value));

        if(sortingCriteria.equals("popular")) {

            gettingPopularMovies();

        }else if(sortingCriteria.equals("top_rated")){

            gettingTopRatedMovies();

        }
    }
}
