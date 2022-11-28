package com.example.puzzledroid;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import gameMechanics.CalendarData;
import util.HighScore;


public class CalendarScores extends AppCompatActivity {
    Button btnExit;
    CalendarData CalendarData = new CalendarData();
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_scores);

        context = this.getApplicationContext();
        btnExit = findViewById(R.id.btn_RS_exit);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitRS();
            }
        });

        ArrayList<String> recentScores = new ArrayList<>();

        recentScores = CalendarData.getRecentScores(context);
        LinearLayout linearLayoutRecentScores = (LinearLayout) findViewById(R.id.linearLayout_RecentScores);
        linearLayoutRecentScores.removeAllViews();
        if (recentScores.size() > 0){
            for (String rs:recentScores){

                TextView textViewResults = new TextView(this);
                textViewResults.setText(rs);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    textViewResults.setTextSize(24);
                    textViewResults.setGravity(Gravity.CENTER);
                }

                linearLayoutRecentScores.addView(textViewResults);

            }

        }else {
            Toast.makeText(this, "No Recent Scores found", Toast.LENGTH_SHORT).show();
        }
    }



    public void exitRS(){
        Intent i = new Intent(this ,MainActivity.class);
        startActivity(i);
    }
}
