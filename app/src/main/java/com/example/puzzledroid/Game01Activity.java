package com.example.puzzledroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.Toast;

import dbHelper.SQLiteHelper;



public class Game01Activity extends AppCompatActivity {
    SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "BD1_HighScores", null, 1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game01);
        sqLiteHelper.openDB();
        sqLiteHelper.insertRow("Dev", "12/2/22", "00:17", "image1",32);
        sqLiteHelper.closeDB();
        Toast.makeText(this, "Closed DB", Toast.LENGTH_SHORT).show();
    }
}