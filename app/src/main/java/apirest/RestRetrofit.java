package apirest;

import java.util.List;

import entities.HighScores;
import entities.UserData;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public class RestRetrofit {
    static OkHttpClient okHttpClient = new OkHttpClient();
    static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl("https://puzzledroid-2605b-default-rtdb.firebaseio.com/") //"https://wondergames-puzzledroid-default-rtdb.firebaseio.com/"
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient);

    static Retrofit retrofit = retrofitBuilder.build();


    public interface UsersService {
        @GET("Users.json")
        Call<entities.Users> listRepos();
    }
    interface UserService {
        @GET("Users/{id}.json")
        Call<List<UserData>> listRepos(@Path("id") String id);
    }
    interface UserScoresService {
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

    public static UsersService usersSrv = retrofit.create(UsersService.class);
    static UserService userSrv = retrofit.create(UserService.class);
    static UserScoresService scores = retrofit.create(UserScoresService.class);
    public static GetTopScoresService getTopScores = retrofit.create(GetTopScoresService.class);
    public static PostTopScoresService postTopScores = retrofit.create(PostTopScoresService.class);

}
