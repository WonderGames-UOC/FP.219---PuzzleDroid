package apirest;

import java.util.List;

import entities.HighScores;
import entities.UserData;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public class RestRetrofit {

    public RestRetrofit() {

    }

    public Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://puzzledroid-2605b-default-rtdb.firebaseio.com/") //"https://wondergames-puzzledroid-default-rtdb.firebaseio.com/"
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();

    public interface UsersService {
        @GET("Users.json")
        Call<entities.Users> listRepos();
    }
    public interface UserService {
        @GET("Users/{id}.json")
        Call<List<UserData>> listRepos(@Path("id") String id);
    }
    public interface UserScoresService {
        @GET("Users/{id}/Scores.json")
        Call<List<entities.Scores>> listRepos(@Path("id") String id);
    }
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

    public UsersService usersSrv = retrofit.create(UsersService.class);
    public UserService userSrv = retrofit.create(UserService.class);
    public UserScoresService scores = retrofit.create(UserScoresService.class);
    public GetTopScoresService getTopScores = retrofit.create(GetTopScoresService.class);
    public PostTopScoresService postTopScores = retrofit.create(PostTopScoresService.class);

    public HighScoresApi highScoresApi = retrofit.create(HighScoresApi.class);

}
