package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.puzzledroid.MainActivity;
import com.example.puzzledroid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.HashMap;

import apirest.RestRetrofit;
import entities.HighScores;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnlineScores extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private Context context = this;
    private Disposable disposable;
    Button back_button;

    //ACTIVITY METHODS onCreate ... on Destroy
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_scores);
        query();

        //Button onClickListener
        back_button = findViewById(R.id.OnlineScores_button_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToMainActivity();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //Dispose the observable
        try{
            disposable.dispose();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }


    //Application logic.///
    public void goBackToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    //This methods set an observable and a subscriber to the firebase realtime database path HighScores
    private void query(){
        RestRetrofit retrofit = new RestRetrofit();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("HighScores");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                disposable = retrofit.highScoresApi.getHighScores()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<HighScores>() {
                            @Override
                            public void accept(HighScores highScores) throws Exception {
                                createTextLayout(highScores);
                            }
                        }, new Consumer<Throwable>(){
                            @Override
                            public void accept(Throwable throwable) throws  Exception{
                                Log.e(TAG, "query: error fetching data. Observable.");
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }

    private void queryDb(){
        Log.d(TAG, "queryDb fn");
        RestRetrofit retrofit = new RestRetrofit();
        try{
            Call<HighScores> getTopScores = retrofit.getTopScores.listRepos();
            getTopScores.enqueue(new Callback<HighScores>() {
                @Override
                public void onResponse(Call<entities.HighScores> call, Response<HighScores> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG,
                                "\nMessage: " + response.message().toString()
                                        + "\nHeaders: " + response.headers()
                                        + "\nResponse: " + response.toString()
                                        + "\nSize: " + response.body()
                        );
                        entities.HighScores hs = response.body();
                        Log.d(TAG,
                                "\nTop1: " + hs.Top1
                                        +"\nTop2: " + hs.Top2
                                        +"\nTop3: " + hs.Top3
                                        +"\nTop4: " + hs.Top4
                                        +"\nTop5: " + hs.Top5
                                        +"\nTop6: " + hs.Top6
                                        +"\nTop7: " + hs.Top7
                                        +"\nTop8: " + hs.Top8
                                        +"\nTop9: " + hs.Top9
                                        +"\nTop10: " + hs.Top10
                        );
                        String scores = "";
                        new AlertDialog.Builder(context)
                                .setTitle("Online Top 10 Scores")
                                .setMessage(
                                        "\t1: " + hs.Top1
                                                +"\n\t2: " + hs.Top2
                                                +"\n\t3: " + hs.Top3
                                                +"\n\t4: " + hs.Top4
                                                +"\n\t5: " + hs.Top5
                                                +"\n\t6: " + hs.Top6
                                                +"\n\t7: " + hs.Top7
                                                +"\n\t8: " + hs.Top8
                                                +"\n\t9: " + hs.Top9
                                                +"\n\t10: " + hs.Top10)
                                .setIcon(R.drawable.puzzledroid_icon)
                                .setNegativeButton("OK", null)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<entities.HighScores> call, Throwable t) {
                    Log.e(TAG, "Error: " + t.getMessage());
                    new AlertDialog.Builder(context)
                            .setTitle("Online Top 10 Scores")
                            .setMessage(
                                    "Error gathering data")
                            .setIcon(R.drawable.puzzledroid_icon)
                            .setNegativeButton("OK", null)
                            .show();
                }
            });
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
    private void createTextLayout(entities.HighScores hs){
        Log.d(TAG, "createTextLayout");
        String layoutId = "";
        String textTitleId = "";
        String textScoreId ="";

        //Vertical LinearLayout that works as a container.
        LinearLayout container = findViewById(R.id.online_vLayout);

        //Remove al views inside the main layout before to paint it again.
        container.removeAllViews();

        int i = 0; //Int counter.

        //Obtains the declared attributes of an object. HighScores => TOP1, TOP2... etc.
        Field[] fields = hs.getClass().getDeclaredFields();

        //Goes over every attribute of the object HighScores (hs) and paint it values in the screen.
        for(Field field : fields){
            i++; //this counter will enumerate the scores.
            field.setAccessible(true);
            String value = "";
            int val = 0;
            try{
                val = field.getInt(hs);
                value = String.format("Top " + i + ":\t\t%8d", val);
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
                value = String.valueOf(0);
            }

            if(val > 0){
                LinearLayout layout = newLinearLayout();
                //TextView tile = newTextViewTitle(String.valueOf(i));
                TextView Score = newTextViewScore(value , String.valueOf(i));
                //layout.addView(tile);
                layout.addView(Score);
                container.addView(layout);
            }
        }
    }
    private TextView newTextViewTitle(String tag){
        Log.d(TAG, "newTextViewTitle: " + tag);
        TextView txt = new TextView(context);

        /*
        <TextView
                android:id="@+id/top2_title"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Top"
                android:textAlignment="center" />
         */
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.weight = 1;

        txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        txt.setPadding(0,0,8,0);
        txt.setLayoutParams(params);
        txt.setTag(new HashMap<String, String>().put("Title", tag));
        txt.setText("Top " +tag + ": ");

        return txt;
    }
    private TextView newTextViewScore(String score, String tag){
        Log.d(TAG, "newTextViewScore; " + score + " " + tag);
        TextView txt = new TextView(context);

        /*
          <TextView
                android:id="@+id/top2_score"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_weight="3"
                android:text="Score" />
         */
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.weight = 1;

        txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        txt.setTextSize(28);
        txt.setPadding(256,0,0,0);
        txt.setLayoutParams(params);
        txt.setTag(new HashMap<String, String>().put("Score", tag));
        txt.setText(score);

        return txt;
    }
    private LinearLayout newLinearLayout(){
        /*
            android:id="@+id/top0_layout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:gravity="center"
            android:orientation="horizontal"
         */
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.HORIZONTAL
                );
        layoutParams.setMargins(0,16,0,0);
        layoutParams.gravity = Gravity.CENTER;
        linearLayout.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }
}