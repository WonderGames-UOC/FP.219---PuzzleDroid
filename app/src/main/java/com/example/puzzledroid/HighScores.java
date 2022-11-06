package com.example.puzzledroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dbHelper.SQLiteHelper;
import util.HighScore;

public class HighScores extends AppCompatActivity {
    Button btnExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        btnExit = findViewById(R.id.btn_HS_exit);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitHS();
            }
        });

        LinearLayout linearLayoutHighScores = (LinearLayout) findViewById(R.id.linearLayout_HighScores);
        linearLayoutHighScores.removeAllViews();
        List<HighScore> highScores = new SQLiteHelper(this, "BD1_HighScores",null, 1).return_HS_List();

        if (highScores.size() > 0){
            for (HighScore hs:highScores){
                int _ID = hs.get_ID();
                String user = hs.getUser();
                String date = hs.getDate();
                String time = hs.getTime();
                String pic = hs.getPic();
                String puzzres = hs.getPuzzres();

                String textViewContent = time + " | " + user + " | " + date + " | " + puzzres;

                TextView textViewResults = new TextView(this);
                textViewResults.setText(textViewContent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    textViewResults.setTextSize(24);
                    textViewResults.setGravity(Gravity.CENTER);
                }

                linearLayoutHighScores.addView(textViewResults);

            }

        }else {
            Toast.makeText(this, "No High Scores found", Toast.LENGTH_SHORT).show();
        }

    }

    public void exitHS(){
        Intent i = new Intent(this ,MainActivity.class);
        startActivity(i);
    };



}
