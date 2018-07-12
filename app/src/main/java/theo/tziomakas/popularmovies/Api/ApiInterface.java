package theo.tziomakas.popularmovies.Api;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import theo.tziomakas.popularmovies.Model.Movie;
import theo.tziomakas.popularmovies.Model.MoviesResponse;

public interface ApiInterface {



    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);


    /*
    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);
    */
    @GET("movie/popular")
    Observable<List<Movie>> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<MoviesResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
}
