package apirest;

import entities.HighScores;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;

public class RestRetrofit {

    public RestRetrofit() {

    }

    public Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://puzzledroid-2605b-default-rtdb.firebaseio.com/") //"https://wondergames-puzzledroid-default-rtdb.firebaseio.com/"
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();

    public interface GetTopScoresService {
        @GET("HighScores.json")
        Call<HighScores> listRepos();
    }
    public interface PostTopScoresService {
        @PATCH("HighScores.json")
        Call<HighScores> createPost(@Body HighScores hs);
    }
    public interface HighScoresApi{
        @GET("HighScores.json")
        Observable<HighScores> getHighScores();
    }

    public GetTopScoresService getTopScores = retrofit.create(GetTopScoresService.class);
    public PostTopScoresService postTopScores = retrofit.create(PostTopScoresService.class);

    public HighScoresApi highScoresApi = retrofit.create(HighScoresApi.class);

}
